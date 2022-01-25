package com.tarento.formservice.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(includeFieldNames = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class IncomingData {

	private Long id;
	private String recordId;
	private Long customerId;
	private String customerName;
	private Long agentId;
	private String agentName;
	private String approval;
	private Long approvedBy;
	private Long approvedTime;
	private String challenge;
	private Long challengeVerifiedBy;
	private Boolean challengeStatus;
	private Long challengeVerifiedTime;
	private String reasonForChallenge;
	private String reasonForApprovalRejection;
	private String adminReply;
	private Long adminReplyTime;
	private int version;
	private Long timestamp;
	private Object dataObject;
	private String formData;
	private Long upvoteCount;
	private Long downvoteCount;
	private List<Vote> upvotes;
	private List<Vote> downvotes;
	private List<ReplyFeedbackDto> replies; 
}
