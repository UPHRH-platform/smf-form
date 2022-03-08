package com.tarento.formservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Consent {
	private String applicationId;
	private Boolean agree;
	private String comments;
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	public Boolean getAgree() {
		return agree;
	}
	public void setAgree(Boolean agree) {
		this.agree = agree;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
}
