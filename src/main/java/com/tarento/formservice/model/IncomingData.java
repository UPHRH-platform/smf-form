package com.tarento.formservice.model;

import java.util.List;
import java.util.Map;

public class IncomingData {

	private Long id;
	private Long formId;
	private String recordId;
	private int version;
	private Long timestamp;
	private Object dataObject;
	private String formData;
	private Long upvoteCount;
	private Long downvoteCount;
	private List<Vote> upvotes;
	private List<Vote> downvotes;
	private List<ReplyFeedbackDto> replies;
	private Map<String, List<String>> attachments;

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

	public Map<String, List<String>> getAttachments() {
		return attachments;
	}

	public void setAttachments(Map<String, List<String>> attachments) {
		this.attachments = attachments;
	}

}
