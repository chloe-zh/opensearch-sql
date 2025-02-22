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

package org.opensearch.sql.legacy.domain;

import java.util.LinkedList;

public class Where implements Cloneable {

    public enum CONN {
        AND, OR;

        public CONN negative() {
            return this == AND ? OR : AND;
        }
    }

    public static Where newInstance() {
        return new Where(CONN.AND);
    }

    private LinkedList<Where> wheres = new LinkedList<>();

    protected CONN conn;

    public Where(String connStr) {
        this.conn = CONN.valueOf(connStr.toUpperCase());
    }

    public Where(CONN conn) {
        this.conn = conn;
    }

    public void addWhere(Where where) {
        wheres.add(where);
    }

    public CONN getConn() {
        return this.conn;
    }

    public void setConn(CONN conn) {
        this.conn = conn;
    }

    public LinkedList<Where> getWheres() {
        return wheres;
    }

    @Override
    public String toString() {
        if (wheres.size() > 0) {
            String whereStr = wheres.toString();
            return this.conn + " ( " + whereStr.substring(1, whereStr.length() - 1) + " ) ";
        } else {
            return "";
        }

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Where clonedWhere = new Where(this.getConn());
        for (Where innerWhere : this.getWheres()) {
            clonedWhere.addWhere((Where) innerWhere.clone());
        }
        return clonedWhere;
    }
}
