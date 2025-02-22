# SPDX-License-Identifier: Apache-2.0
#
# The OpenSearch Contributors require contributions made to
# this file be licensed under the Apache-2.0 license or a
# compatible open source license.
#
# Modifications Copyright OpenSearch Contributors. See
# GitHub history for details.

#
#   Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
#   Licensed under the Apache License, Version 2.0 (the "License").
#   You may not use this file except in compliance with the License.
#   A copy of the License is located at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#   or in the "license" file accompanying this file. This file is distributed
#   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
#   express or implied. See the License for the specific language governing
#   permissions and limitations under the License.
#

project(ut_conn)

# Source, headers, and include dirs
set(SOURCE_FILES test_conn.cpp test_query_execution.cpp)
include_directories(	${UT_HELPER}
						${OPENSEARCHODBC_SRC}
						${RAPIDJSON_SRC} 
						${RABBIT_SRC}		
						${LIBCURL_SRC}
						${VLD_SRC} )

# Generate executable
add_executable(ut_conn ${SOURCE_FILES})

# Library dependencies
target_link_libraries(ut_conn sqlodbc ut_helper gtest_main)
target_compile_definitions(ut_conn PUBLIC _UNICODE UNICODE)
