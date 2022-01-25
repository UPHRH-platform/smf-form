package com.tarento.formservice.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataObject {
	
	@JsonProperty("dataContext")
	private String dataContext;
	
	@JsonProperty("dataContextVersion")
	private String dataContextVersion;
	
	@JsonProperty("contents")
	private List<Content> contents;
	
	@JsonProperty("dataContext")
	public String getDataContext() {
		return dataContext;
	}
	@JsonProperty("dataContext")
	public void setDataContext(String dataContext) {
		this.dataContext = dataContext;
	}
	
	@JsonProperty("dataContextVersion")
	public String getDataContextVersion() {
		return dataContextVersion;
	}
	@JsonProperty("dataContextVersion")
	public void setDataContextVersion(String dataContextVersion) {
		this.dataContextVersion = dataContextVersion;
	}
	
	@JsonProperty("contents")
	public List<Content> getContents() {
		return contents;
	}
	@JsonProperty("contents")
	public void setContents(List<Content> contents) {
		this.contents = contents;
	} 
}
