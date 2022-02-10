package com.tarento.formservice.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignApplication {

	private String applicationId;
	private List<UserProfile> assignTo;
	private String scheduledDate;
	private String status;
	private Long assignedBy;
	private String assignedDate;
}
