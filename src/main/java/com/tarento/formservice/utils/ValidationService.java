package com.tarento.formservice.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.model.Status;
import com.tarento.formservice.models.Field;
import com.tarento.formservice.models.FormDetail;

@Service
public class ValidationService {

	public static final Logger LOGGER = LoggerFactory.getLogger(ValidationService.class);

	public String validateCreateForm(FormDetail form) throws JsonProcessingException {

		if (form == null) {
			return Constants.ResponseMessages.CHECK_REQUEST_PARAMS;
		}
		// validate for create operation
		if (form.getId() == null) {
			if (StringUtils.isBlank(form.getTitle())) {
				return Constants.ResponseMessages.TITLE_MISSING;
			}
			if (form.getFields() == null || form.getFields().size() == 0) {
				return Constants.ResponseMessages.FIELD_MISSING;
			}
			List<Integer> fieldOrders = new ArrayList<>();
			for (Field field : form.getFields()) {
				if (StringUtils.isBlank(field.getName())) {
					return Constants.ResponseMessages.FIELD_NAME_MISSING;
				}
				if (field.getOrder() == null || field.getOrder() == 0 || fieldOrders.contains(field.getOrder())) {
					return Constants.ResponseMessages.FIELD_ORDER_MISSING;
				}
				fieldOrders.add(field.getOrder());
				if (StringUtils.isBlank(field.getFieldType())) {
					field.setFieldType(Constants.FormFieldTypes.TEXT);
				}
			}
		}
		return Constants.ResponseCodes.SUCCESS;

	}

	public String validateSubmittedApplication(IncomingData incomingData) {
		if (incomingData == null) {
			return Constants.ResponseMessages.CHECK_REQUEST_PARAMS;
		}
		if (incomingData.getFormId() == null) {
			return Constants.ResponseMessages.FORM_ID_MISSING;
		}
		if (incomingData.getDataObject() == null) {
			return Constants.ResponseMessages.DATA_OBJECT_MISSING;
		} else {
			if (incomingData.getDataObject() instanceof Map) {
				incomingData.setDataObject(removeEmptyFields(incomingData.getDataObject()));
			}
		}
		return Constants.ResponseCodes.SUCCESS;
	}

	public String validateApplicationReview(IncomingData incomingData) {
		if (incomingData == null) {
			return Constants.ResponseMessages.CHECK_REQUEST_PARAMS;
		}
		if (StringUtils.isBlank(incomingData.getApplicationId())) {
			return Constants.ResponseMessages.APPLICATION_ID_MISSING;
		}
		return Constants.ResponseCodes.SUCCESS;
	}

	/**
	 * Validates the status in the request and sets default status for in the
	 * invalid value
	 * 
	 * @param incomingData
	 *            IncomingData
	 */
	public void validateApplicationStatus(IncomingData incomingData) {
		if (StringUtils.isNotBlank(incomingData.getStatus())
				&& incomingData.getStatus().equalsIgnoreCase(Status.DRAFT.name())) {
			incomingData.setStatus(Status.DRAFT.name());
		} else {
			incomingData.setStatus(Status.NEW.name());
		}
	}

	public String validateInspectionObject(IncomingData incomingData) {
		if (incomingData == null) {
			return Constants.ResponseMessages.CHECK_REQUEST_PARAMS;
		}
		if (StringUtils.isBlank(incomingData.getApplicationId())) {
			return Constants.ResponseMessages.APPLICATION_ID_MISSING;
		}
		if (incomingData.getDataObject() == null) {
			return Constants.ResponseMessages.DATA_OBJECT_MISSING;
		} else {
			if (incomingData.getDataObject() instanceof Map) {
				incomingData.setDataObject(removeEmptyFields(incomingData.getDataObject()));
			}
		}
		if (incomingData.getInspectorSummaryDataObject() == null) {
			return Constants.ResponseMessages.INSPECTOR_SUMMARY_MISSING;
		}
		return Constants.ResponseCodes.SUCCESS;
	}

	/**
	 * Iterates the data object and removes if any empty key or value present with
	 * in the data object
	 * 
	 * @param dataObject
	 *            Object
	 * @return Object
	 */
	public Object removeEmptyFields(Object dataObject) {
		try {
			ConcurrentHashMap<String, Object> dataObjectMap = new ObjectMapper().convertValue(dataObject,
					new TypeReference<ConcurrentHashMap<String, Object>>() {
					});
			for (Map.Entry<String, Object> entry : dataObjectMap.entrySet()) {
				if (entry.getKey().equals(StringUtils.EMPTY) || (entry.getValue() instanceof String
						&& (entry.getValue() == null || entry.getValue().equals(StringUtils.EMPTY)))) {
					dataObjectMap.remove(entry.getKey());
				} else if (entry.getValue() instanceof Map) {
					entry.setValue(removeEmptyFields(entry.getValue()));
				}
			}
			return dataObjectMap;
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "removeEmptyFields", e.getMessage()));
		}
		return dataObject;
	}

	public String validateReturnedApplication(IncomingData incomingData) {
		if (incomingData == null) {
			return Constants.ResponseMessages.CHECK_REQUEST_PARAMS;
		}
		if (StringUtils.isBlank(incomingData.getApplicationId())) {
			return Constants.ResponseMessages.APPLICATION_ID_MISSING;
		}
		if (StringUtils.isBlank(incomingData.getNotes())) {
			return Constants.ResponseMessages.COMMENTS_MISSING;
		}
		return Constants.ResponseCodes.SUCCESS;
	}

	public String validateApprovedApplication(IncomingData incomingData) {
		if (incomingData == null) {
			return Constants.ResponseMessages.CHECK_REQUEST_PARAMS;
		}
		if (StringUtils.isBlank(incomingData.getApplicationId())) {
			return Constants.ResponseMessages.APPLICATION_ID_MISSING;
		}
		return Constants.ResponseCodes.SUCCESS;
	}

	public void validateFormStatus(FormDetail form) {
		if (StringUtils.isNotBlank(form.getStatus()) && form.getStatus().equalsIgnoreCase(Status.DRAFT.name())) {
			form.setStatus(Status.DRAFT.name());
		} else if (StringUtils.isNotBlank(form.getStatus())
				&& form.getStatus().equalsIgnoreCase(Status.UNPUBLISH.name())) {
			form.setStatus(Status.UNPUBLISH.name());
		} else if (StringUtils.isNotBlank(form.getStatus()) && form.getStatus().equalsIgnoreCase(Status.DELETED.name())){
			form.setStatus(Status.DELETED.name());
		}else {
			form.setStatus(Status.PUBLISH.name());
		}
	}

}
