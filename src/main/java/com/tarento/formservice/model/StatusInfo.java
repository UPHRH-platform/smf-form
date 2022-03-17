package com.tarento.formservice.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusInfo {

	private Long statusCode;
	private String statusMessage;
	private String errorMessage;

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("statusCode", statusCode).append("statusMessage", statusMessage)
				.append("errorMessage", errorMessage).toString();
	}

}