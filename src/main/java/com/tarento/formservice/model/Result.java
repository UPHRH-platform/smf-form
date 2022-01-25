package com.tarento.formservice.model;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Result {

	private StatusInfo statusInfo;
	private ResponseData responseData;

	public StatusInfo getStatusInfo() {
		return statusInfo;
	}

	public void setStatusInfo(StatusInfo statusInfo) {
		this.statusInfo = statusInfo;
	}

	public ResponseData getResponseData() {
		return responseData;
	}

	public void setResponseData(ResponseData responseData) {
		this.responseData = responseData;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("statusInfo", statusInfo).append("responseData", responseData)
				.toString();
	}

}