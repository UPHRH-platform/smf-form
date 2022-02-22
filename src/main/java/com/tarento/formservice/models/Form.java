package com.tarento.formservice.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.ToString;

@ToString(includeFieldNames = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "title", "version", "numberOfRecords" })
public class Form {

	@JsonProperty("id")
	private Long id;
	@JsonProperty("title")
	private String title;
	@JsonProperty("version")
	private int version;
	@JsonProperty("numberOfRecords")
	private Long numberOfRecords;
	@JsonProperty("secondaryId")
	private String secondaryId;
	private String status;

	@JsonProperty("secondaryId")
	public String getSecondaryId() {
		return secondaryId;
	}

	@JsonProperty("secondaryId")
	public void setSecondaryId(String secondaryId) {
		this.secondaryId = secondaryId;
	}

	@JsonProperty("numberOfRecords")
	public Long getNumberOfRecords() {
		return numberOfRecords;
	}

	@JsonProperty("numberOfRecords")
	public void setNumberOfRecords(Long numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

	@JsonProperty("version")
	public int getVersion() {
		return version;
	}

	@JsonProperty("version")
	public void setVersion(int version) {
		this.version = version;
	}

	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(Long id) {
		this.id = id;
	}

	@JsonProperty("title")
	public String getTitle() {
		return title;
	}

	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = title;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}