package com.tarento.formservice.utils;

public interface Constants {

	public class SMTP {
		private SMTP() {
			super();
		}

		public static final String HOST = "smtpout.idc.tarento.com";
		public static final int PORT = 587;
		public static final boolean SSL = true;
		public static final String USER = "AKIAJJLLS652M5B32APA";
		public static final String PSWRD = "AhYkENBNOY9M6Cl29IqETcKIWsP5Z+dUNuigVKglCuU5";
		public static final String EMAIL = "timetrack@tarento.com";
		public static final String ALIAS = "aurora-desk.support";
	}

	public static final String HOST = "smtpout.idc.tarento.com";
	public static final String FROM = "timetrack@tarento.com";
	public static final String USER = "AKIAJJLLS652M5B32APA";
	public static final String PSWRD = "AhYkENBNOY9M6Cl29IqETcKIWsP5Z+dUNuigVKglCuU5";
	public static final String ALIAS = "pulz.support";
	public static final String LOGO_URL = "https://cabhound-static.s3.amazonaws.com/insuranceDoc/claim/tarento_logo.png";
	public static final int MAX_EXECUTOR_THREAD = 10;

	interface ServiceRepositories {
		static final String FORM_SERVICE = "formsService";
		static final String FORM_REPO = "formDao";
		static final String FORM_SQL_REPO = "formSqlDao";
		static final String JSON_FORMS_SERVICE = "jsonFormsService";
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
		static final String PARAMETER = "parameter";
		static final String HASHCODE = "hashcode";
		static final String PUBLISHED = "isPublished";
		static final String SAVED = "isSaved";
		static final String ACTIVE = "isActive";
		static final String DETAIL = "isDetail";
		static final String DELETED = "isDeleted";
		static final String ID = "id";
		static final String GETALL = "getAll";
		static final String CONTEXT = "dataContext";
		static final String CONTEXT_VERSION = "dataContextVersion";
		static final String VERSION = "version";
		static final String FORM_DATA = "urlCode";
	}

	interface PortfolioConstants {
		static final String FEATURED = "Featured";
		static final String PROJECT = "PROJECT";
		static final String SERVICE = "SERVICE";
		static final String COLLECTION = "COLLECTION";
		static final String PROJECT_AND_SERVICE = "PROJECT&SERVICE";
		static final String ALL_PORTFOLIO = "All";
		static final Long METADATA_PROJECT_ID = 9999l;
		static final Long ALL_COLLECTION_ID = 9999l;
		static final String BGIMAGE_KEY = "BackgroundImages";
		static final String BGCOLOR_KEY = "BackgroundColors";
		static final String LATEST_PORTFOLIO = "latestPortfolio";
		static final String ID_KEY = "_id";
		static final String VALUE_KEYWORD = "contents.dataNodes.value.keyword";
		static final String CONTENT_VALUE_KEYWORD = "projectDetails.content.value.keyword";
		static final String DISTRIBUTIONLIST_URL_KEYWORD = "distributionList.url.keyword";
		static final String DISTRIBUTIONLIST_PURPOSE_KEYWORD = "distributionList.purpose.keyword";
		static final String TAGS_KEYWORD = "tags.keyword";
	}

	interface KronosDashboards {
		static final String AS_ON_MONTH = "AsOnMonth";
		static final String IBU_NAME = "IBU Name";
		static final String RESPONSES = "responses";
		static final String RESPONSE = "response";
		static final String SOURCE_AS_MAP = "sourceAsMap";
		static final String _INDEX = "_index";
		static final String SALES_PERSRON = "Sales Person";
		static final String JOINING_DATE = "JoiningDate";
		static final String USER_ID = "User ID";
		static final String MOBILE_NO = "MobileNo";
		static final String EMAIL = "Email";
		static final String DATE = "Date";
		static final String IBU = "IBU";
		static final String COUNTRY = "Country";

	}

	interface ResponseMessages {
		static final String ERROR_MESSAGE = "Unable to fetch the details. Please try again later!";
		static final String PACK_HASHCODE_EMPTY = "Hashcode cannot be empty! Please try again with valid hashcode";
		static final String PACK_DISTRIBUTION_INVALID = "This pack is no longer valid. Please contact admin!";
		static final String CHECK_REQUEST_PARAMS = "Check your request paramaters.";
	}

	interface Markers {
		static final String DAO_IMPL = "DAOIMPL";
	}

	static final String HTTP = "https://";
	public static final String TIME_ZONE = "UTC";

	// constants
	public static final String NAME = "name";
	public static final String URL = "url";
	public static final String UP_SMF = "up-smf";
	public static final String _ID = "_id";
	public static final String ID = "id";
	public static final String FORM_ID = "formId";
	public static final String APPLICATION_ID = "applicationId";
	public static final String CREATED_BY = "createdBy";

	interface ApplicationStatus {
		public static final String SUBMITTED = "Submitted";
	}
}
