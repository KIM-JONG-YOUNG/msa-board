package edu.jong.msa.board.common.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import edu.jong.msa.board.common.type.DBCodeEnum.State;

public class EnumBindModule extends SimpleModule {

	private static final long serialVersionUID = 1L;

	public EnumBindModule() {
		super();
		
		this.addSerializer(new EnumSerializer<>(State.class));
		
		this.addDeserializer(State.class, new EnumDeserializer<>(State.class));
	}
	
	public static class EnumSerializer<E extends Enum<E>> extends StdSerializer<E> {

		private static final long serialVersionUID = 1L;

		protected EnumSerializer(Class<E> enumType) {
			super(enumType);
		}

		@Override
		public void serialize(E value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			gen.writeString(value.name());
		}
	}

	public static class EnumDeserializer<E extends Enum<E>> extends StdDeserializer<E> {

		private static final long serialVersionUID = 1L;

		private final Class<E> enumType;
		
		protected EnumDeserializer(Class<E> enumType) {
			super(enumType);
			this.enumType = enumType;
		}

		@Override
		public E deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
			return ObjectMapperUtils.toEnum(p.getText(), this.enumType);
		}
	}
}
