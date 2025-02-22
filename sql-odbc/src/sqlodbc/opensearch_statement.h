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
 * Copyright <2019> Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

#ifndef _OPENSEARCH_STATEMENT_H_
#define _OPENSEARCH_STATEMENT_H_

#include "opensearch_parse_result.h"
#include "qresult.h"
#include "statement.h"

#ifdef __cplusplus
extern "C" {
#endif
RETCODE RePrepareStatement(StatementClass *stmt);
RETCODE PrepareStatement(StatementClass* stmt, const SQLCHAR *stmt_str, SQLINTEGER stmt_sz);
RETCODE ExecuteStatement(StatementClass *stmt, BOOL commit);
QResultClass *SendQueryGetResult(StatementClass *stmt, BOOL commit);
RETCODE AssignResult(StatementClass *stmt);
SQLRETURN OPENSEARCHAPI_Cancel(HSTMT hstmt);
SQLRETURN GetNextResultSet(StatementClass *stmt);
void ClearOpenSearchResult(void *opensearch_result);
#ifdef __cplusplus
}
#endif

#endif
