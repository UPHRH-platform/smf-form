package com.tarento.formservice.utils;

public interface PathRoutes {

	public interface FormServiceApi {
		final String FORMS_ROOT = "/forms";
		final String APPLICATION_ROOT = "/application";

		final String GET_ALL_FORMS = "/getAllForms";
		final String GET_FORM_BY_ID = "/getFormById";

		final String CREATE_FORM = "/createForm";
		final String UPLOAD_RECORDS = "/uploadRecordData";

		final String UPLOAD_USER_COURSE_DATA = "/uploadUserCourseData";

		final String UPLOAD_ASSESSMENT_DATA = "/uploadAssessmentData";

		final String UPLOAD_DATA = "/uploadData";
		final String SAVE_FORM_SUBMIT = "/saveFormSubmit";
		final String SAVE_FORM_SUBMIT_V1 = "/v1/saveFormSubmit";
		final String VERIFY_FEEDBACK = "/verifyFeedback";
		final String VOTE_FEEDBACK = "/voteFeedback";
		final String CHALLENGE_FEEDBACK = "/challengeFeedback";
		final String SAVE_FORM_SUBMIT_BULK = "/saveFormSubmitBulk";
		final String REQUEST_FEEDBACK = "/requestFeedback";

		final String SAVE_USER_CHART = "/saveUserChart";
		final String GET_CHART_BY_ID = "/getChartById";
		final String GET_ALL_CHARTS = "/getAllCharts";
		final String DELETE_CHART_BY_ID = "/deleteChartById";

		final String SAVE_USER_DASHBOARD = "/saveUserDashboard";
		final String GET_DASHBOARD_BY_ID = "/getDashboardById";
		final String GET_ALL_DASHBOARDS = "/getAllDashboards";
		final String DELETE_DASHBOARD_BY_ID = "/deleteDashboardById";

		final String GET_FEEDBACKS = "/getFeedbacks";
		final String GET_FEEDBACK_BY_ID = "/getFeedbackById";
		final String GET_ALL_FEEDBACKS = "/getAllFeedbacks";
		final String GET_OVERVIEW_COUNT = "/getOverviewCount";
		final String ADD_INTERACTION = "/addInteraction";
		final String GET_ALL_INTERACTIONS = "/getAllInteractions";
		final String GET_AGENT_AGGREGATIONS = "/getAgentAggregations";
		final String REPLY_FEEDBACK = "/replyFeedback";

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
	}

	public interface JsonFormServiceApi {
		final String JSON_FORM_ROOT = "/admin";
		final String FORMS = "/forms";
		final String UPLOAD_JSON_DATA = "/uploadFormData";
	}
}
