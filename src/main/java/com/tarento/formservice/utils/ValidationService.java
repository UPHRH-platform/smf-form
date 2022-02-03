package com.tarento.formservice.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.models.Field;
import com.tarento.formservice.models.FormDetail;

@Service
public class ValidationService {

	public String validateCreateForm(FormDetail form) throws JsonProcessingException {

		if (form == null) {
			return Constants.ResponseMessages.CHECK_REQUEST_PARAMS;
		}
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

}
