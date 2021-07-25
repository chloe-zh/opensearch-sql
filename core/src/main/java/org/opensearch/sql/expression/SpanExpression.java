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

package org.opensearch.sql.expression;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.opensearch.sql.data.model.ExprValue;
import org.opensearch.sql.data.type.ExprType;
import org.opensearch.sql.expression.env.Environment;

@RequiredArgsConstructor
@Getter
public class SpanExpression implements Expression {
  private final Expression field;
  private final Expression span;

  @Override
  public ExprValue valueOf(Environment<Expression, ExprValue> valueEnv) {
    return span.valueOf(valueEnv);
  }

  @Override
  public ExprType type() {
    return field.type();
  }

  @Override
  public <T, C> T accept(ExpressionNodeVisitor<T, C> visitor, C context) {
    return null;
  }
}
