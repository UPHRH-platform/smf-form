package com.tarento.formservice.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "action", "role", "url", "currentState", "nextState" })
public class StateMatrix {

	@JsonProperty("action")
	private String action;
	@JsonProperty("role")
	private String role;
	@JsonProperty("url")
	private String url;
	@JsonProperty("currentState")
	private String currentState;
	@JsonProperty("nextState")
	private String nextState;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("action")
	public String getAction() {
		return action;
	}

	@JsonProperty("action")
	public void setAction(String action) {
		this.action = action;
	}

	@JsonProperty("role")
	public String getRole() {
		return role;
	}

	@JsonProperty("role")
	public void setRole(String role) {
		this.role = role;
	}

	@JsonProperty("url")
	public String getUrl() {
		return url;
	}

	@JsonProperty("url")
	public void setUrl(String url) {
		this.url = url;
	}

	@JsonProperty("currentState")
	public String getCurrentState() {
		return currentState;
	}

	@JsonProperty("currentState")
	public void setCurrentState(String currentState) {
		this.currentState = currentState;
	}

	@JsonProperty("nextState")
	public String getNextState() {
		return nextState;
	}

	@JsonProperty("nextState")
	public void setNextState(String nextState) {
		this.nextState = nextState;
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