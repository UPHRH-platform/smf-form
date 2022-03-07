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

	public String getMessageTitle() {
		return messageTitle;
	}

	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public List<UserDevice> getDevices() {
		return devices;
	}

	public void setDevices(List<UserDevice> devices) {
		this.devices = devices;
	}
	
	

}
