package edu.jong.msa.board.micro.validator;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public interface ParamValidator<Param> {

	public interface ParamValidGroup {}
	
	public <T extends ParamValidGroup> void validate(Param param, Class<T> validGroup);

	default boolean isBlank(String value) {
		return StringUtils.isBlank(value);
	}
	
	default boolean isOver(String value, int max) {
		return (value.length() > max);
	}

	default boolean isUnder(String value, int min) {
		return (value.length() < min);
	}

	default boolean isNotMatch(String value, String regex) {
		return !Pattern.matches(regex, value);
	}

}
