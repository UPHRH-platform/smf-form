
package com.tarento.formservice.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.ToString;

@ToString(includeFieldNames = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "formDetails" })
public class FormModel implements Serializable {

	@JsonProperty("formDetails")
	private List<FormDetail> formDetails = null;
	private static final long serialVersionUID = -1434084318938862789L;

	@JsonProperty("formDetails")
	public List<FormDetail> getFormDetails() {
		return formDetails;
	}

	@JsonProperty("formDetails")
	public void setFormDetails(List<FormDetail> formDetails) {
		this.formDetails = formDetails;
	}

	public FormModel withFormDetails(List<FormDetail> formDetails) {
		this.formDetails = formDetails;
		return this;
	}

}
