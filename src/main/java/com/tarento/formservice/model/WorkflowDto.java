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
	private String formId;
	private String changedBy;
	private Long changedDate;
	
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
	public String getFormId() {
		return formId;
	}
	public void setFormId(String formId) {
		this.formId = formId;
	}
	public String getChangedBy() {
		return changedBy;
	}
	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}
	public Long getChangedDate() {
		return changedDate;
	}
	public void setChangedDate(Long changedDate) {
		this.changedDate = changedDate;
	}
	public String getNextState() {
		return nextState;
	}
	public void setNextState(String nextState) {
		this.nextState = nextState;
	}
}
