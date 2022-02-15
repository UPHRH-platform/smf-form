package com.tarento.formservice.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignApplication {

	private String applicationId;
	private List<UserProfile> assignedTo;
	private String scheduledDate;
	private String status;
	private Long assignedBy;
	private String assignedDate;
	private List<Long> userId;
	private Long formId;
	
	public Long getFormId() {
		return formId;
	}
	public void setFormId(Long formId) {
		this.formId = formId;
	}
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	public List<UserProfile> getAssignedTo() {
		return assignedTo;
	}
	public void setAssignedTo(List<UserProfile> assignedTo) {
		this.assignedTo = assignedTo;
	}
	public String getScheduledDate() {
		return scheduledDate;
	}
	public void setScheduledDate(String scheduledDate) {
		this.scheduledDate = scheduledDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getAssignedBy() {
		return assignedBy;
	}
	public void setAssignedBy(Long assignedBy) {
		this.assignedBy = assignedBy;
	}
	public String getAssignedDate() {
		return assignedDate;
	}
	public void setAssignedDate(String assignedDate) {
		this.assignedDate = assignedDate;
	}
	public List<Long> getUserId() {
		return userId;
	}
	public void setUserId(List<Long> userId) {
		this.userId = userId;
	}
	
	
}
