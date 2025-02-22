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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   or in the "license" file accompanying this file. This file is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *   express or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package org.opensearch.sql.legacy.query.planner.physical.node.project;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.opensearch.sql.legacy.expression.domain.BindingTuple;
import org.opensearch.sql.legacy.query.planner.core.ColumnNode;
import org.opensearch.sql.legacy.query.planner.core.PlanNode;
import org.opensearch.sql.legacy.query.planner.physical.PhysicalOperator;
import org.opensearch.sql.legacy.query.planner.physical.Row;
import org.opensearch.sql.legacy.query.planner.physical.estimation.Cost;
import org.opensearch.sql.legacy.query.planner.physical.node.scroll.BindingTupleRow;

/**
 * The definition of Project Operator.
 */
@RequiredArgsConstructor
public class PhysicalProject implements PhysicalOperator<BindingTuple> {
    private final PhysicalOperator<BindingTuple> next;
    private final List<ColumnNode> fields;

    @Override
    public Cost estimate() {
        return null;
    }

    @Override
    public PlanNode[] children() {
        return new PlanNode[]{next};
    }

    @Override
    public boolean hasNext() {
        return next.hasNext();
    }

    @Override
    public Row<BindingTuple> next() {
        BindingTuple input = next.next().data();
        BindingTuple.BindingTupleBuilder outputBindingTupleBuilder = BindingTuple.builder();
        fields.forEach(field -> outputBindingTupleBuilder.binding(field.getName(), field.getExpr().valueOf(input)));
        return new BindingTupleRow(outputBindingTupleBuilder.build());
    }
}
