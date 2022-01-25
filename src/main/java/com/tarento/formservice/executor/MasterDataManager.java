package com.tarento.formservice.executor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.tarento.formservice.model.ResponseData;

@Component
public class MasterDataManager implements ApplicationRunner {

	public static final Logger LOGGER = LoggerFactory.getLogger(MasterDataManager.class);

	protected static ConcurrentMap<Long, ResponseData> userData = new ConcurrentHashMap<>();

	@Override
	public void run(ApplicationArguments args) throws Exception {
	}

	public static void flushMasterData() {
		getUserData().clear();
	}

	public static ConcurrentMap<Long, ResponseData> getUserData() {
		return userData;
	}

	public static void setUserData(ConcurrentMap<Long, ResponseData> userData) {
		MasterDataManager.userData = userData;
	}

}