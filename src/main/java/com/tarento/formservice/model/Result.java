package com.tarento.formservice.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result {

	private StatusInfo statusInfo;
	private ResponseData responseData;

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("statusInfo", statusInfo).append("responseData", responseData)
				.toString();
	}

}