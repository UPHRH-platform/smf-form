package com.tarento.formservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(includeFieldNames = true)
@AllArgsConstructor
@NoArgsConstructor

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "description", "code", "name", "orgId", "createdBy", "admin", "superAdmin", "isAdmin" })
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