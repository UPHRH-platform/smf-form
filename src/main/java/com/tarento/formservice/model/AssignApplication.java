package com.tarento.formservice.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignApplication {

	private String applicationId;
	private List<Assignee> assignedTo;
	private String scheduledDate;
	private String status;
	private Long assignedBy;
	private String assignedDate;
	private List<Long> assistingInspector;
	private List<Long> leadInspector;
	private Long formId;
	private String inspectionDate;
	private String inspectionCompletedDate;

}
