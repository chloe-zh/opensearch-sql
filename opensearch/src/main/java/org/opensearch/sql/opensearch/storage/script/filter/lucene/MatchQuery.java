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

package org.opensearch.sql.opensearch.storage.script.filter.lucene;

import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import java.util.function.BiFunction;
import org.opensearch.index.query.MatchQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.sql.data.model.ExprValue;
import org.opensearch.sql.data.type.ExprType;
import org.opensearch.sql.expression.NamedArgumentExpression;
import org.opensearch.sql.expression.Expression;
import org.opensearch.sql.expression.FunctionExpression;

public class MatchQuery extends LuceneQuery {
  private final static BiFunction<MatchQueryBuilder, ExprValue, MatchQueryBuilder> analyzer =
      (b, v) -> b.analyzer(v.stringValue());
  private final static BiFunction<MatchQueryBuilder, ExprValue, MatchQueryBuilder> auto_generate_synonyms_phrase =
      (b, v) -> b.autoGenerateSynonymsPhraseQuery(Boolean.parseBoolean(v.stringValue()));
  private final static BiFunction<MatchQueryBuilder, ExprValue, MatchQueryBuilder> fuzziness =
      (b, v) -> b.fuzziness(v.stringValue());
  private final static BiFunction<MatchQueryBuilder, ExprValue, MatchQueryBuilder> max_expansions =
      (b, v) -> b.maxExpansions(Integer.parseInt(v.stringValue()));
  private final static BiFunction<MatchQueryBuilder, ExprValue, MatchQueryBuilder> prefix_length =
      (b, v) -> b.prefixLength(Integer.parseInt(v.stringValue()));
  private final static BiFunction<MatchQueryBuilder, ExprValue, MatchQueryBuilder> fuzzy_transpositions =
      (b, v) -> b.fuzzyTranspositions(Boolean.parseBoolean(v.stringValue()));
  private final static BiFunction<MatchQueryBuilder, ExprValue, MatchQueryBuilder> fuzzy_rewrite =
      (b, v) -> b.fuzzyRewrite(v.stringValue());
  private final static BiFunction<MatchQueryBuilder, ExprValue, MatchQueryBuilder> lenient =
      (b, v) -> b.lenient(Boolean.parseBoolean(v.stringValue()));
  private final static BiFunction<MatchQueryBuilder, ExprValue, MatchQueryBuilder> operator =
      (b, v) -> b.operator(Operator.fromString(v.stringValue()));
  private final static BiFunction<MatchQueryBuilder, ExprValue, MatchQueryBuilder> minimum_should_match =
      (b, v) -> b.minimumShouldMatch(v.stringValue());
  private final static BiFunction<MatchQueryBuilder, ExprValue, MatchQueryBuilder> zero_terms_query =
      (b, v) -> b.zeroTermsQuery(
          org.opensearch.index.search.MatchQuery.ZeroTermsQuery.valueOf(v.stringValue()));


  @Override
  protected QueryBuilder doBuild(String fieldName, ExprType fieldType, ExprValue literal) {
    return QueryBuilders.matchQuery(fieldName, literal.value());
  }

  @Override
  public QueryBuilder build(FunctionExpression func) {
    Iterator<Expression> iterator = func.getArguments().iterator();
    switch (func.getFunctionName().getFunctionName()) {
      case "match":
        NamedArgumentExpression field = (NamedArgumentExpression) iterator.next();
        NamedArgumentExpression query = (NamedArgumentExpression) iterator.next();
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery(
            field.getValue().valueOf(null).stringValue(),
            query.getValue().valueOf(null).stringValue());
        while (iterator.hasNext()) {
          NamedArgumentExpression arg = (NamedArgumentExpression) iterator.next();
          ((BiFunction<MatchQueryBuilder, ExprValue, MatchQueryBuilder>) argAction.get(arg.getArgName()))
              .apply(queryBuilder, arg.getValue().valueOf(null));
        }
        return queryBuilder;

      default:
        throw new IllegalStateException();
    }
  }

  ImmutableMap<Object, Object> argAction =
      ImmutableMap.builder()
          .put("analyzer", analyzer)
          .put("auto_generate_synonyms_phrase", auto_generate_synonyms_phrase)
          .put("fuzziness", fuzziness)
          .put("max_expansions", max_expansions)
          .put("prefix_length", prefix_length)
          .put("fuzzy_transpositions", fuzzy_transpositions)
          .put("fuzzy_rewrite", fuzzy_rewrite)
          .put("lenient", lenient)
          .put("operator", operator)
          .put("minimum_should_match", minimum_should_match)
          .put("zero_terms_query", zero_terms_query)
          .build();

  private enum matchParameters {
    field,
    query,
    analyzer,
    auto_generate_synonyms_phrase,
    fuzziness,
    max_expansions,
    prefix_length,
    fuzzy_transpositions,
    fuzzy_rewrite,
    lenient,
    operator,
    minimum_should_match,
    zero_terms_query
  }


}
