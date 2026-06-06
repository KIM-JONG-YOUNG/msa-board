package com.jong.msaboard.support.infra.condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ConditionalOnClass(name = "com.querydsl.jpa.impl.JPAQueryFactory")
public @interface ConditionalOnQueryDsl {}
