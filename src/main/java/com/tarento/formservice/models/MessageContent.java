package com.tarento.formservice.models;

import com.tarento.formservice.utils.NotificationType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageContent {

	private Long id;
	private Long messageId;
	private NotificationType notificationType;
	private String senderServiceName;
	private Long senderServiceId;
	private Long notificationDateTime;
	private Long createdDateTime;
	private String messageBody;
	private String messageTitle;
	private String imageUrl;
	private Long expiryDateTime;
	private String url;

}