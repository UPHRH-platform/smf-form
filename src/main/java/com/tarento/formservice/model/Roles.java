package com.tarento.formservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Roles {

	INSTITUTION("Institution"), REGULATOR("Regulator"), INSPECTOR("Inspector");

	private String value;

	Roles(final String value) {
		this.value = value;
	}

	@JsonValue
	@Override
	public String toString() {
		return value;
	}

	@JsonCreator
	public static Roles fromValue(final String passedValue) {
		for (final Roles obj : Roles.values())
			if (String.valueOf(obj.value).equalsIgnoreCase(passedValue))
				return obj;
		return null;
	}
}