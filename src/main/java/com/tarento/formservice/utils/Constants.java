package com.tarento.formservice.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;

public interface Constants {

	public static final String LOGO_URL = "https://cabhound-static.s3.amazonaws.com/insuranceDoc/claim/tarento_logo.png";
	public static final int MAX_EXECUTOR_THREAD = 10;

	interface ServiceRepositories {
		static final String FORM_SERVICE = "formsService";
		static final String FORM_REPO = "formDao";
		static final String ELASTICSEARCH_REPO = "elasticSearchRepository";
	}

	interface RequestMethods {
		static final String GET = "GET";
		static final String POST = "POST";
		static final String OPTIONS = "OPTIONS";
		static final String DELETE = "DELETE";
		static final String PUT = "PUT";
	}

	interface ResponseCodes {
		static final int UNAUTHORIZED_ID = 401;
		static final int SUCCESS_ID = 200;
		static final int FAILURE_ID = 320;
		static final String UNAUTHORIZED = "Invalid credentials. Please try again.";
		static final String PROCESS_FAIL = "Process failed, Please try again.";
		static final String SUCCESS = "success";
	}

	interface Parameters {
		static final String ID = "id";
		static final String X_USER_INFO = "x-user-info";
		static final String USER_ID = "userId";
		static final String ORG_ID = "orgId";
		static final String EMAIL_ID = "emailId";
		static final String FIRST_NAME = "firstName";
		static final String LAST_NAME = "lastName";
		static final String SEARCH = "search";
		static final String RESPONSE_DATA = "responseData";
		static final String DATA_OBJECT = "dataObject";
		static final String INSPECTION = "inspection";
		static final String INSPECTOR_DATA_OBJECT = "inspectorDataObject";
		static final String COMMENTS = "comments";

		static final String ACTION = "action";
		static final String FIELD = "field";
		static final String CHANGED_FROM = "ChangedFrom";
		static final String CHANGED_TO = "ChangedTo";

		static final String AUTHORIZATION = "Authorization";
		static final String BEARER = "Bearer ";

		static final String UPDATED_BY = "updatedBy";
		static final String UPDATED_DATE = "updatedDate";
		static final String UPDATED_BY_EMAIL = "updatedByEmail";

		// filters
		static final String TO_CONSENT = "toConsent";
	}

	interface WorkflowActions {
		static final String SAVE_FORM_NOTES = "Save Form Notes";
		static final String ASSIGN_INSPECTOR = "Assign Inspector";
		static final String RETURN_APPLICATION = "Return Application";
		static final String COMPLETED_INSPECTION = "Complete Inspection";
		static final String APPROVE_APPLICATION = "Approve Application";
		static final String REJECT_APPLICATION = "Reject Application";
		static final String LEAD_INSPECTION_COMPLETED = "Lead inspection";
	}

	interface ResponseMessages {
		static final String ERROR_MESSAGE = "Unable to fetch the details. Please try again later!";
		static final String PACK_HASHCODE_EMPTY = "Hashcode cannot be empty! Please try again with valid hashcode";
		static final String PACK_DISTRIBUTION_INVALID = "This pack is no longer valid. Please contact admin!";
		static final String CHECK_REQUEST_PARAMS = "Check your request paramaters.";

		// form Validation
		static final String TITLE_MISSING = "Form title is missing";
		static final String FIELD_MISSING = "Form fields is missing";
		static final String FIELD_NAME_MISSING = "Field name is missing";
		static final String FIELD_ORDER_MISSING = "Check field order";

		static final String FORM_ID_MISSING = "Form id is missing";
		static final String DATA_OBJECT_MISSING = "Data object is missing";
		static final String APPLICATION_ID_MISSING = "Application id is missing";
		static final String COMMENTS_MISSING = "Review comments is missing";
		static final String INSPECTOR_SUMMARY_MISSING = "Inspector Summary is missing";
	}

	static final String HTTP = "https://";
	public static final String TIME_ZONE = "UTC";

	// constants
	public static final String NAME = "name";
	public static final String URL = "url";
	public static final String UP_SMF = "up-smf";
	public static final String _ID = "_id";
	public static final String ID = "id";
	public static final String ORG_ID = "orgId";
	public static final String FORM_ID = "formId";
	public static final String APPLICATION_ID = "applicationId";
	public static final String STATUS = "status";
	public static final String CREATED_BY = "createdBy";
	public static final String ASSIGNED_TO = "assignedTo";
	public static final String TIMESTAMP = "timestamp";
	public static final String EMPTY_SET = "emptySet";
	public static final String VALUE = "value";
	public static final String TYPE = "type";
	public static final String BY = "by";
	public static final String LEAD_INSPECTOR = "inspectionLeadInspector";

	public static final String APPEND_KEYWORD = ".keyword";

	public static final String EXCEPTION = "Exception in %s : %s";

	interface ServiceTypes {
		public static final String APPLICATION = "Application";
	}

	interface Operations {
		public static final String CREATE = "Created";
		public static final String UPDATE = "Updated";
		public static final String REMOVE = "Removed";
	}

	interface FormFieldTypes {
		public static final String TEXT = "text";
	}

	interface ElasticSearchFields {
		public static final Map<String, String> MAPPING = new HashMap<String, String>() {
			{
				put("formId", "id");
				put("applicationId", "_id");
				put("status", "status.keyword");
				put("createdBy", "createdBy.keyword");
				put("assignedTo", "inspection.assignedTo.id");
				put("inspectionStatus", "inspection.status.keyword");
				put("inspectionLeadInspector", "inspection.leadInspector");
				put("inspectionApplicationId", "inspection.applicationId.keyword");
				put("inspectionAssignedDate", "inspection.assignedDate");
				put("inspectionUpdatedDate", "inspection.updatedDate");
			}
		};
	}

	public static String convertToTitleCase(String s) {
		if (StringUtils.isNotEmpty(s)) {
			char[] delimiters = { ' ', '_' };
			String d = WordUtils.capitalizeFully(s, delimiters);
			return d;
		}
		return " ";
	}

}
