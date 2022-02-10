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
	private String formData;
	private Long upvoteCount;
	private Long downvoteCount;
	private List<Vote> upvotes;
	private List<Vote> downvotes;
	private List<ReplyFeedbackDto> replies;
	private String status;
	private String comments;
	private AssignApplication inspection;
	private String createdBy;
	private String createdDate;
	private String updatedBy;
	private String updatedDate;
	private Long reviewedBy;
	private String reviewedDate;

}
