package com.tarento.formservice.producer;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("rawtypes")
public class HashMapSerializer implements Serializer<Map> {
	@Override
	public void close() {
		//
	}

	@Override
	public void configure(Map<String, ?> arg0, boolean arg1) {
		//
	}

	@Override
	public byte[] serialize(String topic, Map data) {
		byte[] value = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			value = objectMapper.writeValueAsString(data).getBytes();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return value;
	}
}
