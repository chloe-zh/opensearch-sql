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

package org.opensearch.sql.planner.logical;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.opensearch.sql.ast.tree.Join;
import org.opensearch.sql.expression.Expression;

@ToString
@EqualsAndHashCode(callSuper = true)
@Getter
public class LogicalJoin extends LogicalPlan {
  private final LogicalRelation left;
  private final LogicalRelation right;
  private final Join.JoinType type;
  private final Expression condition;
  private final Boolean isEquiJoin;

  public LogicalJoin(LogicalRelation left, LogicalRelation right, Join.JoinType type,
                     Expression condition, Boolean isEquiJoin) {
    super(ImmutableList.of());
    this.left = left;
    this.right = right;
    this.type = type;
    this.condition = condition;
    this.isEquiJoin = isEquiJoin;
  }

  @Override
  public <R, C> R accept(LogicalPlanNodeVisitor<R, C> visitor, C context) {
    return visitor.visitJoin(this, context);
  }
}
