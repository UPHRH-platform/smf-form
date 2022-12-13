package com.tarento.formservice.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncomingData {

	private Long id;
	private Long formId;
	private String title;
	private String applicationId;
	private String recordId;
	private int version;
	private Long timestamp;
	private Object dataObject;
	private Object inspectorDataObject;
	private Object inspectorSummaryDataObject;
	private String formData;
	private String status;
	private String notes;
	private List<Object> comments;
	private AssignApplication inspection;

	private String createdBy;
	private String createdDate;
	private String updatedBy;
	private String updatedDate;
	private Long reviewedBy;
	private String reviewedDate;
	private String inspectionDate;
	private String inspectionCompletedDate;
	private Boolean inspectionCompleted = false;

}
