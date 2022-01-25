package com.tarento.formservice.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PackDistribution {
	
	@JsonProperty("id")
	private Long id;
	@JsonProperty("purpose")
	private String purpose;
	@JsonProperty("url")
	private String url;
	@JsonProperty("isActive")
	private Boolean isActive;
	@JsonProperty("stats")
	private List<Object> stats;
	
	public List<Object> getStats() {
		return stats;
	}
	public void setStats(List<Object> stats) {
		this.stats = stats;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	

}
