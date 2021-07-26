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

package org.opensearch.sql.planner;

import static com.google.common.base.Strings.isNullOrEmpty;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.opensearch.sql.ast.tree.Join;
import org.opensearch.sql.expression.Expression;
import org.opensearch.sql.planner.logical.LogicalJoin;
import org.opensearch.sql.planner.logical.LogicalPlan;
import org.opensearch.sql.planner.logical.LogicalPlanNodeVisitor;
import org.opensearch.sql.planner.logical.LogicalRelation;
import org.opensearch.sql.planner.optimizer.LogicalPlanOptimizer;
import org.opensearch.sql.planner.physical.PhysicalPlan;
import org.opensearch.sql.planner.physical.join.HashJoin;
import org.opensearch.sql.planner.physical.join.NestedLoopsJoin;
import org.opensearch.sql.planner.physical.join.SortMergeJoin;
import org.opensearch.sql.storage.StorageEngine;
import org.opensearch.sql.storage.Table;

@RequiredArgsConstructor
public class JoinPlanner<C> extends DefaultImplementor<C> {
  /**
   * Storage engine.
   */
  private final StorageEngine storageEngine;

  private final LogicalPlanOptimizer logicalOptimizer;

  @Override
  public PhysicalPlan visitJoin(LogicalJoin node, C context) {
    PhysicalPlan left = plan(node.getLeft());
    PhysicalPlan right = plan(node.getRight());
    Expression condition = node.getCondition();
//    if (node.getType().equals(Join.JoinType.CROSS)) {
//      if (!node.getIsEquiJoin()) {
//        return new NestedLoopsJoin(left, right, condition);
//      } else {
//        // work estimation should be performed here before determining the hash type
//        return new HashJoin(left, right, condition);
//      }
//    } else {
//      return new SortMergeJoin(left, right, condition);
//    }
    return new NestedLoopsJoin(left, right, condition);
  }

  private PhysicalPlan plan(LogicalPlan plan) {
    String tableName = findTableName(plan);
    if (isNullOrEmpty(tableName)) {
      return plan.accept(new DefaultImplementor<>(), null);
    }

    Table table = storageEngine.getTable(tableName);
    return table.implement(
        table.optimize(optimize(plan)));
  }

  private String findTableName(LogicalPlan plan) {
    return plan.accept(new LogicalPlanNodeVisitor<String, Object>() {

      @Override
      public String visitNode(LogicalPlan node, Object context) {
        List<LogicalPlan> children = node.getChild();
        if (children.isEmpty()) {
          return "";
        }
        return children.get(0).accept(this, context);
      }

      @Override
      public String visitRelation(LogicalRelation node, Object context) {
        return node.getRelationName();
      }
    }, null);
  }

  private LogicalPlan optimize(LogicalPlan plan) {
    return logicalOptimizer.optimize(plan);
  }
}
