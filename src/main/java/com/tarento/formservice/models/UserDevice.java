package com.tarento.formservice.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDevice {
	private Long id;

	private Long userId;

	private String userDeviceToken;

	private String userName;

	private String deviceId;

}
