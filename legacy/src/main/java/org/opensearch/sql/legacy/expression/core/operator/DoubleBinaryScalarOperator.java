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

package org.opensearch.sql.legacy.expression.core.operator;

import static org.opensearch.sql.legacy.expression.model.ExprValueUtils.getDoubleValue;

import java.util.List;
import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;
import org.opensearch.sql.legacy.expression.model.ExprDoubleValue;
import org.opensearch.sql.legacy.expression.model.ExprValue;
import org.opensearch.sql.legacy.expression.model.ExprValueFactory;

/**
 * Double Binary Scalar Operator take two {@link ExprValue} which have double value as arguments ans return one
 * {@link ExprDoubleValue} as result.
 */
@RequiredArgsConstructor
public class DoubleBinaryScalarOperator implements ScalarOperator {
    private final ScalarOperation op;
    private final BiFunction<Double, Double, Double> doubleFunc;

    @Override
    public ExprValue apply(List<ExprValue> exprValues) {
        ExprValue exprValue1 = exprValues.get(0);
        ExprValue exprValue2 = exprValues.get(1);
        if (exprValue1.kind() != exprValue2.kind()) {
            throw new RuntimeException(String.format("unexpected operation type: %s(%s,%s)", op.name(),
                                                     exprValue1.kind(), exprValue2.kind()));
        }
        switch (exprValue1.kind()) {
            case DOUBLE_VALUE:
            case INTEGER_VALUE:
            case LONG_VALUE:
            case FLOAT_VALUE:
                return ExprValueFactory.from(doubleFunc.apply(getDoubleValue(exprValue1),
                                                              getDoubleValue(exprValue2)));
            default:
                throw new RuntimeException(String.format("unexpected operation type: %s(%s,%s)", op.name(),
                                                         exprValue1.kind(), exprValue2.kind()));
        }
    }

    @Override
    public String name() {
        return op.name();
    }
}
