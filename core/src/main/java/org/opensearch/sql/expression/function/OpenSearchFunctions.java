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

package org.opensearch.sql.expression.function;

import static org.opensearch.sql.data.type.ExprCoreType.STRING;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.opensearch.sql.data.model.ExprValue;
import org.opensearch.sql.data.type.ExprCoreType;
import org.opensearch.sql.data.type.ExprType;
import org.opensearch.sql.expression.Expression;
import org.opensearch.sql.expression.FunctionExpression;
import org.opensearch.sql.expression.env.Environment;

@UtilityClass
public class OpenSearchFunctions {
  public void register(BuiltinFunctionRepository repository) {
    repository.register(match());
  }

  private static FunctionResolver match() {
    FunctionName funcName = new FunctionName("match");
    return new FunctionResolver(funcName,
        ImmutableMap.<FunctionSignature, FunctionBuilder>builder()
            .put(new FunctionSignature(funcName, ImmutableList.of(STRING, STRING)),
                args -> new OpenSearchFunction(funcName, args))
            .put(new FunctionSignature(funcName, ImmutableList.of(STRING, STRING, STRING)),
                args -> new OpenSearchFunction(funcName, args))
            .put(new FunctionSignature(funcName, ImmutableList.of(STRING, STRING, STRING, STRING)),
                args -> new OpenSearchFunction(funcName, args))
            .put(new FunctionSignature(funcName, ImmutableList.of(STRING, STRING, STRING, STRING, STRING)),
                args -> new OpenSearchFunction(funcName, args))
            .put(new FunctionSignature(funcName, ImmutableList.of(STRING, STRING, STRING, STRING, STRING, STRING)),
                args -> new OpenSearchFunction(funcName, args))
            .put(new FunctionSignature(funcName, ImmutableList.of(STRING, STRING, STRING, STRING, STRING, STRING, STRING)),
                args -> new OpenSearchFunction(funcName, args))
            .put(new FunctionSignature(funcName, ImmutableList.of(STRING, STRING, STRING, STRING, STRING, STRING, STRING, STRING)),
                args -> new OpenSearchFunction(funcName, args))
            .build());
  }

  private static class OpenSearchFunction extends FunctionExpression {

    public OpenSearchFunction(FunctionName functionName, List<Expression> arguments) {
      super(functionName, arguments);
    }

    @Override
    public ExprValue valueOf(Environment<Expression, ExprValue> valueEnv) {
      throw new UnsupportedOperationException(
          "OpenSearch defined function cannot run without OpenSearch DSL support");
    }

    @Override
    public ExprType type() {
      return ExprCoreType.BOOLEAN;
    }
  }

}
