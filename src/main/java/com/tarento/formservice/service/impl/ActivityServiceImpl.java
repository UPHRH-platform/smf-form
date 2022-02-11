package com.tarento.formservice.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.htrace.fasterxml.jackson.core.type.TypeReference;
import org.apache.htrace.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tarento.formservice.dao.FormsDao;
import com.tarento.formservice.model.ActivityLogs;
import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.model.UserInfo;
import com.tarento.formservice.service.ActivityService;
import com.tarento.formservice.utils.Constants;
import com.tarento.formservice.utils.DateUtils;

@Service
public class ActivityServiceImpl implements ActivityService {

	public static final Logger LOGGER = LoggerFactory.getLogger(ActivityServiceImpl.class);

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	FormsDao formsDao;

	@Override
	@Async
	public void createUpdateApplication(IncomingData oldObj, IncomingData updatedObj, UserInfo userInfo) {
		try {
			ActivityLogs activityLogs = new ActivityLogs();
			activityLogs.setId(updatedObj.getApplicationId());
			activityLogs.setType(Constants.ServiceTypes.APPLICATION);
			activityLogs.setUpdatedDate(DateUtils.getFormattedDateInUTC(DateUtils.getCurrentDate()));
			if (userInfo != null) {
				activityLogs.setUpdatedBy(userInfo.getId());
				activityLogs.setUpdatedByEmail(userInfo.getEmailId());
				activityLogs.setUser(String.valueOf(userInfo.getName()));
			}
			if (oldObj != null && updatedObj != null) {
				activityLogs.setObject(oldObj);
				activityLogs.setUpdatedObject(updatedObj);
				Map<String, Map<String, Object>> changes = getUpdatedFields(oldObj, updatedObj);
				if (changes != null && changes.size() > 0) {
					activityLogs.setChanges(changes);
					formsDao.addLogs(activityLogs);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(String.format(Constants.EXCEPTION, "createUpdateApplication", e.getMessage()));
		}
	}

	private Map<String, Map<String, Object>> getUpdatedFields(IncomingData oldObj, IncomingData updatedObj)
			throws Exception {
		Map<String, Map<String, Object>> changes = new HashMap<>();
		// status
		getStatement(oldObj.getStatus(), updatedObj.getStatus(), Constants.Parameters.STATUS,
				Constants.Parameters.STATUS, changes);
		// dataObject
		getStatement(oldObj.getDataObject(), updatedObj.getDataObject(), Constants.Parameters.DATA_OBJECT,
				Constants.Parameters.DATA_OBJECT, changes);
		// inspection
		getStatement(oldObj.getInspection(), updatedObj.getInspection(), Constants.Parameters.INSPECTION,
				Constants.Parameters.INSPECTION, changes);
		return changes;
	}

	/**
	 * Finds the changes between the old and the updated object, and creates a map
	 * with the changed statement
	 * 
	 * @throws Exception
	 * 
	 */
	private void getStatement(Object oldObj, Object updatedObj, String fieldName, String fieldPath,
			Map<String, Map<String, Object>> changes) throws Exception {
		Map<String, Object> logStatement = new HashMap<>();
		String changedFromKey = fieldName + Constants.Parameters.CHANGED_FROM;
		String changedToKey = fieldName + Constants.Parameters.CHANGED_TO;

		if (oldObj == null && updatedObj != null) {
			logStatement.put(Constants.Parameters.ACTION, Constants.Operations.CREATE);
			logStatement.put(changedToKey, updatedObj);
		} else if (oldObj != null && updatedObj == null) {
			logStatement.put(Constants.Parameters.ACTION, Constants.Operations.REMOVE);
			logStatement.put(changedFromKey, oldObj);
		} else if (oldObj != null && updatedObj != null && !oldObj.equals(updatedObj)) {
			logStatement.put(Constants.Parameters.ACTION, Constants.Operations.UPDATE);
			if (oldObj instanceof Map && updatedObj instanceof Map) {
				getMapFields(oldObj, updatedObj, changes, fieldPath);
			} else if (oldObj instanceof List && updatedObj instanceof List) {
				getListFields(oldObj, updatedObj, changes, fieldName, fieldPath);
			} else {
				logStatement.put(changedFromKey, oldObj);
				logStatement.put(changedToKey, updatedObj);
			}
		}
		if (logStatement.get(changedFromKey) != null || logStatement.get(changedToKey) != null) {
			logStatement.put(Constants.Parameters.FIELD, fieldName);
			changes.put(fieldPath, logStatement);
		}
	}

	private void getMapFields(Object oldObj, Object updatedObj, Map<String, Map<String, Object>> changes,
			String fieldPath) throws Exception {

		Map<String, Object> oldObjMap = mapper.convertValue(oldObj, new TypeReference<Map<String, Object>>() {
		});
		Map<String, Object> updatedObjMap = mapper.convertValue(updatedObj, new TypeReference<Map<String, Object>>() {
		});

		Set<String> keySet = new HashSet<>();
		keySet.addAll(oldObjMap.keySet());
		keySet.addAll(updatedObjMap.keySet());
		for (String key : keySet) {
			getStatement(oldObjMap.get(key), updatedObjMap.get(key), key, fieldPath + "." + key, changes);
		}
	}

	private void getListFields(Object oldObj, Object updatedObj, Map<String, Map<String, Object>> changes,
			String fieldName, String fieldPath) throws Exception {
		List<Object> oldObjList = mapper.convertValue(oldObj, new TypeReference<List<Object>>() {
		});
		List<Object> updatedObjList = mapper.convertValue(updatedObj, new TypeReference<List<Object>>() {
		});
		// removed object
		for (int i = 0; i < oldObjList.size(); i++) {
			if (!updatedObjList.contains(oldObjList.get(0))
					&& !(oldObjList.get(0) instanceof Map && oldObjList.get(0) instanceof List)) {
				getStatement(oldObjList.get(0), null, fieldName, fieldPath + "." + i, changes);
			}
		}
		// added objects
		for (int i = 0; i < updatedObjList.size(); i++) {
			if (!oldObjList.contains(updatedObjList.get(i))
					&& !(updatedObjList.get(i) instanceof Map && updatedObjList.get(i) instanceof List)) {
				getStatement(null, updatedObjList.get(i), fieldName, fieldPath + "." + i, changes);
			}
		}

	}

}
