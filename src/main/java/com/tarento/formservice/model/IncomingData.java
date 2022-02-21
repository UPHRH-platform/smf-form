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
	private String formData;
	private Long upvoteCount;
	private Long downvoteCount;
	private List<Vote> upvotes;
	private List<Vote> downvotes;
	private List<ReplyFeedbackDto> replies;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Object getDataObject() {
		return dataObject;
	}

	public void setDataObject(Object dataObject) {
		this.dataObject = dataObject;
	}

	public String getFormData() {
		return formData;
	}

	public void setFormData(String formData) {
		this.formData = formData;
	}

	public Long getUpvoteCount() {
		return upvoteCount;
	}

	public void setUpvoteCount(Long upvoteCount) {
		this.upvoteCount = upvoteCount;
	}

	public Long getDownvoteCount() {
		return downvoteCount;
	}

	public void setDownvoteCount(Long downvoteCount) {
		this.downvoteCount = downvoteCount;
	}

	public List<Vote> getUpvotes() {
		return upvotes;
	}

	public void setUpvotes(List<Vote> upvotes) {
		this.upvotes = upvotes;
	}

	public List<Vote> getDownvotes() {
		return downvotes;
	}

	public void setDownvotes(List<Vote> downvotes) {
		this.downvotes = downvotes;
	}

	public List<ReplyFeedbackDto> getReplies() {
		return replies;
	}

	public void setReplies(List<ReplyFeedbackDto> replies) {
		this.replies = replies;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNote() {
		return notes;
	}

	public void setNote(String note) {
		this.notes = note;
	}

	public AssignApplication getInspection() {
		return inspection;
	}

	public void setInspection(AssignApplication inspection) {
		this.inspection = inspection;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Long getReviewedBy() {
		return reviewedBy;
	}

	public void setReviewedBy(Long reviewedBy) {
		this.reviewedBy = reviewedBy;
	}

	public String getReviewedDate() {
		return reviewedDate;
	}

	public void setReviewedDate(String reviewedDate) {
		this.reviewedDate = reviewedDate;
	}

	public Object getInspectorDataObject() {
		return inspectorDataObject;
	}

	public void setInspectorDataObject(Object inspectorDataObject) {
		this.inspectorDataObject = inspectorDataObject;
	}

}
