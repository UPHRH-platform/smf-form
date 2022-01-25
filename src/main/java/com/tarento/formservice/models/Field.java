package com.tarento.formservice.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.ToString;

@ToString(includeFieldNames = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "refApi", "logicalGroupCode", "name", "fieldType", "values", "order" })
public class Field {

	@JsonProperty("refApi")
	private String refApi;
	@JsonProperty("logicalGroupCode")
	private String logicalGroupCode;
	@JsonProperty("name")
	private String name;
	@JsonProperty("fieldType")
	private String fieldType;
	@JsonProperty("values")
	private List<Value> values = null;
	@JsonProperty("isRequired")
	private Boolean isRequired; 
	@JsonProperty("order")
	private Integer order;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	@JsonProperty("isRequired")
	public Boolean getIsRequired() {
		return isRequired;
	}

	@JsonProperty("isRequired")
	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

	@JsonProperty("refApi")
	public String getRefApi() {
		return refApi;
	}

	@JsonProperty("refApi")
	public void setRefApi(String refApi) {
		this.refApi = refApi;
	}

	@JsonProperty("logicalGroupCode")
	public String getLogicalGroupCode() {
		return logicalGroupCode;
	}

	@JsonProperty("logicalGroupCode")
	public void setLogicalGroupCode(String logicalGroupCode) {
		this.logicalGroupCode = logicalGroupCode;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("fieldType")
	public String getFieldType() {
		return fieldType;
	}

	@JsonProperty("fieldType")
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	@JsonProperty("values")
	public List<Value> getValues() {
		return values;
	}

	@JsonProperty("values")
	public void setValues(List<Value> values) {
		this.values = values;
	}

	@JsonProperty("order")
	public Integer getOrder() {
		return order;
	}

	@JsonProperty("order")
	public void setOrder(Integer order) {
		this.order = order;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public String toString() {
		return "Field [refApi=" + refApi + ", logicalGroupCode=" + logicalGroupCode + ", name=" + name + ", fieldType="
				+ fieldType + ", values=" + values + ", isRequired=" + isRequired + ", order=" + order
				+ ", additionalProperties=" + additionalProperties + "]";
	}

	
}