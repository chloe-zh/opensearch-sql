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

package org.opensearch.sql.ast.tree;

import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.opensearch.sql.ast.AbstractNodeVisitor;
import org.opensearch.sql.ast.expression.UnresolvedExpression;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Join extends UnresolvedPlan {
  private final Relation left;
  private final Relation right;
  private final JoinType joinType;
  private final UnresolvedExpression joinCondition;
  private final Boolean isEquiJoin;

  @Override
  public UnresolvedPlan attach(UnresolvedPlan child) {
    return this;
  }

  @Override
  public List<UnresolvedPlan> getChild() {
    return ImmutableList.of();
  }

  @Override
  public <T, C> T accept(AbstractNodeVisitor<T, C> nodeVisitor, C context) {
    return nodeVisitor.visitJoin(this, context);
  }

  public enum JoinType {
    CROSS, INNER, LEFT, RIGHT, FULL
  }
}
