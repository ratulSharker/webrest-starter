package com.webrest.common.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.webrest.common.dto.response.Response;

import org.springframework.data.redis.serializer.SerializationException;

public class HibernateAwareResponseDataRedisSerializer extends HibernateAwareFullResponseRedisSerializer {
	
	@Override
	public byte[] serialize(Response<Object> t) throws SerializationException {
		try {
			return objectmapper.writeValueAsBytes(t.getData());
		} catch(JsonProcessingException ex) {
			throw new SerializationException(ex.getMessage(), ex.getCause());
		}
	}

	@Override
	public Response<Object> deserialize(byte[] bytes) throws SerializationException {
		try {
			Object data = objectmapper.readValue(bytes, new TypeReference<Object>() {
			});
			return Response.builder().data(data).build();
		} catch (IOException ex) {
			throw new SerializationException(ex.getMessage(), ex.getCause());
		}		
	}
}
