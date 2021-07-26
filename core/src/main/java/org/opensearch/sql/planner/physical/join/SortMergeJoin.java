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

import lombok.RequiredArgsConstructor;
import org.opensearch.sql.data.model.ExprValue;
import org.opensearch.sql.expression.Expression;
import org.opensearch.sql.planner.physical.PhysicalPlan;

@RequiredArgsConstructor
public class SortMergeJoin extends JoinOperator {
  private final PhysicalPlan left;
  private final PhysicalPlan right;
  private final Expression condition;

  @Override
  public boolean hasNext() {
    return false;
  }

  @Override
  public ExprValue next() {
    return null;
  }
}
