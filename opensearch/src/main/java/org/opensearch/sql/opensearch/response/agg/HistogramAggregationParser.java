/*
 * SPDX-License-Identifier: Apache-2.0
 *
 *  The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 *
 */

package org.opensearch.sql.opensearch.response.agg;

import com.google.common.collect.ImmutableList;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opensearch.search.aggregations.Aggregation;
import org.opensearch.search.aggregations.Aggregations;
import org.opensearch.search.aggregations.bucket.histogram.InternalDateHistogram;

public class HistogramAggregationParser implements OpenSearchAggregationResponseParser {
  private final MetricParserHelper metricsParser;

  public HistogramAggregationParser(MetricParser metricParser) {
    this.metricsParser = new MetricParserHelper(Collections.singletonList(metricParser));
  }

  @Override
  public List<Map<String, Object>> parse(Aggregations aggregations) {
    ImmutableList.Builder<Map<String, Object>> list = ImmutableList.builder();
    aggregations.asList().forEach(aggregation -> {
      if (aggregation instanceof InternalDateHistogram) {
        list.addAll(parseInternalDateHistogram((InternalDateHistogram) aggregation));
      }
    });
    return list.build();
  }

  private List<Map<String, Object>> parseInternalDateHistogram(InternalDateHistogram histogram) {
    ImmutableList.Builder<Map<String, Object>> mapList = ImmutableList.builder();
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    histogram.getBuckets().forEach(bucket -> {
      Map<String, Object> map = new HashMap<>();
      map.put(histogram.getName(), bucket.getKey().toString());
      Aggregation aggregation = bucket.getAggregations().asList().get(0);
      Map<String, Object> metricsAggMap = metricsParser.parse(bucket.getAggregations());
      map.put(aggregation.getName(), metricsAggMap.get(aggregation.getName()));
      mapList.add(map);
    });
    return mapList.build();
  }
}
