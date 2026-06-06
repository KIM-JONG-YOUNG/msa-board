package com.jong.msaboard.support.infra.utils;

import java.lang.annotation.Annotation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public final class MethodAspectUtils {

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    public static <A extends Annotation> A getAnnotation(JoinPoint joinPoint, Class<A> annotationClass) {
        var methodSignature = (MethodSignature) joinPoint.getSignature();
        var method = methodSignature.getMethod();
        return AnnotationUtils.getAnnotation(method, annotationClass);
    }

    public static Object getParameter(JoinPoint joinPoint, String expression) {
        var methodSignature = (MethodSignature) joinPoint.getSignature();
        var parameterNames = methodSignature.getParameterNames();
        var parameterValues = joinPoint.getArgs();
        var context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], parameterValues[i]);
        }
        return PARSER.parseExpression(expression).getValue(context);
    }

    public static Class<?> getReturnType(JoinPoint joinPoint) {
        var methodSignature = (MethodSignature) joinPoint.getSignature();
        return methodSignature.getReturnType();
    }

}
