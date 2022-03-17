package com.tarento.formservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Role {

	@JsonProperty("id")
	public Long id;
	@JsonProperty("description")
	public String description;
	@JsonProperty("code")
	public Object code;
	@JsonProperty("name")
	public String name;
	@JsonProperty("orgId")
	public Object orgId;
	@JsonProperty("createdBy")
	public Object createdBy;
	@JsonProperty("admin")
	public Boolean admin;
	@JsonProperty("superAdmin")
	public Boolean superAdmin;
	@JsonProperty("isAdmin")
	public Boolean isAdmin;

}