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
@JsonPropertyOrder({
"projectMedia",
"projectDetails"
})
public class ServiceInformation extends MetaInformation {
	@JsonProperty("serviceMedia")
	private List<InformationMedia> serviceMedia = null;
	@JsonProperty("serviceDetails")
	private List<InformationDetail> serviceDetails = null;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();


	@JsonProperty("serviceMedia")
	public List<InformationMedia> getServiceMedia() {
	return serviceMedia;
	}

	@JsonProperty("serviceMedia")
	public void setServiceMedia(List<InformationMedia> serviceMedia) {
	this.serviceMedia = serviceMedia;
	}

	@JsonProperty("serviceDetails")
	public List<InformationDetail> getServiceDetails() {
	return serviceDetails;
	}

	@JsonProperty("serviceDetails")
	public void setServiceDetails(List<InformationDetail> serviceDetails) {
	this.serviceDetails = serviceDetails;
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
