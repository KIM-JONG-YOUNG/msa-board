package edu.jong.msa.board.domain.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.apache.commons.lang3.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.StringPath;

public final class QueryExpressionUtils {

	public static BooleanExpression betweenDate(
			DateTimePath<LocalDateTime> column, 
			LocalDate from, LocalDate to) {

		if (from != null && to != null) {
			return column.between(
					LocalDateTime.of(from, LocalTime.of(0, 0, 0)),
					LocalDateTime.of(to, LocalTime.of(23, 59, 59)));
		} else if (from != null ) {
			return column.after(
					LocalDateTime.of(from, LocalTime.of(0, 0, 0)));
		} else if (to != null ) {
			return column.before(
					LocalDateTime.of(to, LocalTime.of(23, 59, 59)));
		} else {
			return null;
		}
	}

	public static BooleanExpression contains(StringPath column, String queryString) {
		return (StringUtils.isBlank(queryString)) ? null : column.contains(queryString);
	}

	public static BooleanExpression equals(StringPath column, String queryString) {
		return (StringUtils.isBlank(queryString)) ? null : column.eq(queryString);
	}

	public static <E extends Enum<E>> BooleanExpression equals(EnumPath<E> column, E queryEnum) {
		return (queryEnum == null) ? null : column.eq(queryEnum);
	}

}
