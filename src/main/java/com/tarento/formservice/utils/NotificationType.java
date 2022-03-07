package com.tarento.formservice.utils;

import org.apache.commons.lang3.StringUtils;

public enum NotificationType {
	INFORMATION("INFORMATION"), ALERT("ALERT");

	private String name;

	private NotificationType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return StringUtils.capitalize(name());
	}

}
