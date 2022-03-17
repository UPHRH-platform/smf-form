package com.tarento.formservice.models;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Darshan Nagesh
 *
 */
@Getter
@Setter
public class UserDevice {

	private Long id;

	private Long userId;

	private String deviceToken;

	private String userName;

	private String deviceId;

	private String authToken;

}
