package com.tarento.formservice.controllers;

import java.io.File;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.tarento.formservice.model.AssignApplication;
import com.tarento.formservice.model.FormData;
import com.tarento.formservice.model.FormModel;
import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.model.KeyValueList;
import com.tarento.formservice.model.ReplyFeedbackDto;
import com.tarento.formservice.model.Role;
import com.tarento.formservice.model.SearchObject;
import com.tarento.formservice.model.SearchRequestDto;
import com.tarento.formservice.model.UserInfo;
import com.tarento.formservice.model.VerifyFeedbackDto;
import com.tarento.formservice.model.VoteFeedbackDto;
import com.tarento.formservice.models.Form;
import com.tarento.formservice.models.FormDetail;
import com.tarento.formservice.service.FormsService;
import com.tarento.formservice.service.JsonFormsService;
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

	@Value("${file.config.path}")
	public String fileDirectory;

	@Value("${file.config.name}")
	public String fileName;

	@Autowired
	private JsonFormsService jsonFormsService;

	@Autowired
	private ValidationService validationService;

	@GetMapping(value = PathRoutes.FormServiceApi.GET_ALL_FORMS, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAllForms(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo)
			throws JsonProcessingException {
		return ResponseGenerator.successResponse(formsService.getAllForms());
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
	public String createForm(@RequestBody FormDetail form) throws IOException {
		String validation = validationService.validateCreateForm(form);
		if (validation.equals(Constants.ResponseCodes.SUCCESS)) {
			Form createdForm = formsService.createForm(form);
			if (createdForm.getId() != null) {
				return ResponseGenerator.successResponse(form);
			}
			return ResponseGenerator.failureResponse(Constants.ResponseCodes.PROCESS_FAIL);
		}
		return ResponseGenerator.failureResponse(validation);
	}

	@PostMapping(value = PathRoutes.FormServiceApi.SAVE_FORM_SUBMIT)
	public String saveFormSubmit(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestBody IncomingData incomingData) throws IOException {
		Boolean status = false;
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		UserInfo userInfo = new UserInfo();
		FormData fData = new FormData();
		FormModel formModel;
		Boolean matchConfigStatus = false;
		if (incomingData != null) {
			logger.info("Incomming Data : {}", incomingData);
			formModel = mapper.readValue(new File(fileDirectory + fileName),
					// mapper.readValue(ResourceUtils.getFile("classpath:schema/FormConfig.yml"),
					// ,
					FormModel.class);
			Form plainForm = new Form();
			com.tarento.formservice.model.FormDetail fDetails = new com.tarento.formservice.model.FormDetail();
			for (int k = 0; k < formModel.getFormDetails().size(); k++) {
				if (formModel.getFormDetails().get(k).getFormId().equals(Long.toString(incomingData.getId()))) {
					fDetails = formModel.getFormDetails().get(k);
					matchConfigStatus = true;
					break;
				} else {
					plainForm.setSecondaryId(fDetails.getFormId());
				}
			}
			plainForm.setSecondaryId(fDetails.getFormId());
			plainForm.setVersion(incomingData.getVersion());
			if (matchConfigStatus)
				status = jsonFormsService.processJsonForms(incomingData.getDataObject(), plainForm, fDetails);
			else
				status = formsService.saveFormSubmit(incomingData);
		}
		if (status) {
			return ResponseGenerator.successResponse(status);
		} else {
			return ResponseGenerator.failureResponse(Constants.ResponseMessages.ERROR_MESSAGE);
		}
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
				if (formsService.saveFormSubmitv1(incomingData, userInfo)) {
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

	@PostMapping(value = PathRoutes.FormServiceApi.VERIFY_FEEDBACK)
	public String verifyFeedback(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestBody VerifyFeedbackDto verifyFeedbackDto) throws IOException {
		UserInfo userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
		Boolean stat = formsService.verifyFeedback(userInfo, verifyFeedbackDto);
		if (stat) {
			return ResponseGenerator.successResponse(stat);
		} else {
			return ResponseGenerator.failureResponse(Constants.ResponseMessages.ERROR_MESSAGE);
		}
	}

	@PostMapping(value = PathRoutes.FormServiceApi.VOTE_FEEDBACK)
	public String voteFeedback(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestBody VoteFeedbackDto voteFeedbackDto) throws IOException {
		UserInfo userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
		if (voteFeedbackDto.getCustomerId() == null) {
			voteFeedbackDto.setCustomerId(userInfo.getId());
		}
		Boolean stat = formsService.voteFeedback(userInfo, voteFeedbackDto);
		if (stat) {
			return ResponseGenerator.successResponse(stat);
		} else {
			return ResponseGenerator.failureResponse(Constants.ResponseMessages.ERROR_MESSAGE);
		}
	}

	@PostMapping(value = PathRoutes.FormServiceApi.REPLY_FEEDBACK)
	public String replyFeedback(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestBody ReplyFeedbackDto replyFeedbackDto) throws IOException {
		if (StringUtils.isNotBlank(xUserInfo)) {
			UserInfo userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
			String userRole = "";
			for (Role role : userInfo.getRoles()) {
				userRole = role.getName();
			}
			if (userRole.equals("Agent")) {
				return ResponseGenerator.successResponse(formsService.replyFeedback(userInfo, replyFeedbackDto));
			}
		}
		return ResponseGenerator.failureResponse(Constants.ResponseMessages.ERROR_MESSAGE);

	}

	@GetMapping(value = PathRoutes.FormServiceApi.GET_FEEDBACKS, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getFeedbacks(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestParam(value = "formId", required = false) Long formId,
			@RequestParam(value = "agentId", required = false) Long agentId,
			@RequestParam(value = "customerId", required = false) Long customerId,
			@RequestParam(value = "approved", required = false) String approved,
			@RequestParam(value = "challenged", required = false) String challenged,
			@RequestParam(value = "challengeStatus", required = false) Boolean challengeStatus)
			throws JsonProcessingException {
		UserInfo userInfo = null;
		if (xUserInfo != null) {
			userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
		}
		List<Map<String, Object>> formFeedback = null;
		formFeedback = formsService.getFeedbacksByFormId(formId, approved, challenged, agentId, customerId, userInfo,
				challengeStatus);
		if (formFeedback != null)
			return ResponseGenerator.successResponse(formFeedback);
		return ResponseGenerator.failureResponse(Constants.ResponseMessages.ERROR_MESSAGE);
	}

	@GetMapping(value = PathRoutes.FormServiceApi.GET_FEEDBACK_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getFeedbackById(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestParam(value = Constants.FORM_ID, required = false) Long formId) throws JsonProcessingException {
		List<Map<String, Object>> formFeedback = null;
		formFeedback = formsService.getFeedbacksByFormId(formId);
		if (formFeedback != null)
			return ResponseGenerator.successResponse(formFeedback);
		return ResponseGenerator.failureResponse(Constants.ResponseMessages.ERROR_MESSAGE);
	}

	@GetMapping(value = PathRoutes.FormServiceApi.GET_ALL_FEEDBACKS, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getFeedbacks(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestParam(value = "approved", required = false) String approved,
			@RequestParam(value = "challenged", required = false) String challenged,
			@RequestParam(value = "challengeStatus", required = false) Boolean challengeStatus,
			@RequestParam(value = "count", required = true) Boolean count) throws JsonProcessingException {
		UserInfo userInfo = null;
		if (StringUtils.isNotBlank(xUserInfo)) {
			userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
		}
		System.out.println(userInfo.toString());
		List<Map<String, Object>> formFeedback = null;
		formFeedback = formsService.getFeedbacks(approved, challenged, challengeStatus);
		if (formFeedback != null) {
			if (count)
				return ResponseGenerator.successResponse(formFeedback.size());
			else
				return ResponseGenerator.successResponse(formFeedback);
		}
		return ResponseGenerator.failureResponse(Constants.ResponseMessages.ERROR_MESSAGE);
	}

	@PostMapping(value = PathRoutes.FormServiceApi.SAVE_FORM_SUBMIT_BULK)
	public String saveFormSubmitBulk(@RequestBody List<IncomingData> incomingDataList) throws IOException {
		if (incomingDataList != null && incomingDataList.size() > 0) {
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
		KeyValueList responseData = formsService.getApplicationsStatusCount();
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
		List<Map<String, Object>> responseData = formsService.getApplications(userInfo, createSearchRequestObject(applicationId));
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
		List<SearchObject> searchObjectList = new ArrayList<SearchObject>();
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
				userInfo= new Gson().fromJson(xUserInfo, UserInfo.class);
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
			inspectionData.setInspectorDataObject(incomingData);
			inspectionData.setApplicationId(incomingData.getApplicationId());
			UserInfo userInfo = null;
			if (StringUtils.isNotBlank(xUserInfo)) {
				userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
				inspectionData.setUpdatedBy(userInfo.getEmailId());
			}
			if (formsService.saveFormSubmitv1(inspectionData, userInfo)) {
				return ResponseGenerator.successResponse(Boolean.TRUE);
			}
		}
		return ResponseGenerator.failureResponse(validation);
	}
	
	@PostMapping(value = PathRoutes.FormServiceApi.RETURN_APPLICATION)
	public String returnApplication(
			@RequestHeader(value = Constants.Parameters.X_USER_INFO, required = false) String xUserInfo,
			@RequestBody IncomingData incomingData) throws IOException {
		String validation = validationService.validateApplicationReturn(incomingData);
		if (validation.equals(Constants.ResponseCodes.SUCCESS)) {
			IncomingData applicationReturn = new IncomingData();
			applicationReturn.setApplicationId(incomingData.getApplicationId());
			UserInfo userInfo = null;
			if (StringUtils.isNotBlank(xUserInfo)) {
				userInfo = new Gson().fromJson(xUserInfo, UserInfo.class);
				applicationReturn.setUpdatedBy(userInfo.getEmailId());
			}
			if (formsService.returnApplication(incomingData, userInfo)) {
				return ResponseGenerator.successResponse(Boolean.TRUE);
			}
		}
		return ResponseGenerator.failureResponse(validation);
	}

}
