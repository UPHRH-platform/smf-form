package com.tarento.formservice.service;

import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.model.UserInfo;

public interface ActivityService {

	public void applicationActivity(IncomingData oldObj, IncomingData updatedObj, UserInfo userInfo);

}
