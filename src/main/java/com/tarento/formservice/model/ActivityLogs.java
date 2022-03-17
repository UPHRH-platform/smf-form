package com.tarento.formservice.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityLogs {

	private String id;
	private String type;
	private Long updatedBy;
	private String updatedByEmail;
	private String updatedDate;
	private String user;
	private Object object;
	private Object updatedObject;
	private Long timestamp;
	private Map<String, Map<String, Object>> changes;

}
