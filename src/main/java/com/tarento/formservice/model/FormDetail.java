
package com.tarento.formservice.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.ToString;

@ToString(includeFieldNames = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "file_name", "fileCode", "form_id", "file_format", "isFormIdPresentFileName", "esIndexName",
		"esIndexDocType", "kafkaTopic", "kakfkaKey", "description", "action" })
public class FormDetail implements Serializable {

	@JsonProperty("file_name")
	private String fileName;
	@JsonProperty("fileCode")
	private String fileCode;
	@JsonProperty("form_id")
	private String formId;
	@JsonProperty("file_format")
	private String fileFormat;
	@JsonProperty("isFormIdPresentFileName")
	private Boolean isFormIdPresentFileName;
	@JsonProperty("esIndexName")
	private String esIndexName;
	@JsonProperty("esIndexDocType")
	private String esIndexDocType;
	@JsonProperty("kafkaTopic")
	private String kafkaTopic;
	@JsonProperty("kakfkaKey")
	private String kakfkaKey;
	@JsonProperty("description")
	private String description;
	@JsonProperty("action")
	private String action;
	private static final long serialVersionUID = 366110758009975933L;

	@JsonProperty("file_name")
	public String getFileName() {
		return fileName;
	}

	@JsonProperty("file_name")
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public FormDetail withFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	@JsonProperty("fileCode")
	public String getFileCode() {
		return fileCode;
	}

	@JsonProperty("fileCode")
	public void setFileCode(String fileCode) {
		this.fileCode = fileCode;
	}

	public FormDetail withFileCode(String fileCode) {
		this.fileCode = fileCode;
		return this;
	}

	@JsonProperty("form_id")
	public String getFormId() {
		return formId;
	}

	@JsonProperty("form_id")
	public void setFormId(String formId) {
		this.formId = formId;
	}

	public FormDetail withFormId(String formId) {
		this.formId = formId;
		return this;
	}

	@JsonProperty("file_format")
	public String getFileFormat() {
		return fileFormat;
	}

	@JsonProperty("file_format")
	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	public FormDetail withFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
		return this;
	}

	@JsonProperty("isFormIdPresentFileName")
	public Boolean getIsFormIdPresentFileName() {
		return isFormIdPresentFileName;
	}

	@JsonProperty("isFormIdPresentFileName")
	public void setIsFormIdPresentFileName(Boolean isFormIdPresentFileName) {
		this.isFormIdPresentFileName = isFormIdPresentFileName;
	}

	public FormDetail withIsFormIdPresentFileName(Boolean isFormIdPresentFileName) {
		this.isFormIdPresentFileName = isFormIdPresentFileName;
		return this;
	}

	@JsonProperty("esIndexName")
	public String getEsIndexName() {
		return esIndexName;
	}

	@JsonProperty("esIndexName")
	public void setEsIndexName(String esIndexName) {
		this.esIndexName = esIndexName;
	}

	public FormDetail withEsIndexName(String esIndexName) {
		this.esIndexName = esIndexName;
		return this;
	}

	@JsonProperty("esIndexDocType")
	public String getEsIndexDocType() {
		return esIndexDocType;
	}

	@JsonProperty("esIndexDocType")
	public void setEsIndexDocType(String esIndexDocType) {
		this.esIndexDocType = esIndexDocType;
	}

	public FormDetail withEsIndexDocType(String esIndexDocType) {
		this.esIndexDocType = esIndexDocType;
		return this;
	}

	@JsonProperty("kafkaTopic")
	public String getKafkaTopic() {
		return kafkaTopic;
	}

	@JsonProperty("kafkaTopic")
	public void setKafkaTopic(String kafkaTopic) {
		this.kafkaTopic = kafkaTopic;
	}

	public FormDetail withKafkaTopic(String kafkaTopic) {
		this.kafkaTopic = kafkaTopic;
		return this;
	}

	@JsonProperty("kakfkaKey")
	public String getKakfkaKey() {
		return kakfkaKey;
	}

	@JsonProperty("kakfkaKey")
	public void setKakfkaKey(String kakfkaKey) {
		this.kakfkaKey = kakfkaKey;
	}

	public FormDetail withKakfkaKey(String kakfkaKey) {
		this.kakfkaKey = kakfkaKey;
		return this;
	}

	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}

	public FormDetail withDescription(String description) {
		this.description = description;
		return this;
	}

	@JsonProperty("action")
	public String getAction() {
		return action;
	}

	@JsonProperty("action")
	public void setAction(String action) {
		this.action = action;
	}

	public FormDetail withAction(String action) {
		this.action = action;
		return this;
	}

}
