package edu.jong.msa.board.common.utils;

import java.util.EnumSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import edu.jong.msa.board.common.exception.ObjectConvertException;
import edu.jong.msa.board.common.type.DBCodeEnum;

public final class ObjectMapperUtils {

	private static final ObjectMapper MAPPER = new ObjectMapper()
			.registerModule(new EnumBindModule())
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	
	public static ObjectMapper getMapper() {
		return MAPPER.copy();
	}
	
	public static String toJson(Object object) {
		try {
			return MAPPER.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new ObjectConvertException(object, "String");
		}
	}

	public static <T> T toObject(String json, Class<T> type) {
		try {
			return MAPPER.readValue(json, type);
		} catch (JsonProcessingException e) {
			throw new ObjectConvertException(json, type.getTypeName());
		}
	}

	public static <T> T toObject(String json, TypeReference<T> type) {
		try {
			return MAPPER.readValue(json, type);
		} catch (JsonProcessingException e) {
			throw new ObjectConvertException(json, type.getType().getTypeName());
		}
	}
	
	public static <E extends Enum<E>> E toEnum(String name, Class<E> enumType) {
		
		String camelCaseName = name.trim()
				.toUpperCase()
				.replace("-", "_");
		
		return EnumSet.allOf(enumType).stream()
				.filter(e -> e.name().equals(camelCaseName))
				.findAny().orElseThrow(() 
						-> new ObjectConvertException(name, enumType.getTypeName()));
	}
	
	public static <E extends Enum<E> & DBCodeEnum<V>, V> E toDBCodeEnum(V code, Class<E> enumType) {
		return EnumSet.allOf(enumType).stream()
				.filter(e -> e.getCode().equals(code))
				.findAny().orElseThrow(() 
						-> new ObjectConvertException(code, enumType.getTypeName()));
	}
	
}
