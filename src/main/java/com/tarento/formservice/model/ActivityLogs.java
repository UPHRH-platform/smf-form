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

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getUpdatedByEmail() {
		return updatedByEmail;
	}

	public void setUpdatedByEmail(String updatedByEmail) {
		this.updatedByEmail = updatedByEmail;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Object getUpdatedObject() {
		return updatedObject;
	}

	public void setUpdatedObject(Object updatedObject) {
		this.updatedObject = updatedObject;
	}

	public Map<String, Map<String, Object>> getChanges() {
		return changes;
	}

	public void setChanges(Map<String, Map<String, Object>> changes) {
		this.changes = changes;
	}

}
