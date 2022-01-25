package com.tarento.formservice.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.ToString;

@ToString(includeFieldNames = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pack {

	@JsonProperty("id")
	private Long id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("description")
	private String description;
	@JsonProperty("coverImage")
	private String coverImage;
	@JsonProperty("backgroundColor")
	private String backgroundColor;
	@JsonProperty("isActive")
	private Boolean isActive;
	@JsonProperty("isDeleted")
	private Boolean isDeleted;
	@JsonProperty("collectionList")
	private List<Long> collectionList;
	@JsonProperty("collectionInfoList")
	private List<Object> collectionInfoList;
	@JsonProperty("distributionList")
	private List<PackDistribution> distributionList;
	
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
	public String getCoverImage() {
		return coverImage;
	}
	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	public List<PackDistribution> getDistributionList() {
		return distributionList;
	}
	public void setDistributionList(List<PackDistribution> distributionList) {
		this.distributionList = distributionList;
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
	public List<Long> getCollectionList() {
		return collectionList;
	}
	public void setCollectionList(List<Long> collectionList) {
		this.collectionList = collectionList;
	}
	public List<Object> getCollectionInfoList() {
		return collectionInfoList;
	}
	public void setCollectionInfoList(List<Object> collectionInfoList) {
		this.collectionInfoList = collectionInfoList;
	}
}
