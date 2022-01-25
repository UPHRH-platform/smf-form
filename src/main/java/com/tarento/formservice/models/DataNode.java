package com.tarento.formservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataNode {
	
	@JsonProperty("key")
	private String key; 
	
	@JsonProperty("value")
	private String value;
	
	@JsonProperty("dataType")
	private String dataType;
	
	@JsonProperty("key")
	public String getKey() {
		return key;
	}
	@JsonProperty("key")
	public void setKey(String key) {
		this.key = key;
	}
	
	@JsonProperty("value")
	public String getValue() {
		return value;
	}
	@JsonProperty("value")
	public void setValue(String value) {
		this.value = value;
	}
	
	@JsonProperty("dataType")
	public String getDataType() {
		return dataType;
	}
	@JsonProperty("dataType")
	public void setDataType(String dataType) {
		this.dataType = dataType;
	} 
}
