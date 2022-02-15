package com.tarento.formservice.model;


/**
 * @author Darshan Nagesh
 *
 */
public class WorkflowDto {
	private String currentState;
	private String role;
	private String actionStatement;
	private String nextState; 
	private String applicationId;
	private Long formId;
	private Long changedBy;
	private String changedDate;
	
	public WorkflowDto() {} 
	public WorkflowDto(IncomingData data, UserInfo userInfo, String actionStatement) { 
		this.currentState = data.getStatus();
		for(Role role : userInfo.getRoles()) { 
			this.role = role.getName();
		}
		this.actionStatement = actionStatement; 
		this.applicationId = data.getApplicationId();
		this.formId = data.getFormId(); 
		this.changedBy = data.getReviewedBy(); 
		this.changedDate = data.getReviewedDate();
	}
	
	public WorkflowDto(AssignApplication assign, UserInfo userInfo, String actionStatement) { 
		this.currentState = assign.getStatus();
		for(Role role : userInfo.getRoles()) { 
			this.role = role.getName();
		}
		this.actionStatement = actionStatement; 
		this.applicationId = assign.getApplicationId();
		this.formId = assign.getFormId(); 
		this.changedBy = assign.getAssignedBy(); 
		this.changedDate = assign.getAssignedDate();
	}
	
	public String getCurrentState() {
		return currentState;
	}
	public void setCurrentState(String currentState) {
		this.currentState = currentState;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getActionStatement() {
		return actionStatement;
	}
	public void setActionStatement(String actionStatement) {
		this.actionStatement = actionStatement;
	}
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	public String getNextState() {
		return nextState;
	}
	public void setNextState(String nextState) {
		this.nextState = nextState;
	}
	public Long getFormId() {
		return formId;
	}
	public void setFormId(Long formId) {
		this.formId = formId;
	}
	public Long getChangedBy() {
		return changedBy;
	}
	public void setChangedBy(Long changedBy) {
		this.changedBy = changedBy;
	}
	public String getChangedDate() {
		return changedDate;
	}
	public void setChangedDate(String changedDate) {
		this.changedDate = changedDate;
	}
	
}
