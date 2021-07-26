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

package org.opensearch.sql.planner.physical.join;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.opensearch.sql.data.model.ExprTupleValue;
import org.opensearch.sql.data.model.ExprValue;
import org.opensearch.sql.expression.Expression;
import org.opensearch.sql.planner.physical.PhysicalPlan;

@RequiredArgsConstructor
public class NestedLoopsJoin extends JoinOperator {
  private final PhysicalPlan left;
  private final PhysicalPlan right;
  private final Expression condition;

  private final ImmutableList.Builder<ExprValue> leftTableBuilder = ImmutableList.builder();
  private final ImmutableList.Builder<ExprValue> rightTableBuilder = ImmutableList.builder();
  private Iterator<ExprValue> leftIterator;
  private Iterator<ExprValue> rightIterator;
  private ExprValue currentLeft;

  @ToString.Exclude private ExprValue next = null;


  @Override
  public void open() {
    left.open();
    while (left.hasNext()) {
      leftTableBuilder.add(left.next());
    }
    right.open();
    while (right.hasNext()) {
      rightTableBuilder.add(right.next());
    }
    leftIterator = leftTableBuilder.build().iterator();
  }

  @Override
  public void close() {
    left.close();
    right.close();
  }

  @Override
  public boolean hasNext() {
    if (rightIterator != null && rightIterator.hasNext()) {
      while (rightIterator.hasNext()) {
        ExprTupleValue tuple = combineExprTupleValue(currentLeft, rightIterator.next());
        ExprValue conditionValue = condition.valueOf(tuple.bindingTuples());
        if (!(conditionValue.isNull() || conditionValue.isMissing())
            && (conditionValue.booleanValue())) {
          next = tuple;
          return true;
        }
      }
    } else {
      currentLeft = leftIterator.next();
      rightIterator = rightTableBuilder.build().iterator();
      while (rightIterator.hasNext()) {
        ExprTupleValue tuple = combineExprTupleValue(currentLeft, rightIterator.next());
        ExprValue conditionValue = condition.valueOf(tuple.bindingTuples());
        if (!(conditionValue.isNull() || conditionValue.isMissing())
            && (conditionValue.booleanValue())) {
          next = tuple;
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public ExprValue next() {
    return next;
  }

  private ExprTupleValue combineExprTupleValue(ExprValue left, ExprValue right) {
    Map<String, ExprValue> combinedMap = left.tupleValue();
    combinedMap.putAll(right.tupleValue());
    return ExprTupleValue.fromExprValueMap(combinedMap);
  }
}
