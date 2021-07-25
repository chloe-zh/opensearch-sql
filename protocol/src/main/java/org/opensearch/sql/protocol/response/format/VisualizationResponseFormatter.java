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

package org.opensearch.sql.protocol.response.format;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.opensearch.sql.common.antlr.SyntaxCheckException;
import org.opensearch.sql.data.type.ExprType;
import org.opensearch.sql.exception.QueryEngineException;
import org.opensearch.sql.executor.ExecutionEngine;
import org.opensearch.sql.opensearch.response.error.ErrorMessage;
import org.opensearch.sql.opensearch.response.error.ErrorMessageFactory;
import org.opensearch.sql.protocol.response.QueryResult;

public class VisualizationResponseFormatter extends JsonResponseFormatter<QueryResult> {
  public VisualizationResponseFormatter() {
    super(Style.PRETTY);
  }

  /**

   {
     "data": [
        "count()": [10, 12, 7, 3],
        "city": ["Seattle", "Palo Alto", "Los Angelos", "Portland"]
     ],
     "metadata": {
        "x": {
          "name": "city",
          "type": "text"
        },
        "y": {
          "name": "count()",
          "type": "integer"
        }
     },
     "size": 4,
     "status": 200
   }

   */

  @Override
  protected Object buildJsonObject(QueryResult response) {
    return VisualizationResponse.builder()
        .data(fetchData(response))
        .metadata(fetchMetadata(response))
        .size(response.size())
        .status(200)
        .build();
  }

  @Override
  public String format(Throwable t) {
    int status = getStatus(t);
    ErrorMessage message = ErrorMessageFactory.createErrorMessage(t, status);
    VisualizationResponseFormatter.Error error = new Error(
        message.getType(),
        message.getReason(),
        message.getDetails());
    return jsonify(new VisualizationErrorResponse(error, status));
  }

  private int getStatus(Throwable t) {
    return (t instanceof SyntaxCheckException
        || t instanceof QueryEngineException) ? 400 : 503;
  }

  private Map<String, List<Object>> fetchData(QueryResult response) {
    Map<String, List<Object>> columnMap = new LinkedHashMap<>();
    response.getSchema().getColumns()
        .forEach(column -> columnMap.put(column.getName(), new LinkedList<>()));

    for (Object[] dataRow : response) {
      int column = 0;
      for (Map.Entry<String, List<Object>> entry : columnMap.entrySet()) {
        List<Object> dataColumn = entry.getValue();
        dataColumn.add(dataRow[column++]);
      }
    }

    return columnMap;
  }

  private Metadata fetchMetadata(QueryResult response) {
    List<ExecutionEngine.Schema.Column> columns = response.getSchema().getColumns();
    if (columns.size() == 2) {
      return fetchVisualizeMeta(columns);
    }
    List<FieldInfo> fields = new LinkedList<>();
    columns.forEach(column -> {
      FieldInfo field = new FieldInfo(column.getName(), convertToLegacyType(column.getExprType()));
      fields.add(field);
    });
    return new Metadata(null, null, fields);
  }

  private Metadata fetchVisualizeMeta(List<ExecutionEngine.Schema.Column> columns) {
    FieldInfo yfield = new FieldInfo(columns.get(0).getName(),
        convertToLegacyType(columns.get(0).getExprType()));
    FieldInfo xfield = new FieldInfo(columns.get(1).getName(),
        convertToLegacyType(columns.get(1).getExprType()));
    return new Metadata(xfield, yfield, Arrays.asList(yfield, xfield));
  }

  /**
   * Convert type that exists in both legacy and new engine but has different name.
   * Return old type name to avoid breaking impact on client-side.
   */
  private String convertToLegacyType(ExprType type) {
    return type.legacyTypeName().toLowerCase();
  }

  @RequiredArgsConstructor
  @Getter
  public static class VisualizationErrorResponse {
    private final Error error;
    private final int status;
  }

  @RequiredArgsConstructor
  @Getter
  public static class Error {
    private final String type;
    private final String reason;
    private final String details;
  }

  @Builder
  @Getter
  public static class VisualizationResponse {
    private final Map<String, List<Object>> data;
    private final Metadata metadata;
    private final long size;
    private final int status;
  }

  @RequiredArgsConstructor
  public static class Metadata {
    private final FieldInfo xfield;
    private final FieldInfo yfield;
    private final List<FieldInfo> fields;
  }

  @RequiredArgsConstructor
  public static class FieldInfo {
    private final String name;
    private final String type;
  }
}
