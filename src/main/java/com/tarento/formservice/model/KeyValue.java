package com.tarento.formservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyValue {

	private String key;
	private Object value;

	public KeyValue() {
	}

	public KeyValue(String key, Object value) {
		this.key = key;
		this.value = value;
	}

}
