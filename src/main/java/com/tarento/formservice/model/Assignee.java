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

}
