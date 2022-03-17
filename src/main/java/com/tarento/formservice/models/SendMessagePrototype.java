package com.tarento.formservice.models;

import java.util.List;

import com.tarento.formservice.utils.NotificationType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessagePrototype {

	private String messageTitle;

	private String messageContent;

	private NotificationType type;

	private List<UserDevice> devices;

}
