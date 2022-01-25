package com.tarento.formservice.models;

import java.util.HashMap;
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
@JsonPropertyOrder({ "heading", "subHeading" })
public class Value {

	@JsonProperty("heading")
	private String heading;
	@JsonProperty("subHeading")
	private String subHeading;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("heading")
	public String getHeading() {
		return heading;
	}

	@JsonProperty("heading")
	public void setHeading(String heading) {
		this.heading = heading;
	}

	@JsonProperty("subHeading")
	public String getSubHeading() {
		return subHeading;
	}

	@JsonProperty("subHeading")
	public void setSubHeading(String subHeading) {
		this.subHeading = subHeading;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}