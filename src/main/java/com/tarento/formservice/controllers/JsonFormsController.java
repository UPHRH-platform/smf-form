package com.tarento.formservice.controllers;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.tarento.formservice.model.FormModel;
import com.tarento.formservice.models.Form;
import com.tarento.formservice.service.JsonFormsService;
import com.tarento.formservice.utils.PathRoutes;
import com.tarento.formservice.utils.ResponseGenerator;

@RestController
@RequestMapping(PathRoutes.JsonFormServiceApi.JSON_FORM_ROOT)
public class JsonFormsController {

	public static final Logger logger = LoggerFactory.getLogger(JsonFormsController.class);

	@Value("${file.config.path}")
	public String fileDirectory;

	@Value("${file.config.name}")
	public String fileName;

	@Autowired
	private JsonFormsService jsonFormsService;

	@RequestMapping(path = PathRoutes.JsonFormServiceApi.FORMS, method = RequestMethod.POST)
	public String uploadFormJsonData(@RequestHeader(value = "x-user-info", required = false) String xUserInfo,
			@RequestParam String id, @RequestBody Object jsonForm) throws IOException {
		Boolean status = Boolean.FALSE;
		logger.info("Received JSON Form {}", jsonForm);
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		FormModel formModel;
		if (jsonForm != null) {
			logger.info("Form ID : {}", id);
			formModel = mapper.readValue(new File(fileDirectory + fileName)
			// ResourceUtils.getFile("classpath:schema/FormConfig.yml")
					, FormModel.class);
			Form plainForm = new Form();
			com.tarento.formservice.model.FormDetail fDetails = new com.tarento.formservice.model.FormDetail();
			for (int k = 0; k < formModel.getFormDetails().size(); k++) {
				if (formModel.getFormDetails().get(k).getFormId().equals(id)) {
					fDetails = formModel.getFormDetails().get(k);
				} else {
					plainForm.setSecondaryId(fDetails.getFormId());
				}
			}
			plainForm.setSecondaryId(fDetails.getFormId());
			plainForm.setVersion(1);
			status = jsonFormsService.processJsonForms(jsonForm, plainForm, fDetails);
		}
		return ResponseGenerator.successResponse(status);

	}
}
