package com.jong.msa.board.common.utils;

import java.util.Map;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public final class SpELUtils {

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    public static <T> T getValue(String expression, Map<String, Object> variables) {
        EvaluationContext context = new StandardEvaluationContext();
        variables.forEach(context::setVariable);
        return (T) PARSER.parseExpression(expression).getValue(context);
    }

}
