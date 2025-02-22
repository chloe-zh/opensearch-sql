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

import org.opensearch.sql.data.type.ExprCoreType;
import org.opensearch.sql.data.type.ExprType;

/**
 * Expression Byte Value.
 */
public class ExprByteValue extends AbstractExprNumberValue {

  public ExprByteValue(Number value) {
    super(value);
  }

  @Override
  public int compare(ExprValue other) {
    return Byte.compare(byteValue(), other.byteValue());
  }

  @Override
  public boolean equal(ExprValue other) {
    return byteValue().equals(other.byteValue());
  }

  @Override
  public Object value() {
    return byteValue();
  }

  @Override
  public ExprType type() {
    return ExprCoreType.BYTE;
  }

  @Override
  public String toString() {
    return value().toString();
  }
}
