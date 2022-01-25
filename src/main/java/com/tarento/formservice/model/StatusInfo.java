package com.tarento.formservice.model;
import org.apache.commons.lang.builder.ToStringBuilder;

public class StatusInfo {

	private Long statusCode;
	private String statusMessage;
	private String errorMessage;

	public Long getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Long statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("statusCode", statusCode).append("statusMessage", statusMessage)
				.append("errorMessage", errorMessage).toString();
	}

}