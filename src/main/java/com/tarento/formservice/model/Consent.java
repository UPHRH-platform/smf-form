package com.tarento.formservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Consent {
	private String applicationId;
	private Boolean agree;
	private String comments;
}
