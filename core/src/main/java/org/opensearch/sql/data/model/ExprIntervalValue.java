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
 *   Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package org.opensearch.sql.data.model;

import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import lombok.RequiredArgsConstructor;
import org.opensearch.sql.data.type.ExprCoreType;
import org.opensearch.sql.data.type.ExprType;
import org.opensearch.sql.exception.ExpressionEvaluationException;

@RequiredArgsConstructor
public class ExprIntervalValue extends AbstractExprValue {
  private final TemporalAmount interval;

  @Override
  public TemporalAmount intervalValue() {
    return interval;
  }

  @Override
  public int compare(ExprValue other) {
    TemporalAmount otherInterval = other.intervalValue();
    if (!interval.getClass().equals(other.intervalValue().getClass())) {
      throw new ExpressionEvaluationException(
          String.format("invalid to compare intervals with units %s and %s",
              unit(), ((ExprIntervalValue) other).unit()));
    }
    return Long.compare(
        interval.get(unit()), otherInterval.get(((ExprIntervalValue) other).unit()));
  }

  @Override
  public boolean equal(ExprValue other) {
    return interval.equals(other.intervalValue());
  }

  @Override
  public TemporalAmount value() {
    return interval;
  }

  @Override
  public ExprType type() {
    return ExprCoreType.INTERVAL;
  }

  /**
   * Util method to get temporal unit stored locally.
   */
  public TemporalUnit unit() {
    return interval.getUnits()
        .stream()
        .filter(v -> interval.get(v) != 0)
        .findAny()
        .orElse(interval.getUnits().get(0));
  }
}
