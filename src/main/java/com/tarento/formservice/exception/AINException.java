package com.tarento.formservice.exception;

public class AINException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final String errorCode;
	
	final String errorMessage;

	public AINException(String errorCode, String errorMessage) {
		this.errorCode= errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	
	public String getErrorMessage() {
		return errorMessage;
	}


	
}
