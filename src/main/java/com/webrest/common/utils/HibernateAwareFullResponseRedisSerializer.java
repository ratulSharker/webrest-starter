package com.webrest.common.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.webrest.common.dto.response.Response;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class HibernateAwareFullResponseRedisSerializer implements RedisSerializer<Response<Object>> {

	protected final ObjectMapper objectmapper;

	public HibernateAwareFullResponseRedisSerializer() {
		objectmapper = new ObjectMapper();
		Hibernate5Module hibernate5Module = new Hibernate5Module();
		objectmapper.registerModule(hibernate5Module);
		objectmapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
	}

	@Override
	public byte[] serialize(Response<Object> t) throws SerializationException {
		try {
			return objectmapper.writeValueAsBytes(t);
		} catch(JsonProcessingException ex) {
			throw new SerializationException(ex.getMessage(), ex.getCause());
		}
	}

	@Override
	public Response<Object> deserialize(byte[] bytes) throws SerializationException {
		try {
			return objectmapper.readValue(bytes, new TypeReference<Response<Object>>() {
			});
		} catch (IOException ex) {
			throw new SerializationException(ex.getMessage(), ex.getCause());
		}		
	}

}
