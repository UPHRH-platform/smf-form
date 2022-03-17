package com.tarento.formservice.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

}
