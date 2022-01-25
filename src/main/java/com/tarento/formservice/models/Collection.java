package com.tarento.formservice.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.ToString;

@ToString(includeFieldNames = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Collection {

	@JsonProperty("id")
	private Long id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("description")
	private String description;
	@JsonProperty("isActive")
	private Boolean isActive;
	@JsonProperty("isDeleted")
	private Boolean isDeleted;
	@JsonProperty("isPrimary")
	private Boolean isPrimary;
	@JsonProperty("backgroundImage")
	private String backgroundImage;
	@JsonProperty("backgroundColor")
	private String backgroundColor;
	@JsonProperty("projectList")
	private List<Long> projectList;
	@JsonProperty("projectInfoList")
	private List<Object> projectInfoList;
	@JsonProperty("serviceList")
	private List<Long> serviceList;
	@JsonProperty("serviceInfoList")
	private List<Object> serviceInfoList;
	
	public List<Long> getServiceList() {
		return serviceList;
	}
	public void setServiceList(List<Long> serviceList) {
		this.serviceList = serviceList;
	}
	public List<Object> getServiceInfoList() {
		return serviceInfoList;
	}
	public void setServiceInfoList(List<Object> serviceInfoList) {
		this.serviceInfoList = serviceInfoList;
	}
	public Boolean getIsPrimary() {
		return isPrimary;
	}
	public void setIsPrimary(Boolean isPrimary) {
		this.isPrimary = isPrimary;
	}
	public String getBackgroundImage() {
		return backgroundImage;
	}
	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
	}
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	public List<Object> getProjectInfoList() {
		return projectInfoList;
	}
	public void setProjectInfoList(List<Object> projectInfoList) {
		this.projectInfoList = projectInfoList;
	}
	public List<Long> getProjectList() {
		return projectList;
	}
	public void setProjectList(List<Long> projectList) {
		this.projectList = projectList;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	
}