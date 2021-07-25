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

package org.opensearch.sql.opensearch.storage.script.aggregation.dsl;

import org.opensearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.opensearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.opensearch.sql.expression.NamedExpression;
import org.opensearch.sql.expression.SpanExpression;
import org.opensearch.sql.opensearch.storage.serialization.ExpressionSerializer;

public class HistogramAggregationBuilder {
  private final AggregationBuilderHelper<DateHistogramAggregationBuilder> helper;

  public HistogramAggregationBuilder(ExpressionSerializer serializer) {
    this.helper = new AggregationBuilderHelper<>(serializer);
  }

  public DateHistogramAggregationBuilder build(NamedExpression namedExpression) {
    SpanExpression spanExpr = (SpanExpression) namedExpression.getDelegated();
    String intervalExpr = unquote(spanExpr.getSpan().toString());
    return makeBuilder(namedExpression.getName(), spanExpr.getField().toString(), intervalExpr,
        intervalType(intervalExpr));
  }

  private DateHistogramAggregationBuilder makeBuilder(
      String name, String field, String intervalExpr, IntervalType type) {
    if (type.equals(IntervalType.FIXED_INTERVAL)) {
      return new DateHistogramAggregationBuilder(name)
          .field(field)
          .fixedInterval(interval(intervalExpr));
    } else {
      return new DateHistogramAggregationBuilder(name)
          .field(field)
          .calendarInterval(interval(intervalExpr));
    }
  }

  private DateHistogramInterval interval(String expr) {
    return new DateHistogramInterval(expr);
  }

  private String unquote(String text) {
    if (text.startsWith("'") || text.startsWith("\"")) {
      return text.substring(1, text.length() - 1);
    }
    return text;
  }

  enum IntervalType {
    CALENDAR_INTERVAL,
    FIXED_INTERVAL
  }

  private IntervalType intervalType(String expr) {
    switch (expr.charAt(expr.length() - 1)) {
      case 's':
      case 'm':
      case 'h':
      case 'd':
        return IntervalType.FIXED_INTERVAL;
      case 'w':
      case 'M':
      case 'q':
      case 'y':
        return IntervalType.CALENDAR_INTERVAL;
      default:
        throw new IllegalStateException("Unable to recognize interval type");
    }
  }

//  private String extractUnit(Interval interval) {
//    switch (interval.getUnit().toString()) {
//      case "microsecond":
//        return "ms";
//      case "second":
//        return "s";
//      case "minute":
//        return "m";
//      case "hour":
//        return "h";
//      case "day":
//        return "d";
//      case "week":
//        return "w";
//      case "month":
//        return "M";
//      case "quarter":
//        return "q";
//      case "year":
//        return "y";
//      default:
//        throw new ExpressionEvaluationException(String.format(
//            "%s is not acceptable as span unit.", interval.getUnit()));
//    }
//  }
}
