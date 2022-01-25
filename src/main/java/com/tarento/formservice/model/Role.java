package com.tarento.formservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Object getCode() {
		return code;
	}
	public void setCode(Object code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getOrgId() {
		return orgId;
	}
	public void setOrgId(Object orgId) {
		this.orgId = orgId;
	}
	public Object getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Object createdBy) {
		this.createdBy = createdBy;
	}
	public Boolean getAdmin() {
		return admin;
	}
	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
	public Boolean getSuperAdmin() {
		return superAdmin;
	}
	public void setSuperAdmin(Boolean superAdmin) {
		this.superAdmin = superAdmin;
	}
	public Boolean getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	

}