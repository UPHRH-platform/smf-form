package com.tarento.formservice.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tarento.formservice.models.Field;
import com.tarento.formservice.models.FormDetail;

@Service
public class ValidationService {

	public String validateCreateForm(FormDetail form) throws JsonProcessingException {

		List<Integer> fieldOrders = new ArrayList<>();

		if (StringUtils.isBlank(form.getTitle())) {
			return Constants.ResponseMessages.TITLE_MISSING;
		}
		if (form.getFields() == null || form.getFields().size() == 0) {
			return Constants.ResponseMessages.FIELD_MISSING;
		}
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

}
