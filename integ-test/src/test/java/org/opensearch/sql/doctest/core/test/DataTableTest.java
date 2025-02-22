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

package org.opensearch.sql.doctest.core.test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.opensearch.sql.doctest.core.response.DataTable;

/**
 * Test cases for {@link DataTable}
 */
public class DataTableTest {

  @Test
  public void testSingleColumnTable() {
    DataTable table = new DataTable(new Object[] {"Test Table"});
    table.addRow(new Object[] {"this is a very long line"});

    assertThat(
        table.toString(),
        is(
            "+------------------------+\n" +
                "|              Test Table|\n" +
                "+========================+\n" +
                "|this is a very long line|\n" +
                "+------------------------+\n"
        )
    );
  }

  @Test
  public void testTwoColumnsTable() {
    DataTable table = new DataTable(new Object[] {"Test Table", "Very Long Title"});
    table.addRow(new Object[] {"this is a very long line", "short"});

    assertThat(
        table.toString(),
        is(
            "+------------------------+---------------+\n" +
                "|              Test Table|Very Long Title|\n" +
                "+========================+===============+\n" +
                "|this is a very long line|          short|\n" +
                "+------------------------+---------------+\n"
        )
    );
  }

}
