package com.tarento.formservice.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.tarento.formservice.model.AssignApplication;
import com.tarento.formservice.model.Consent;
import com.tarento.formservice.model.FormData;
import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.model.KeyValue;
import com.tarento.formservice.model.KeyValueList;
import com.tarento.formservice.model.SearchObject;
import com.tarento.formservice.model.SearchRequestDto;
import com.tarento.formservice.model.Status;
import com.tarento.formservice.model.UserInfo;
import com.tarento.formservice.models.Form;
import com.tarento.formservice.models.FormDetail;
import com.tarento.formservice.service.FormsService;
import com.tarento.formservice.utils.Constants;
import com.tarento.formservice.utils.PathRoutes;
import com.tarento.formservice.utils.ResponseGenerator;
import com.tarento.formservice.utils.ValidationService;

/**
 * 
 * @author Darshan Nagesh
 *
 */
@RestController
@RequestMapping(PathRoutes.FormServiceApi.FORMS_ROOT)
public class FormsController {

	public static final Logger logger = LoggerFactory.getLogger(FormsController.class);

	@Autowired
	private FormsService formsService;

	@Autowired
	private ValidationService validationService;

	@GetMapping(value = PathRoutes.FormServiceApi.GET_ALL_FORMS, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAllForms(@RequestParam(required = false) Boolean isDetail,
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo)
			throws JsonProcessingException {
		UserInfo userInfo = null;
		if (StringUtils.isNotBlank(xUserInfo)) {
			userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
		}
		return ResponseGenerator.successResponse(formsService.getAllForms(userInfo, isDetail));
	}

	@GetMapping(value = PathRoutes.FormServiceApi.GET_FORM_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getFormById(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestParam(value = Constants.ID, required = true) String id) throws JsonProcessingException {
		Long formId = null;
		if (id.length() <= 13) {
			formId = Long.parseLong(id);
		} else if (id instanceof String) {
			FormData fData = decodeValue(String.valueOf(id));
			formId = fData.getId();
		}
		return ResponseGenerator.successResponse(formsService.getFormById(formId));
	}

	@PostMapping(value = PathRoutes.FormServiceApi.CREATE_FORM)
	public String createForm(@RequestBody FormDetail form,
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo)
			throws IOException {
		String validation = validationService.validateCreateForm(form);
		if (validation.equals(Constants.ResponseCodes.SUCCESS)) {
			validationService.validateFormStatus(form);
			UserInfo userInfo = null;
			if (StringUtils.isNotBlank(xUserInfo)) {
				userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
				form.setUpdatedBy(userInfo.getId());
			}
			Form createdForm = formsService.createForm(form);
			if (createdForm.getId() != null) {
				return ResponseGenerator.successResponse(form);
			}
			return ResponseGenerator.failureResponse(Constants.ResponseCodes.PROCESS_FAIL);
		}
		return ResponseGenerator.failureResponse(validation);
	}

	@PostMapping(value = PathRoutes.FormServiceApi.SAVE_FORM_SUBMIT_V1)
	public String saveFormSubmitv1(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestBody IncomingData incomingData) throws IOException {

		String validation = validationService.validateSubmittedApplication(incomingData);
		if (validation.equals(Constants.ResponseCodes.SUCCESS)) {
			try {
				validationService.validateApplicationStatus(incomingData);
				UserInfo userInfo = null;
				if (StringUtils.isNotBlank(xUserInfo)) {
					userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
					if (StringUtils.isBlank(incomingData.getApplicationId())) {
						incomingData.setCreatedBy(userInfo.getEmailId());
					} else {
						incomingData.setUpdatedBy(userInfo.getEmailId());
					}
				}
				if (formsService.saveFormSubmitv1(incomingData, userInfo,
						StringUtils.isBlank(incomingData.getApplicationId()) ? Status.NEW.name()
								: Status.REVIEW.name())) {
					return ResponseGenerator.successResponse(Boolean.TRUE);
				}
			} catch (Exception e) {
				logger.error(String.format(Constants.EXCEPTION, "saveFormSubmitv1", e.getMessage()));
				return ResponseGenerator.failureResponse(Constants.ResponseMessages.CHECK_REQUEST_PARAMS);
			}
			return ResponseGenerator.failureResponse();
		}
		return ResponseGenerator.failureResponse(validation);
	}

	@PostMapping(value = PathRoutes.FormServiceApi.SAVE_FORM_SUBMIT_BULK)
	public String saveFormSubmitBulk(@RequestBody List<IncomingData> incomingDataList) throws IOException {
		if (incomingDataList != null && !incomingDataList.isEmpty()) {
			for (IncomingData incomingData : incomingDataList) {
				formsService.saveFormSubmit(incomingData);
			}
		}
		return ResponseGenerator.successResponse(Boolean.TRUE);
	}

	private FormData decodeValue(String encodedValue) {
		byte[] decodedString = Base64.decodeBase64(encodedValue);
		FormData fData = new Gson().fromJson(new String(decodedString), FormData.class);
		return fData;
	}

	@PostMapping(value = PathRoutes.FormServiceApi.GET_ALL_APPLICATIONS, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAllApplications(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestBody SearchRequestDto searchRequestDto) throws JsonProcessingException {
		List<Map<String, Object>> responseData = new ArrayList<>();
		UserInfo userInfo = null;
		if (StringUtils.isNotBlank(xUserInfo)) {
			userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
		}
		responseData = formsService.getApplications(userInfo, searchRequestDto);
		if (responseData != null) {
			return ResponseGenerator.successResponse(responseData);
		}
		return ResponseGenerator.failureResponse(Constants.ResponseMessages.ERROR_MESSAGE);
	}

	@GetMapping(value = PathRoutes.FormServiceApi.GET_APPLICATIONS_STATUS_COUNT, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getApplicationsStatusCount(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo)
			throws JsonProcessingException {
		UserInfo userInfo = null;
		if (StringUtils.isNotBlank(xUserInfo)) {
			userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
		}
		KeyValueList responseData = formsService.getApplicationsStatusCount(userInfo);
		if (responseData != null) {
			return ResponseGenerator.successResponse(responseData);
		}
		return ResponseGenerator.failureResponse(Constants.ResponseMessages.ERROR_MESSAGE);
	}

	@GetMapping(value = PathRoutes.FormServiceApi.GET_APPLICATIONS_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getApplicationsById(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestParam(value = Constants.APPLICATION_ID, required = true) String applicationId)
			throws JsonProcessingException {
		UserInfo userInfo = null;
		if (StringUtils.isNotBlank(xUserInfo)) {
			userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
		}
		List<Map<String, Object>> responseData = formsService.getApplications(userInfo,
				createSearchRequestObject(applicationId));
		if (responseData != null) {
			return (responseData.isEmpty()) ? ResponseGenerator.successResponse(new HashMap<>())
					: ResponseGenerator.successResponse(responseData.get(0));
		}
		return ResponseGenerator.failureResponse(Constants.ResponseMessages.ERROR_MESSAGE);
	}

	public SearchRequestDto createSearchRequestObject(String applicationId) {
		SearchRequestDto searchRequestDto = new SearchRequestDto();
		SearchObject sObject = new SearchObject();
		sObject.setKey(Constants.APPLICATION_ID);
		sObject.setValues(applicationId);
		List<SearchObject> searchObjectList = new ArrayList<>();
		searchObjectList.add(sObject);
		searchRequestDto.setSearchObjects(searchObjectList);
		return searchRequestDto;
	}

	@PostMapping(value = PathRoutes.FormServiceApi.FILE_UPLOAD, produces = MediaType.APPLICATION_JSON_VALUE)
	public String fileUpload(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestParam(required = true) MultipartFile[] files,
			@RequestParam(value = "folderName", required = false) String folderName) throws JsonProcessingException {
		if (files != null) {
			List<String> uploadedFiles = new ArrayList<>();
			for (MultipartFile multipartFile : files) {
				String url = formsService.fileUpload(multipartFile, folderName);
				if (StringUtils.isNotBlank(url)) {
					uploadedFiles.add(url);
				}
			}
			return ResponseGenerator.successResponse(uploadedFiles);
		}
		return ResponseGenerator.failureResponse(Constants.ResponseCodes.PROCESS_FAIL);
	}

	@DeleteMapping(value = PathRoutes.FormServiceApi.DELETE_CLOUD_FILE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteCloudFile(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestBody(required = true) List<String> files) throws JsonProcessingException {
		if (formsService.deleteCloudFile(files)) {
			return ResponseGenerator.successResponse(Boolean.TRUE);
		}
		return ResponseGenerator.failureResponse(Constants.ResponseCodes.PROCESS_FAIL);
	}

	@PostMapping(value = PathRoutes.FormServiceApi.REVIEW_APPLICATION, produces = MediaType.APPLICATION_JSON_VALUE)
	public String reviewApplication(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestBody IncomingData incomingData) throws JsonProcessingException {
		String validation = validationService.validateApplicationReview(incomingData);
		UserInfo userInfo = null;
		if (validation.equals(Constants.ResponseCodes.SUCCESS)) {
			if (StringUtils.isNotBlank(xUserInfo)) {
				userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
				incomingData.setReviewedBy(userInfo.getId());
			}
			if (formsService.reviewApplication(incomingData, userInfo)) {
				return ResponseGenerator.successResponse(Boolean.TRUE);
			}
			return ResponseGenerator.failureResponse(Constants.ResponseCodes.PROCESS_FAIL);
		}
		return ResponseGenerator.failureResponse(validation);
	}

	@PostMapping(value = PathRoutes.FormServiceApi.ASSIGN)
	public String assignApplication(@RequestBody AssignApplication assign,
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo)
			throws JsonProcessingException {
		UserInfo userInfo = null;
		if (StringUtils.isNotBlank(xUserInfo)) {
			userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
			assign.setAssignedBy(userInfo.getId());
			Boolean status = formsService.assignApplication(userInfo, assign);
			if (status) {
				return ResponseGenerator.successResponse(status);
			}
		}

		return ResponseGenerator.failureResponse(Constants.ResponseCodes.PROCESS_FAIL);
	}

	@PostMapping(value = PathRoutes.FormServiceApi.SUBMIT_INSPECTION)
	public String submitInspection(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestBody IncomingData incomingData) throws IOException {
		String validation = validationService.validateInspectionObject(incomingData);
		if (validation.equals(Constants.ResponseCodes.SUCCESS)) {
			IncomingData inspectionData = new IncomingData();
			inspectionData.setInspectionCompleted(incomingData.getInspectionCompleted());
			inspectionData.setLatitude(incomingData.getLatitude());
			inspectionData.setLongitude(incomingData.getLongitude());
			inspectionData.setInspectorDataObject(incomingData);
			inspectionData.setApplicationId(incomingData.getApplicationId());
			inspectionData.setInspectorSummaryDataObject(incomingData.getInspectorSummaryDataObject());
			UserInfo userInfo = null;
			if (StringUtils.isNotBlank(xUserInfo)) {
				userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
				inspectionData.setUpdatedBy(userInfo.getEmailId());
			}
			if (formsService.submitInspection(inspectionData, userInfo)) {
				return ResponseGenerator.successResponse(Boolean.TRUE);
			}
		}
		return ResponseGenerator.failureResponse(validation);
	}

	@PostMapping(value = PathRoutes.FormServiceApi.RETURN_APPLICATION)
	public String returnApplication(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestBody IncomingData incomingData) throws IOException {
		String validation = validationService.validateReturnedApplication(incomingData);
		if (validation.equals(Constants.ResponseCodes.SUCCESS)) {
			IncomingData applicationReturn = new IncomingData();
			applicationReturn.setApplicationId(incomingData.getApplicationId());
			UserInfo userInfo = null;
			if (StringUtils.isNotBlank(xUserInfo)) {
				userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
				applicationReturn.setUpdatedBy(userInfo.getEmailId());
			}
			if (formsService.updateApplicationStatus(incomingData, userInfo,
					Constants.WorkflowActions.RETURN_APPLICATION)) {
				return ResponseGenerator.successResponse(Boolean.TRUE);
			}
		}
		return ResponseGenerator.failureResponse(validation);
	}

	@PostMapping(value = PathRoutes.FormServiceApi.APPROVE_APPLICATION)
	public String approveApplication(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestBody IncomingData incomingData) throws IOException {
		String validation = validationService.validateApprovedApplication(incomingData);
		if (validation.equals(Constants.ResponseCodes.SUCCESS)) {
			IncomingData applicationReturn = new IncomingData();
			applicationReturn.setApplicationId(incomingData.getApplicationId());
			UserInfo userInfo = null;
			if (StringUtils.isNotBlank(xUserInfo)) {
				userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
				applicationReturn.setUpdatedBy(userInfo.getEmailId());
			}
			if (formsService.updateApplicationStatus(incomingData, userInfo,
					Constants.WorkflowActions.APPROVE_APPLICATION)) {
				return ResponseGenerator.successResponse(Boolean.TRUE);
			}
		}
		return ResponseGenerator.failureResponse(validation);
	}

	@PostMapping(value = PathRoutes.FormServiceApi.REJECT_APPLICATION)
	public String rejectApplication(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestBody IncomingData incomingData) throws IOException {
		String validation = validationService.validateReturnedApplication(incomingData);
		if (validation.equals(Constants.ResponseCodes.SUCCESS)) {
			IncomingData applicationReturn = new IncomingData();
			applicationReturn.setApplicationId(incomingData.getApplicationId());
			UserInfo userInfo = null;
			if (StringUtils.isNotBlank(xUserInfo)) {
				userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
				applicationReturn.setUpdatedBy(userInfo.getEmailId());
			}
			if (formsService.updateApplicationStatus(incomingData, userInfo,
					Constants.WorkflowActions.REJECT_APPLICATION)) {
				return ResponseGenerator.successResponse(Boolean.TRUE);
			}
		}
		return ResponseGenerator.failureResponse(validation);
	}

	@GetMapping(value = PathRoutes.FormServiceApi.ACTIVITY_LOGS)
	public String getActivityLogs(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestParam(required = true) String applicationId) throws IOException {
		List<Map<String, Object>> response = formsService.getActivityLogs(applicationId);
		if (response != null) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse();
	}

	@GetMapping(value = PathRoutes.FormServiceApi.GET_ALL_FORM_STATUS)
	public String getAllFormStatus() throws IOException {
		final List<KeyValue> modelList = new ArrayList<>();
		for (final Status key : Status.values())
			modelList.add(new KeyValue(key.name(), key));
		return ResponseGenerator.successResponse(modelList);
	}

	@PostMapping(value = PathRoutes.FormServiceApi.GPS_TAGGING)
	public String gpsTagging(@RequestBody IncomingData incomingData,
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo)
			throws JsonProcessingException {
		return ResponseGenerator.successResponse(Constants.ResponseCodes.SUCCESS);
	}

	@PostMapping(value = PathRoutes.FormServiceApi.CONSENT_APPLICATION)
	public String consentApplication(@RequestBody Consent consent,
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo)
			throws JsonProcessingException {
		UserInfo userInfo = null;
		if (StringUtils.isNotBlank(xUserInfo)) {
			userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
			if (formsService.consentApplication(consent, userInfo)) {
				return ResponseGenerator.successResponse(Boolean.TRUE);
			}
		}
		return ResponseGenerator.failureResponse();
	}

	@PostMapping(value = PathRoutes.FormServiceApi.SUBMIT_BULK_INSPECTION)
	public String submitBulkInspection(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestBody List<IncomingData> incomingDataList) throws IOException {
		if (incomingDataList != null && !incomingDataList.isEmpty()) {
			for (IncomingData incomingData : incomingDataList) {
				List<IncomingData> inspectionDataList = new ArrayList<>();
				String validation = validationService.validateInspectionObject(incomingData);
				if (validation.equals(Constants.ResponseCodes.SUCCESS)) {
					IncomingData inspectionData = new IncomingData();
					inspectionData.setInspectorDataObject(incomingData);
					inspectionData.setApplicationId(incomingData.getApplicationId());
					inspectionData.setInspectorSummaryDataObject(incomingData.getInspectorSummaryDataObject());
					UserInfo userInfo = null;
					if (StringUtils.isNotBlank(xUserInfo)) {
						userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
						inspectionData.setUpdatedBy(userInfo.getEmailId());
					}
					inspectionDataList.add(inspectionData);
					formsService.submitBulkInspection(inspectionDataList, userInfo);
					return ResponseGenerator.successResponse(Boolean.TRUE);

				}
			}
		}
		return ResponseGenerator.failureResponse();
	}

	@PostMapping(value = PathRoutes.FormServiceApi.CONSENT_BULK_APPLICATION)
	public String consentBulkApplication(@RequestBody List<Consent> consentList,
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo)
			throws JsonProcessingException {
		UserInfo userInfo = null;
		if (StringUtils.isNotBlank(xUserInfo)) {
			userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
			formsService.consentBulkApplication(consentList, userInfo);
			return ResponseGenerator.successResponse(Boolean.TRUE);
		}
		return ResponseGenerator.failureResponse();
	}

	@PostMapping(value = PathRoutes.FormServiceApi.SAVE_PLAIN_FORM)
	public String savePlainForm(@RequestBody IncomingData incomingData) throws IOException {
		try {
			if (incomingData != null) {
				incomingData.setFormId(9999l);
				if (formsService.savePlainForm(incomingData)) {
					return ResponseGenerator.successResponse(Boolean.TRUE);
				}
			}

		} catch (Exception e) {
			logger.error(String.format(Constants.EXCEPTION, "savePlainForm", e.getMessage()));
			return ResponseGenerator.failureResponse(Constants.ResponseMessages.CHECK_REQUEST_PARAMS);
		}
		return ResponseGenerator.failureResponse();
	}

	@GetMapping(value = PathRoutes.FormServiceApi.GET_ALL_PLAIN_FORMS, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAllPlainForms() throws JsonProcessingException {
		List<Map<String, Object>> responseData = new ArrayList<>();
		responseData = formsService.getAllPlainForms();
		if (responseData != null) {
			return ResponseGenerator.successResponse(responseData);
		}
		return ResponseGenerator.failureResponse(Constants.ResponseMessages.ERROR_MESSAGE);
	}

	@GetMapping(value = PathRoutes.FormServiceApi.GET_PLAIN_FORM_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getPlainFormById(
			@RequestParam(value = Constants.APPLICATION_ID, required = true) String applicationId)
			throws JsonProcessingException {
		List<Map<String, Object>> responseData = formsService.getPlainFormsById(applicationId);
		if (responseData != null) {
			return (responseData.isEmpty()) ? ResponseGenerator.successResponse(new HashMap<>())
					: ResponseGenerator.successResponse(responseData.get(0));
		}
		return ResponseGenerator.failureResponse(Constants.ResponseMessages.ERROR_MESSAGE);
	}
	
	@GetMapping(value = PathRoutes.FormServiceApi.GET_INSTITUTE_FORM_DATA_EXCEL)
	public ResponseEntity<Resource> getFile(@RequestParam(value = Constants.ORG_ID, required = true) Long orgId) {
	    String filename = "institute.xlsx";
	    InputStreamResource file = new InputStreamResource(formsService.getInstituteFormData(orgId));

	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
	        .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
	        .body(file);
	  }
	
	
}
