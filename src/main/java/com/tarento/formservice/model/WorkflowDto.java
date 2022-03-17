package com.tarento.formservice.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Darshan Nagesh
 *
 */
@Getter
@Setter
public class WorkflowDto {
	private String currentState;
	private String role;
	private String actionStatement;
	private String nextState;
	private String applicationId;
	private Long formId;
	private Long changedBy;
	private String changedDate;

	public WorkflowDto() {
	}

	public WorkflowDto(IncomingData data, UserInfo userInfo, String actionStatement) {
		this.currentState = data.getStatus();
		for (Role role : userInfo.getRoles()) {
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
		for (Role role : userInfo.getRoles()) {
			this.role = role.getName();
		}
		this.actionStatement = actionStatement;
		this.applicationId = assign.getApplicationId();
		this.formId = assign.getFormId();
		this.changedBy = assign.getAssignedBy();
		this.changedDate = assign.getAssignedDate();
	}

}
