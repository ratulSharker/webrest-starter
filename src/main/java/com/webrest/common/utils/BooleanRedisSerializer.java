package com.webrest.common.utils;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class BooleanRedisSerializer implements RedisSerializer<Boolean>  {

	@Override
	public byte[] serialize(Boolean t) throws SerializationException {
		return Boolean.TRUE.equals(t) ? "T".getBytes() : "F".getBytes();
	}

	@Override
	public Boolean deserialize(byte[] bytes) throws SerializationException {
		String string = new String(bytes);
		return "T".equals(string) ? Boolean.TRUE : Boolean.FALSE;
	}
	
}
