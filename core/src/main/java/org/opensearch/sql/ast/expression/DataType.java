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
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   or in the "license" file accompanying this file. This file is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *   express or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package org.opensearch.sql.ast.expression;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.opensearch.sql.data.type.ExprCoreType;

/**
 * The DataType defintion in AST.
 * Question, could we use {@link ExprCoreType} directly in AST?
 */
@RequiredArgsConstructor
public enum DataType {
  TYPE_ERROR(ExprCoreType.UNKNOWN),
  NULL(ExprCoreType.UNDEFINED),

  INTEGER(ExprCoreType.INTEGER),
  LONG(ExprCoreType.LONG),
  DOUBLE(ExprCoreType.DOUBLE),
  STRING(ExprCoreType.STRING),
  BOOLEAN(ExprCoreType.BOOLEAN),

  DATE(ExprCoreType.DATE),
  TIME(ExprCoreType.TIME),
  TIMESTAMP(ExprCoreType.TIMESTAMP),
  INTERVAL(ExprCoreType.INTERVAL);

  @Getter
  private final ExprCoreType coreType;
}
