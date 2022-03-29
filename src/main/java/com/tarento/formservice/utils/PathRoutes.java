package com.tarento.formservice.utils;

public interface PathRoutes {

	public interface FormServiceApi {
		final String FORMS_ROOT = "/forms";

		final String GET_ALL_FORMS = "/getAllForms";
		final String GET_FORM_BY_ID = "/getFormById";
		final String CREATE_FORM = "/createForm";
		final String SAVE_FORM_SUBMIT_V1 = "/v1/saveFormSubmit";
		final String SAVE_FORM_SUBMIT_BULK = "/saveFormSubmitBulk";
		final String GET_ALL_APPLICATIONS = "/getAllApplications";
		final String GET_APPLICATIONS_BY_ID = "/getApplicationsById";
		final String GET_APPLICATIONS_STATUS_COUNT = "/getApplicationsStatusCount";
		final String FILE_UPLOAD = "/fileUpload";
		final String DELETE_CLOUD_FILE = "/deleteCloudFile";
		final String REVIEW_APPLICATION = "/reviewApplication";
		final String ASSIGN = "/assign";
		final String SUBMIT_INSPECTION = "/submitInspection";
		final String RETURN_APPLICATION = "/returnApplication";
		final String APPROVE_APPLICATION = "/approveApplication";
		final String REJECT_APPLICATION = "/rejectApplication";
		final String ACTIVITY_LOGS = "/getActivityLogs";
		final String GPS_TAGGING = "/gpsTagging";
		final String CONSENT_APPLICATION = "/consentApplication";
		final String GET_ALL_FORM_STATUS = "/getAllFormStatus";
		final String SUBMIT_BULK_INSPECTION = "/submitBulkInspection";
		final String CONSENT_BULK_APPLICATION = "/consentBulkApplication";
		final String SAVE_PLAIN_FORM = "/v1/savePlainForm";
		final String GET_ALL_PLAIN_FORMS = "/getAllPlainForms";
		final String GET_PLAIN_FORM_BY_ID = "/getPlainFormById";
	}

	public interface JsonFormServiceApi {
		final String JSON_FORM_ROOT = "/admin";
		final String FORMS = "/forms";
		final String UPLOAD_JSON_DATA = "/uploadFormData";
	}
}
