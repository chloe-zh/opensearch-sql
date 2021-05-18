/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

/*
 *   Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License").
 *   You may not use this file except in compliance with the License.
 *   A copy of the License is located at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   or in the "license" file accompanying this file. This file is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *   express or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package org.opensearch.sql.legacy.executor;

import static org.opensearch.sql.legacy.plugin.SqlSettings.QUERY_SLOWLOG;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.OpenSearchException;
import org.opensearch.client.Client;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestChannel;
import org.opensearch.rest.RestStatus;
import org.opensearch.sql.legacy.esdomain.LocalClusterState;
import org.opensearch.sql.legacy.exception.SqlParseException;
import org.opensearch.sql.legacy.metrics.MetricName;
import org.opensearch.sql.legacy.metrics.Metrics;
import org.opensearch.sql.legacy.query.QueryAction;
import org.opensearch.sql.legacy.query.join.BackOffRetryStrategy;
import org.opensearch.sql.legacy.utils.LogUtils;
import org.opensearch.threadpool.ThreadPool;
import org.opensearch.transport.Transports;

/**
 * A RestExecutor wrapper to execute request asynchronously to avoid blocking transport thread.
 */
public class AsyncRestExecutor implements RestExecutor {

    /**
     * Custom thread pool name managed by OpenSearch
     */
    public static final String SQL_WORKER_THREAD_POOL_NAME = "sql-worker";

    private static final Logger LOG = LogManager.getLogger(AsyncRestExecutor.class);

    /**
     * Treat all actions as blocking which means async all actions,
     * ex. execute() in csv executor or pretty format executor
     */
    private static final Predicate<QueryAction> ALL_ACTION_IS_BLOCKING = anyAction -> true;

    /**
     * Delegated rest executor to async
     */
    private final RestExecutor executor;

    /**
     * Request type that expect to async to avoid blocking
     */
    private final Predicate<QueryAction> isBlocking;


    AsyncRestExecutor(RestExecutor executor) {
        this(executor, ALL_ACTION_IS_BLOCKING);
    }

    AsyncRestExecutor(RestExecutor executor, Predicate<QueryAction> isBlocking) {
        this.executor = executor;
        this.isBlocking = isBlocking;
    }

    @Override
    public void execute(Client client, Map<String, String> params, QueryAction queryAction, RestChannel channel)
            throws Exception {
        if (isBlockingAction(queryAction) && isRunningInTransportThread()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("[{}] Async blocking query action [{}] for executor [{}] in current thread [{}]",
                        LogUtils.getRequestId(), name(executor), name(queryAction), Thread.currentThread().getName());
            }
            async(client, params, queryAction, channel);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("[{}] Continue running query action [{}] for executor [{}] in current thread [{}]",
                        LogUtils.getRequestId(), name(executor), name(queryAction), Thread.currentThread().getName());
            }
            doExecuteWithTimeMeasured(client, params, queryAction, channel);
        }
    }

    @Override
    public String execute(Client client, Map<String, String> params, QueryAction queryAction) throws Exception {
        // Result is always required and no easy way to async it here.
        return executor.execute(client, params, queryAction);
    }

    private boolean isBlockingAction(QueryAction queryAction) {
        return isBlocking.test(queryAction);
    }

    private boolean isRunningInTransportThread() {
        return Transports.isTransportThread(Thread.currentThread());
    }

    /**
     * Run given task in thread pool asynchronously
     */
    private void async(Client client, Map<String, String> params, QueryAction queryAction, RestChannel channel) {

        ThreadPool threadPool = client.threadPool();
        Runnable runnable = () -> {
            try {
                doExecuteWithTimeMeasured(client, params, queryAction, channel);
            } catch (IOException | SqlParseException | OpenSearchException e) {
                Metrics.getInstance().getNumericalMetric(MetricName.FAILED_REQ_COUNT_SYS).increment();
                LOG.warn("[{}] [MCB] async task got an IO/SQL exception: {}", LogUtils.getRequestId(),
                        e.getMessage());
                channel.sendResponse(new BytesRestResponse(RestStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
            } catch (IllegalStateException e) {
                Metrics.getInstance().getNumericalMetric(MetricName.FAILED_REQ_COUNT_SYS).increment();
                LOG.warn("[{}] [MCB] async task got a runtime exception: {}", LogUtils.getRequestId(),
                        e.getMessage());
                channel.sendResponse(new BytesRestResponse(RestStatus.INSUFFICIENT_STORAGE,
                        "Memory circuit is broken."));
            } catch (Throwable t) {
                Metrics.getInstance().getNumericalMetric(MetricName.FAILED_REQ_COUNT_SYS).increment();
                LOG.warn("[{}] [MCB] async task got an unknown throwable: {}", LogUtils.getRequestId(),
                        t.getMessage());
                channel.sendResponse(new BytesRestResponse(RestStatus.INTERNAL_SERVER_ERROR,
                        String.valueOf(t.getMessage())));
            } finally {
                BackOffRetryStrategy.releaseMem(executor);
            }
        };

        // Preserve context of calling thread to ensure headers of requests are forwarded when running blocking actions
        threadPool.schedule(
                LogUtils.withCurrentContext(runnable),
                new TimeValue(0L),
                SQL_WORKER_THREAD_POOL_NAME
        );
    }

    /**
     * Time the real execution of Executor and log slow query for troubleshooting
     */
    private void doExecuteWithTimeMeasured(Client client,
                                           Map<String, String> params,
                                           QueryAction action,
                                           RestChannel channel) throws Exception {
        long startTime = System.nanoTime();
        try {
            executor.execute(client, params, action, channel);
        } finally {
            Duration elapsed = Duration.ofNanos(System.nanoTime() - startTime);
            int slowLogThreshold = LocalClusterState.state().getSettingValue(QUERY_SLOWLOG);
            if (elapsed.getSeconds() >= slowLogThreshold) {
                LOG.warn("[{}] Slow query: elapsed={} (ms)", LogUtils.getRequestId(), elapsed.toMillis());
            }
        }
    }

    private String name(Object object) {
        return object.getClass().getSimpleName();
    }
}