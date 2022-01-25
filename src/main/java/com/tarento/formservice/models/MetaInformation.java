package com.tarento.formservice.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.ToString;

@ToString(includeFieldNames = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetaInformation {

	@JsonProperty("id")
	private Long id = null;
	@JsonProperty("version")
	private Long version= null;
	@JsonProperty("name")
	private String name= null;
	@JsonProperty("tagline")
	private String tagline= null;
	@JsonProperty("domain")
	private String domain= null;
	@JsonProperty("colorCode")
	private String colorCode=null;
	@JsonProperty("isFeatured")
	private Boolean isFeatured=null;
	@JsonProperty("isSaved")
	private Boolean isSaved=null;
	@JsonProperty("isPublished")
	private Boolean isPublished=null;
	@JsonProperty("clientName")
	private String clientName=null;
	@JsonProperty("clientCountry")
	private String clientCountry=null;
	@JsonProperty("clientLogo")
	private String clientLogo=null;
	@JsonProperty("tags")
	private String tags=null;
	
	
	
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public MetaInformation() {} 
	public MetaInformation(Long id, String name, String domain, String tagline, String colorCode) { 
		this.id = id ; 
		this.name = name; 
		this.domain = domain;
		this.tagline = tagline; 
		this.colorCode = colorCode; 
	}
	
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getClientCountry() {
		return clientCountry;
	}
	public void setClientCountry(String clientCountry) {
		this.clientCountry = clientCountry;
	}
	public String getClientLogo() {
		return clientLogo;
	}
	public void setClientLogo(String clientLogo) {
		this.clientLogo = clientLogo;
	}
	public Boolean getIsSaved() {
		return isSaved;
	}
	public void setIsSaved(Boolean isSaved) {
		this.isSaved = isSaved;
	}
	public Boolean getIsPublished() {
		return isPublished;
	}
	public void setIsPublished(Boolean isPublished) {
		this.isPublished = isPublished;
	}
	public Boolean getIsFeatured() {
		return isFeatured;
	}
	public void setIsFeatured(Boolean isFeatured) {
		this.isFeatured = isFeatured;
	}
	public String getColorCode() {
		return colorCode;
	}
	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTagline() {
		return tagline;
	}
	public void setTagline(String tagline) {
		this.tagline = tagline;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	

}