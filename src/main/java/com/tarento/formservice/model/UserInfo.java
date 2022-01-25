package com.tarento.formservice.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


public class UserInfo {

	@JsonProperty("timeZone")
	public Object timeZone;
	@JsonProperty("id")
	public Long id;
	@JsonProperty("userName")
	public String userName;
	@JsonProperty("name")
	public Object name;
	@JsonProperty("type")
	public Object type;
	@JsonProperty("mobileNumber")
	public Object mobileNumber;
	@JsonProperty("authToken")
	public String authToken;
	@JsonProperty("emailId")
	public String emailId;
	@JsonProperty("orgId")
	public String orgId;
	@JsonProperty("roles")
	public List<Role> roles = null;
	public Object getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(Object timeZone) {
		this.timeZone = timeZone;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Object getName() {
		return name;
	}
	public void setName(Object name) {
		this.name = name;
	}
	public Object getType() {
		return type;
	}
	public void setType(Object type) {
		this.type = type;
	}
	public Object getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(Object mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	

}