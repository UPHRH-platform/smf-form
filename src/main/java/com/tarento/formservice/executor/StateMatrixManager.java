package com.tarento.formservice.executor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.tarento.formservice.model.ResponseData;
import com.tarento.formservice.model.State;
import com.tarento.formservice.model.StateMatrix;
import com.tarento.formservice.service.FormsService;

@Component
public class StateMatrixManager implements ApplicationRunner {

	public static final Logger LOGGER = LoggerFactory.getLogger(StateMatrixManager.class);
	protected static ConcurrentMap<Long, ResponseData> userData = new ConcurrentHashMap<>();
	protected static ConcurrentMap<Long, State> stateMap = new ConcurrentHashMap<>();
	protected static ConcurrentMap<String, StateMatrix> stateMatrixMap = new ConcurrentHashMap<>();
	private static FormsService formsService;
	
	@Autowired
	public void setFormService(FormsService formsService) {
		StateMatrixManager.formsService = formsService;
	}

	/**
	 * Application Runner will make sure this piece of code executes at start of the application Form Service
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		getAllStates(); 
		getAllStateMatrix();
	}

	/**
	 * This method would return the User Data which has been stored in the Concurrent Hash Map
	 * @return
	 */
	public static ConcurrentMap<Long, ResponseData> getUserData() {
		return userData;
	}

	/**
	 * This Method would set the User Data in the Map 
	 * @param userData
	 */
	public static void setUserData(ConcurrentMap<Long, ResponseData> userData) {
		StateMatrixManager.userData = userData;
	}
	
	/**
	 * This method would clear the State Map and then invoke Form Service APIs to fetch the data from Elasticsearch Database and 
	 * save it in the State Map 
	 */
	public static void getAllStates() {
		getStateMap().clear();
		stateMap = formsService.fetchAllStates();
		
	}
	/**
	 * This method would return the State Map Data which has been stored in the Concurrent Hash Map
	 * @return
	 */
	public static ConcurrentMap<Long, State> getStateMap() {
		return stateMap;
	}
	
	/**
	 * This method would clear the State Matrix Map and then invoke Form Service APIs to fetch the data from Elasticsearch Database and 
	 * save it in the State Matrix Map
	 */
	public static void getAllStateMatrix() {
		getStateMatrixMap().clear();
		stateMatrixMap = formsService.fetchAllStateMatrix();
		
	}
	
	/**
	 * This method would return the State Matrix Map Data which has been stored in the Concurrent Hash Map
	 * @return
	 */
	public static ConcurrentMap<String, StateMatrix> getStateMatrixMap() {
		return stateMatrixMap;
	}
	
	/**
	 * Method which flushes the data that has been stored in the HashMaps
	 */
	public static void flushMasterData() {
		getStateMap().clear();
		getStateMatrixMap().clear();
		getUserData().clear();
	}

	/** 
	 * After flush has been invoked, this method would reload all the data on runtime
	 */
	public static void reloadMasterData() {
		getAllStates();
		getAllStateMatrix();
	}


}