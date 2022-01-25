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
public class ProjectInformation extends MetaInformation {


@JsonProperty("projectMedia")
private List<InformationMedia> projectMedia = null;
@JsonProperty("projectDetails")
private List<InformationDetail> projectDetails = null;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();


@JsonProperty("projectMedia")
public List<InformationMedia> getProjectMedia() {
return projectMedia;
}

@JsonProperty("projectMedia")
public void setProjectMedia(List<InformationMedia> projectMedia) {
this.projectMedia = projectMedia;
}

@JsonProperty("projectDetails")
public List<InformationDetail> getProjectDetails() {
return projectDetails;
}

@JsonProperty("projectDetails")
public void setProjectDetails(List<InformationDetail> projectDetails) {
this.projectDetails = projectDetails;
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
