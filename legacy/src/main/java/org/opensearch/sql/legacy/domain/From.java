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


/**
 * Represents the from clause.
 * Contains index and type which the
 * query refer to.
 */
public class From {
    private String index;
    private String type;
    private String alias;

    /**
     * Extract index and type from the 'from' string
     *
     * @param from The part after the FROM keyword.
     */
    public From(String from) {
        if (from.startsWith("<")) {
            index = from;
            if (!from.endsWith(">")) {
                int i = from.lastIndexOf('/');
                if (-1 < i) {
                    index = from.substring(0, i);
                    type = from.substring(i + 1);
                }
            }
            return;
        }
        String[] parts = from.split("/");
        this.index = parts[0].trim();
        if (parts.length == 2) {
            this.type = parts[1].trim();
        }
    }

    public From(String from, String alias) {
        this(from);
        this.alias = alias;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(index);
        if (type != null) {
            str.append('/').append(type);
        }
        if (alias != null) {
            str.append(" AS ").append(alias);
        }
        return str.toString();
    }
}
