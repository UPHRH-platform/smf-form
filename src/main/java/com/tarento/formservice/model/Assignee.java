package com.tarento.formservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Assignee {

	private Long id;
	private String firstName;
	private String lastName;
	private String emailId;
	private Boolean leadInspector;
	private Boolean consentApplication;
	private String comments;
	private String status;
	private String consentDate;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public Boolean getLeadInspector() {
		return leadInspector;
	}
	public void setLeadInspector(Boolean leadInspector) {
		this.leadInspector = leadInspector;
	}
	public Boolean getConsentApplication() {
		return consentApplication;
	}
	public void setConsentApplication(Boolean consentApplication) {
		this.consentApplication = consentApplication;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getConsentDate() {
		return consentDate;
	}
	public void setConsentDate(String consentDate) {
		this.consentDate = consentDate;
	}
	
	
}
