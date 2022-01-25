package com.tarento.formservice.service;

import java.io.IOException;

import com.tarento.formservice.model.FormDetail;
import com.tarento.formservice.models.Form;

public interface JsonFormsService {

	public Boolean processJsonForms(Object obj, Form form, FormDetail formModel) throws IOException;

}
