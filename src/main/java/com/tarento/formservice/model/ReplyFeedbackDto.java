package com.tarento.formservice.model;

public class ReplyFeedbackDto {
	private String recordId;
	private String reply; 
	private Long userId; 
	private String username; 
	private Long replyDate;
	public String getRecordId() {
		return recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Long getReplyDate() {
		return replyDate;
	}
	public void setReplyDate(Long replyDate) {
		this.replyDate = replyDate;
	} 

	
}
