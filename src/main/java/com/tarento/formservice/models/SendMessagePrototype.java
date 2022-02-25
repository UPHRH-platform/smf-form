package com.tarento.formservice.models;

import java.util.List;

import com.tarento.formservice.utils.NotificationType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessagePrototype {
	private String messageKey;

	private MessageContent message;

	private NotificationType type;

	private List<UserDevice> devices;

}
