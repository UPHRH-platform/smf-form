package com.tarento.formservice.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mchange.v2.codegen.bean.BeangenUtils;
import com.tarento.formservice.dao.FormsDao;
import com.tarento.formservice.dao.InstituteCoursesDao;
import com.tarento.formservice.model.AssignApplication;
import com.tarento.formservice.model.Assignee;
import com.tarento.formservice.model.Consent;
import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.model.InstituteFormDataDto;
import com.tarento.formservice.model.KeyValue;
import com.tarento.formservice.model.KeyValueList;
import com.tarento.formservice.model.ResponseData;
import com.tarento.formservice.model.Result;
import com.tarento.formservice.model.Role;
import com.tarento.formservice.model.Roles;
import com.tarento.formservice.model.SearchObject;
import com.tarento.formservice.model.SearchRequestDto;
import com.tarento.formservice.model.State;
import com.tarento.formservice.model.StateMatrix;
import com.tarento.formservice.model.Status;
import com.tarento.formservice.model.UserInfo;
import com.tarento.formservice.model.WorkflowDto;
import com.tarento.formservice.models.Field;
import com.tarento.formservice.models.Form;
import com.tarento.formservice.models.FormDetail;
import com.tarento.formservice.models.InstitueFormExcelDto;
import com.tarento.formservice.repository.ElasticSearchRepository;
import com.tarento.formservice.repository.RestService;
import com.tarento.formservice.service.ActivityService;
import com.tarento.formservice.service.FormsService;
import com.tarento.formservice.utils.AppConfiguration;
import com.tarento.formservice.utils.CloudStorage;
import com.tarento.formservice.utils.Constants;
import com.tarento.formservice.utils.DateUtils;
import com.tarento.formservice.utils.ExcelHelper;
import com.tarento.formservice.utils.WorkflowUtil;
import com.tarento.formservice.utils.NotificationService.NotificationUtil;

@Service(Constants.ServiceRepositories.FORM_SERVICE)
public class FormsServiceImpl implements FormsService {

	public static final Logger LOGGER = LoggerFactory.getLogger(FormsServiceImpl.class);

	Gson gson = new Gson();
	ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private ElasticSearchRepository elasticRepository;

	@Autowired
	private FormsDao formsDao;

	@Autowired
	private AppConfiguration appConfig;

	@Autowired
	private ActivityService activityService;
	
	@Autowired
	InstituteCoursesDao instituteCoursesDao;

	@Override
	public Form createForm(FormDetail newForm) throws IOException {
		Boolean response = Boolean.FALSE;
		if (newForm.getId() != null)
			performVersionCheck(newForm);

		if (newForm.getId() != null) {
			newForm.setUpdatedDate(new Date().getTime());
			response = formsDao.updateForm(newForm);
		} else {
			newForm.setId(new Date().getTime());
			newForm.setUpdatedDate(new Date().getTime());
			newForm.setVersion(1);
			addAdditionalMandatoryFormFields(newForm);
			response = formsDao.addForm(newForm);
		}
		return (response) ? newForm : null;

	}

	private void addAdditionalMandatoryFormFields(FormDetail newForm) {
		String jsonContent = "[{\"refApi\":\"\",\"logicalGroupCode\":\"\",\"name\":\"heading\",\"fieldType\":\"heading\",\"values\":[{\"heading\":\"Inspection Summary\",\"subHeading\":\"Summary section where inspector is expected to add a detailed statement\",\"additionalProperties\":{}}],\"isRequired\":false,\"order\":1,\"additionalProperties\":{}},{\"refApi\":\"\",\"logicalGroupCode\":\"\",\"name\":\"Enter the summary of this inspection\",\"fieldType\":\"textarea\",\"values\":[],\"isRequired\":false,\"order\":2,\"additionalProperties\":{}},{\"refApi\":\"\",\"logicalGroupCode\":\"\",\"name\":\"Terms and Conditions\",\"fieldType\":\"checkbox\",\"values\":[{\"additionalProperties\":{\"value\":\"I accept the terms and conditions laid out by UP SMF\",\"key\":\"accept\"}}],\"isRequired\":false,\"order\":3,\"additionalProperties\":{}}]";
		try {
			List<Field> inspectionFields = objectMapper.readValue(jsonContent, new TypeReference<List<Field>>() {
			});
			newForm.setInspectionFields(inspectionFields);
		} catch (Exception e) {
			LOGGER.error("Encountered Error : {}", e.getMessage());

		}

	}

	private void performVersionCheck(Form newForm) {
		MultiSearchResponse response = elasticRepository
				.executeMultiSearchRequest(createRequestForVersionCheck(newForm));
		SearchResponse searchResponse = response.getResponses()[0].getResponse();
		if (searchResponse != null) {
			for (SearchHit hit : searchResponse.getHits()) {
				Form existingForm = gson.fromJson(hit.getSourceAsString(), Form.class);
				if (existingForm.getId().equals(newForm.getId()) && existingForm.getVersion() == newForm.getVersion()) {
					int nextVersion = newForm.getVersion() + 1;
					newForm.setVersion(nextVersion);
				}
			}
		}

	}

	private SearchRequest createRequestForVersionCheck(Form newForm) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
		BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
		boolBuilder.must().add(QueryBuilders.matchQuery(Constants.Parameters.ID, newForm.getId()));
		searchSourceBuilder.query(boolBuilder);
		SearchRequest sRequest;
		sRequest = new SearchRequest(appConfig.getFormIndex()).source(searchSourceBuilder);
		return sRequest;
	}

	@Override
	public List<Form> getAllForms(UserInfo userInfo, Boolean isDetail) {
		List<Form> formList = new ArrayList<>();
		SearchRequest searchRequest = buildQueryForGetAllForms(userInfo);
		MultiSearchResponse response = elasticRepository.executeMultiSearchRequest(searchRequest);
		SearchResponse searchResponse = response.getResponses()[0].getResponse();
		JsonNode responseNode = null;
		if (searchResponse != null) {
			responseNode = new ObjectMapper().convertValue(searchResponse.getAggregations(), JsonNode.class);
			JsonNode aggregationNode = responseNode.findValue("UniqueFormId");
			if (aggregationNode.has("buckets")) {
				JsonNode buckets = aggregationNode.findValue("buckets");
				for (JsonNode bucket : buckets) {
					@SuppressWarnings("unused")
					JsonNode latestVersionNode = bucket.findValue("LatestVersion");
					JsonNode hitsNode = latestVersionNode.get("hits");
					if (hitsNode.has("hits")) {
						JsonNode innerHits = hitsNode.findValue("hits");
						for (JsonNode eachInnerHit : innerHits) {
							Form form = null;
							if (isDetail != null && isDetail) {
								form = gson.fromJson(eachInnerHit.findValue("sourceAsMap").toString(),
										FormDetail.class);
							} else {
								form = gson.fromJson(eachInnerHit.findValue("sourceAsMap").toString(), Form.class);
							}
							form.setNumberOfRecords((long) randInt(1, 1000));
							formList.add(form);
						}
					}
				}
			}
			LOGGER.info("Form List: {}", gson.toJson(formList));
		}
		return formList;
	}

	public static int randInt(int min, int max) {
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}

	@Override
	public FormDetail getFormById(Long id) {
		FormDetail form = new FormDetail();
		SearchRequest searchRequest = buildQueryForGetQueryById(id);
		MultiSearchResponse response = elasticRepository.executeMultiSearchRequest(searchRequest);
		SearchResponse searchResponse = response.getResponses()[0].getResponse();
		JsonNode responseNode = null;
		if (searchResponse != null) {
			responseNode = new ObjectMapper().convertValue(searchResponse.getAggregations(), JsonNode.class);
			JsonNode aggregationNode = responseNode.findValue("UniqueFormId");
			if (aggregationNode.has("buckets")) {
				JsonNode buckets = aggregationNode.findValue("buckets");
				for (JsonNode bucket : buckets) {
					JsonNode latestVersionNode = bucket.findValue("LatestVersion");
					JsonNode hitsNode = latestVersionNode.get("hits");
					if (hitsNode.has("hits")) {
						JsonNode innerHits = hitsNode.findValue("hits");
						for (JsonNode eachInnerHit : innerHits) {
							form = gson.fromJson(eachInnerHit.findValue("sourceAsMap").toString(), FormDetail.class);
							LOGGER.info("Each Form : {}", gson.toJson(form));
						}
					}

				}
			}
		}
		return form;
	}

	private SearchRequest buildQueryForGetAllForms(UserInfo userInfo) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0).query(roleBasedFormQuery(userInfo))
				.aggregation(AggregationBuilders.terms("UniqueFormId").field("id").size(100)
						.order(BucketOrder.key(Boolean.TRUE))
						.subAggregation(AggregationBuilders.topHits("LatestVersion").from(0).size(1)
								.version(Boolean.FALSE).explain(Boolean.FALSE)
								.sort(SortBuilders.fieldSort("version").order(SortOrder.DESC))));
		return new SearchRequest(appConfig.getFormIndex()).source(searchSourceBuilder);
	}

	private BoolQueryBuilder roleBasedFormQuery(UserInfo userInfo) {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if (userInfo != null) {
			// Returning all the published & unpublished forms for regulator & inspector
			// login and only published form for institute login
			for (Role role : userInfo.getRoles()) {
				if (role.getName().equalsIgnoreCase(Roles.INSTITUTION.name())) {
					boolQuery.must(QueryBuilders.matchPhraseQuery(Constants.STATUS, Status.PUBLISH.name()));
				} else if (role.getName().equalsIgnoreCase(Roles.REGULATOR.name())
						|| role.getName().equalsIgnoreCase(Roles.INSPECTOR.name())) {
					boolQuery
							.should(QueryBuilders.boolQuery()
									.mustNot(QueryBuilders.matchPhraseQuery(Constants.STATUS, Status.DRAFT.name())))
							.should(QueryBuilders.boolQuery()
									.must(QueryBuilders.matchPhraseQuery(Constants.STATUS, Status.DRAFT.name()))
									.must(QueryBuilders.matchPhraseQuery(Constants.Parameters.UPDATED_BY,
											userInfo.getId())));
				}
			}
		} else {
			boolQuery.must(QueryBuilders.matchPhraseQuery(Constants.STATUS, Status.PUBLISH.name()));
		}

		return boolQuery;
	}

	private SearchRequest buildQueryForGetQueryById(Long id) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0)
				.query(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("id", id)))
				.aggregation(AggregationBuilders.terms("UniqueFormId").field("id").size(100)
						.subAggregation(AggregationBuilders.topHits("LatestVersion").from(0).size(1)
								.version(Boolean.FALSE).explain(Boolean.FALSE)
								.sort(SortBuilders.fieldSort("version").order(SortOrder.DESC))));
		return new SearchRequest(appConfig.getFormIndex()).source(searchSourceBuilder);
	}

	@Override
	public Boolean saveFormSubmit(IncomingData incomingData) throws IOException {
		return formsDao.addFormData(incomingData);
	}

	@Override
	public ResponseData fetchUserInfo(Long userId) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			final String uri = "http://localhost:8081/user/getUserById?id=" + userId + "&orgId=5001";
			HttpHeaders headers = new HttpHeaders();
			headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri).queryParam("id", userId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<String> result = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
					String.class);
			if (result.getStatusCode() == HttpStatus.OK && !StringUtils.isEmpty(result.getBody())) {
				Gson gson = new Gson();
				Result rs = gson.fromJson(result.getBody(), Result.class);
				if (rs.getResponseData() != null) {
					return rs.getResponseData();
				}
			}
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an error while fetching user info object in userservice :  %s",
					e.getMessage()));
		}
		return null;
	}

	public Boolean challengeFeedback(String id, String reason) throws IOException {
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("challengeStatus", true);
		jsonMap.put("reasonForChallenge", reason);
		return formsDao.updateFormData(jsonMap, id);
	}

	@Override
	public List<Map<String, Object>> getApplications(UserInfo userInfo, SearchRequestDto searchRequestDto) {
		try {
			// query builder
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
			BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
			setRoleBasedSearchObject(userInfo, searchRequestDto);
			setRoleBasedExcludeSearchObject(userInfo, searchRequestDto);
			if (searchRequestDto != null) {
				if (searchRequestDto.getSearchObjects() != null) {
					for (SearchObject objects : searchRequestDto.getSearchObjects()) {
						String key = objects.getKey();
						Object values = objects.getValues();
						if (Constants.ElasticSearchFields.MAPPING.containsKey(key)) {
							boolBuilder.must().add(
									QueryBuilders.termsQuery(Constants.ElasticSearchFields.MAPPING.get(key), values));
							/*
							 * boolBuilder.must()
							 * .add(QueryBuilders.matchQuery(Constants.ElasticSearchFields.MAPPING.get(key),
							 * values));
							 */
						} else {
							// In the case where UI tries to send random values which are not configured in
							// our ES Mapping, the API should send empty set as a response.
							// So here, we just query as empty set and we know that we will get empty set as
							// a response
							boolBuilder.must().add(QueryBuilders.matchQuery(Constants.EMPTY_SET, Constants.EMPTY_SET));
						}
					}
				}
				if (searchRequestDto.getExcludeObject() != null) {
					for (SearchObject objects : searchRequestDto.getExcludeObject()) {
						String key = objects.getKey();
						Object values = objects.getValues();
						if (Constants.ElasticSearchFields.MAPPING.containsKey(key)) {
							boolBuilder.mustNot().add(
									QueryBuilders.termsQuery(Constants.ElasticSearchFields.MAPPING.get(key), values));
							/*
							 * boolBuilder.must()
							 * .add(QueryBuilders.matchQuery(Constants.ElasticSearchFields.MAPPING.get(key),
							 * values));
							 */
						} else {
							// In the case where UI tries to send random values which are not configured in
							// our ES Mapping, the API should send empty set as a response.
							// So here, we just query as empty set and we know that we will get empty set as
							// a response
							boolBuilder.must().add(QueryBuilders.matchQuery(Constants.EMPTY_SET, Constants.EMPTY_SET));
						}
					}
				}
			}
			searchSourceBuilder.query(boolBuilder).sort(Constants.TIMESTAMP, SortOrder.DESC);
			// es call
			SearchRequest searchRequest = new SearchRequest(appConfig.getFormDataIndex()).source(searchSourceBuilder);
			LOGGER.info("Search Request : " + searchRequest);
			List<Map<String, Object>> response = formsDao.searchResponse(searchRequest);
			if (searchRequestDto != null && searchRequestDto.getFilterObjects() != null) {
				return filterSearchResults(response, searchRequestDto.getFilterObjects(), userInfo);
			}
			return response;

		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "getApplications", e.getMessage()));
		}
		return null;
	}

	/**
	 * Add filters in search response
	 */
	private List<Map<String, Object>> filterSearchResults(List<Map<String, Object>> response,
			List<SearchObject> filterObjects, UserInfo userInfo) throws Exception {
		if (filterObjects != null && filterObjects.size() > 0) {
			List<Map<String, Object>> filteredResponse = new ArrayList<>();
			for (SearchObject searchObj : filterObjects) {
				if (searchObj.getKey().equals(Constants.Parameters.TO_CONSENT)
						&& searchObj.getValues().equals(Boolean.TRUE)) {
					for (Map<String, Object> responseObj : response) {
						IncomingData incomingData = objectMapper.convertValue(responseObj, IncomingData.class);
						if (incomingData.getInspection() != null
								&& incomingData.getInspection().getStatus().equals(Status.LEADINSCOMPLETED.name())
								&& incomingData.getInspection().getAssignedTo() != null) {
							for (Assignee assignee : incomingData.getInspection().getAssignedTo()) {
								if (assignee.getId().equals(userInfo.getId())
										&& StringUtils.isBlank(assignee.getStatus())) {
									filteredResponse.add(responseObj);
								}
							}
						}
					}
				}
			}
			return filteredResponse;
		}
		return response;
	}

	private void setRoleBasedSearchObject(UserInfo userInfo, SearchRequestDto searchRequestDto) {
		if (userInfo != null && userInfo.getRoles() != null) {
			for (Role role : userInfo.getRoles()) {
				SearchObject roleBasedSearch = new SearchObject();
				if (role.getName().equalsIgnoreCase(Roles.INSTITUTION.name())) {
					roleBasedSearch.setKey(Constants.CREATED_BY);
					roleBasedSearch.setValues(userInfo.getEmailId());
				} else if (role.getName().equalsIgnoreCase(Roles.INSPECTOR.name())) {
					roleBasedSearch.setKey(Constants.ASSIGNED_TO);
					roleBasedSearch.setValues(userInfo.getId());
				}
				if (searchRequestDto.getSearchObjects() != null && StringUtils.isNotBlank(roleBasedSearch.getKey())) {
					searchRequestDto.getSearchObjects().add(roleBasedSearch);
				} else if (StringUtils.isNotBlank(roleBasedSearch.getKey())) {
					List<SearchObject> searchObjectList = new ArrayList<>();
					searchObjectList.add(roleBasedSearch);
					searchRequestDto.setSearchObjects(searchObjectList);
				}
			}
		}
	}

	private void setRoleBasedExcludeSearchObject(UserInfo userInfo, SearchRequestDto searchRequestDto) {
		if (userInfo != null && userInfo.getRoles() != null) {
			for (Role role : userInfo.getRoles()) {
				if (role.getName().equalsIgnoreCase(Roles.REGULATOR.name())
						|| role.getName().equalsIgnoreCase(Roles.INSPECTOR.name())) {
					SearchObject roleBasedSearch = new SearchObject();
					roleBasedSearch.setKey(Constants.STATUS);
					roleBasedSearch.setValues(Status.DRAFT.name());
					if (searchRequestDto.getExcludeObject() != null) {
						searchRequestDto.getExcludeObject().add(roleBasedSearch);
					} else if (StringUtils.isNotBlank(roleBasedSearch.getKey())) {
						List<SearchObject> searchObjectList = new ArrayList<>();
						searchObjectList.add(roleBasedSearch);
						searchRequestDto.setExcludeObject(searchObjectList);
					}
				}

			}
		}
	}

	@Override
	public KeyValueList getApplicationsStatusCount(UserInfo userInfo) {
		try {
			// query builder
			if (userInfo != null && userInfo.getRoles() != null) {
				for (Role role : userInfo.getRoles()) {
					if (role.getName().equalsIgnoreCase(Roles.REGULATOR.name())) {
						SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
						searchSourceBuilder.aggregation(AggregationBuilders.terms("Total Pending")
								.field(Constants.ElasticSearchFields.MAPPING.get(Constants.STATUS)));
						SearchRequest searchRequest = new SearchRequest(appConfig.getFormDataIndex())
								.source(searchSourceBuilder);
						LOGGER.info("Search Request : " + searchRequest);
						List<Map<String, Object>> responseNode = formsDao.searchAggregationResponse(searchRequest,
								"Total Pending");
						return translateResponse(responseNode);
					} else {
						// Setting all prerequisites
						List<Long> userIdList = new ArrayList<Long>();
						userIdList.add(userInfo.getId());
						Calendar cal = Calendar.getInstance();
						cal.setTime(new Date());
						KeyValueList finalList = new KeyValueList();
						List<KeyValue> keyValueList = new ArrayList<>();
						finalList.setKeyValues(keyValueList);
						Calendar startOfTodayCal = Calendar.getInstance();
						startOfTodayCal.setTime(new Date());
						startOfTodayCal.add(Calendar.DAY_OF_YEAR, -1);
						startOfTodayCal.set(Calendar.HOUR, 11);
						startOfTodayCal.set(Calendar.AM_PM, Calendar.PM);
						startOfTodayCal.set(Calendar.MINUTE, 59);

						Calendar endOfTodayCal = Calendar.getInstance();
						endOfTodayCal.setTime(new Date());
						endOfTodayCal.set(Calendar.HOUR, 11);
						endOfTodayCal.set(Calendar.AM_PM, Calendar.PM);
						endOfTodayCal.set(Calendar.MINUTE, 59);

						// Creating Search Request for Total Pending
						TermsQueryBuilder userIdFilter = QueryBuilders.termsQuery(
								Constants.ElasticSearchFields.MAPPING.get("inspectionLeadInspector"), userIdList);
						TermQueryBuilder inspectionStatusFilter = QueryBuilders.termQuery(
								Constants.ElasticSearchFields.MAPPING.get("inspectionStatus"),
								Status.SENTFORINS.name());
						TermQueryBuilder statusFilter = QueryBuilders.termQuery(
								Constants.ElasticSearchFields.MAPPING.get("status"), Status.SENTFORINS.name());
						BoolQueryBuilder filters = QueryBuilders.boolQuery().filter(userIdFilter)
								.filter(inspectionStatusFilter).filter(statusFilter);
						FilterAggregationBuilder totalPendingAggregationFilter = AggregationBuilders
								.filter("Inspector Total Pending", filters)
								.subAggregation(AggregationBuilders.cardinality("Count")
										.field(Constants.ElasticSearchFields.MAPPING.get("inspectionApplicationId")));
						SearchSourceBuilder totalPendingAggrSourceBuilder = new SearchSourceBuilder().size(0);
						totalPendingAggrSourceBuilder.aggregation(totalPendingAggregationFilter);
						SearchRequest totalPendingSearchRequest = new SearchRequest(appConfig.getFormDataIndex())
								.source(totalPendingAggrSourceBuilder);
						LOGGER.info("Search Request : " + totalPendingSearchRequest);
						List<Map<String, Object>> totalPendingResponse = formsDao
								.searchAggregationResponse(totalPendingSearchRequest, "Inspector Total Pending");
						KeyValueList list = translateResponse(totalPendingResponse);
						finalList.getKeyValues().addAll(list.getKeyValues());

						// Creating Search Request for Received Today
						RangeQueryBuilder assignedDateFilter = QueryBuilders
								.rangeQuery(Constants.ElasticSearchFields.MAPPING.get("inspectionAssignedDate"))
								.from(startOfTodayCal.getTime().getTime()).to(endOfTodayCal.getTime().getTime());
						BoolQueryBuilder filters2 = QueryBuilders.boolQuery().filter(userIdFilter)
								.filter(assignedDateFilter);
						FilterAggregationBuilder receivedTodayAggregationFilter = AggregationBuilders
								.filter("Received Today", filters2)
								.subAggregation(AggregationBuilders.cardinality("Count")
										.field(Constants.ElasticSearchFields.MAPPING.get("inspectionApplicationId")));
						SearchSourceBuilder receivedTodayAggrSourceBuilder = new SearchSourceBuilder().size(0);
						receivedTodayAggrSourceBuilder.aggregation(receivedTodayAggregationFilter);
						SearchRequest receivedTodaySearchRequest = new SearchRequest(appConfig.getFormDataIndex())
								.source(receivedTodayAggrSourceBuilder);
						LOGGER.info("Search Request : " + receivedTodaySearchRequest);
						List<Map<String, Object>> receivedTodayResponse = formsDao
								.searchAggregationResponse(receivedTodaySearchRequest, "Received Today");
						list = translateResponse(receivedTodayResponse);
						finalList.getKeyValues().addAll(list.getKeyValues());

						// Creating Search Request for Reviewed Today
						RangeQueryBuilder updatedDateFilter = QueryBuilders.rangeQuery("inspectionDate")
								.from(startOfTodayCal.getTime().getTime()).to(endOfTodayCal.getTime().getTime());
						BoolQueryBuilder filters3 = QueryBuilders.boolQuery().filter(userIdFilter)
								.filter(updatedDateFilter);
						FilterAggregationBuilder reviewedTodayAggregationFilter = AggregationBuilders
								.filter("Reviewed Today", filters3)
								.subAggregation(AggregationBuilders.cardinality("Count")
										.field(Constants.ElasticSearchFields.MAPPING.get("inspectionApplicationId")));
						SearchSourceBuilder reviewedTodayAggrSourceBuilder = new SearchSourceBuilder().size(0);
						reviewedTodayAggrSourceBuilder.aggregation(reviewedTodayAggregationFilter);
						SearchRequest reviewedTodaySearchRequest = new SearchRequest(appConfig.getFormDataIndex())
								.source(reviewedTodayAggrSourceBuilder);
						LOGGER.info("Search Request : " + reviewedTodaySearchRequest);
						List<Map<String, Object>> reviewedTodayResponse = formsDao
								.searchAggregationResponse(reviewedTodaySearchRequest, "Reviewed Today");
						list = translateResponse(reviewedTodayResponse);
						finalList.getKeyValues().addAll(list.getKeyValues());

						return finalList;

					}
				}
			}

		} catch (Exception ex) {
			LOGGER.error(String.format(Constants.EXCEPTION, "getApplicationsStatusCount", ex.getMessage()));
		}
		return null;

	}

	KeyValueList translateResponse(List<Map<String, Object>> responseNode) {
		KeyValueList list = new KeyValueList();
		List<KeyValue> listOfKeyValuePairs = new ArrayList<KeyValue>();
		for (Map<String, Object> eachMap : responseNode) {
			List<KeyValue> keyValueList = eachMap.entrySet().stream().filter(entry -> !"DRAFT".equals(entry.getKey()))
					.map(entry -> new KeyValue(entry.getKey().equals("NEW") ? "Total Pending" : entry.getKey(),
							entry.getValue()))
					.collect(Collectors.toList());
			listOfKeyValuePairs.addAll(keyValueList);
		}
		list.setKeyValues(listOfKeyValuePairs);
		return list;
	}

	KeyValueList translateInspectorResponse(List<Map<String, Object>> responseNode) {
		KeyValueList list = new KeyValueList();
		List<KeyValue> listOfKeyValuePairs = new ArrayList<KeyValue>();
		for (Map<String, Object> eachMap : responseNode) {
			List<KeyValue> keyValueList = eachMap.entrySet().stream()
					.map(entry -> new KeyValue(entry.getKey(), entry.getValue())).collect(Collectors.toList());
			listOfKeyValuePairs.addAll(keyValueList);
		}
		list.setKeyValues(listOfKeyValuePairs);
		return list;
	}

	@Override
	public Boolean saveFormSubmitv1(IncomingData incomingData, UserInfo userInfo, String action) {
		Boolean indexed = Boolean.FALSE;
		try {
			if (StringUtils.isBlank(incomingData.getApplicationId())) {
				incomingData.setTimestamp(DateUtils.getCurrentTimestamp());
				incomingData.setCreatedDate(DateUtils.getYyyyMmDdInUTC());
				indexed = formsDao.addFormData(incomingData);
				appStatusTrack(indexed, objectMapper.convertValue(incomingData, Map.class), action, userInfo);
			} else {
				Map<String, Object> applicationObject = getApplicationById(incomingData.getApplicationId(), userInfo);
				incomingData.setUpdatedDate(DateUtils.getYyyyMmDdInUTC());
				indexed = formsDao.updateFormData(incomingData, incomingData.getApplicationId());
				appStatusTrack(indexed, applicationObject, action, userInfo);
			}
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "saveFormSubmitv1", e.getMessage()));
		}
		return indexed;
	}

	@Override
	public Boolean savePlainForm(IncomingData incomingData) {
		Boolean indexed = null;
		try {
			indexed = formsDao.addPlainFormData(incomingData);
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "savePlainForm", e.getMessage()));
		}
		return indexed;
	}

	@Override
	public Map<String, Object> getApplicationById(String applicationId, UserInfo userInfo) {
		SearchRequestDto searchRequestDto = new SearchRequestDto();
		SearchObject sObject = new SearchObject();
		sObject.setKey(Constants.APPLICATION_ID);
		sObject.setValues(applicationId);
		List<SearchObject> searchObjectList = new ArrayList<SearchObject>();
		searchObjectList.add(sObject);
		searchRequestDto.setSearchObjects(searchObjectList);
		List<Map<String, Object>> responseData = getApplications(userInfo, searchRequestDto);
		if (responseData != null) {
			return (responseData.isEmpty()) ? new HashMap<>() : responseData.get(0);
		}
		return null;
	}

	@Override
	public String fileUpload(MultipartFile multipartFile, String folderName) {
		try {
			String folderPath = Constants.UP_SMF;
			if (StringUtils.isNotBlank(folderName)) {
				folderPath = folderPath + "/" + folderName;
			}
			File file = new File(multipartFile.getOriginalFilename());
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(multipartFile.getBytes());
			fos.close();
			Map<String, String> uploadedFile = CloudStorage.uploadFile(folderPath, file);
			file.delete();
			return uploadedFile.get(Constants.URL);
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "fileUpload", e.getMessage()));
			return null;
		}
	}

	@Override
	public Boolean deleteCloudFile(List<String> files) {
		try {
			for (String file : files) {
				String fileName = file;
				String[] nameList = file.split("/" + appConfig.getContainerName() + "/");
				if (nameList.length > 1) {
					fileName = nameList[1];
				}
				CloudStorage.deleteFile(fileName);
			}
			return Boolean.TRUE;
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "deleteCloudFile", e.getMessage()));
		}
		return Boolean.FALSE;
	}

	@Override
	public Boolean reviewApplication(IncomingData incomingData, UserInfo userInfo) {
		try {
			SearchRequestDto srd = createSearchRequestObject(incomingData.getApplicationId());
			List<Map<String, Object>> applicationMap = getApplications(userInfo, srd);
			for (Map<String, Object> innerMap : applicationMap) {
				if (innerMap.containsKey(Constants.STATUS)) {
					incomingData.setStatus(innerMap.get(Constants.STATUS).toString());
				}
			}
			incomingData.setReviewedDate(DateUtils.getYyyyMmDdInUTC());
			WorkflowDto workflowDto = new WorkflowDto(incomingData, userInfo,
					Constants.WorkflowActions.SAVE_FORM_NOTES);
			WorkflowUtil.getNextStateForMyRequest(workflowDto);
			incomingData.setStatus(workflowDto.getNextState());
			return formsDao.updateFormData(incomingData, incomingData.getApplicationId());
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "reviewApplication", e.getMessage()));
			return Boolean.FALSE;
		}

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

	@Override
	public Boolean assignApplication(UserInfo userInfo, AssignApplication assign) {
		try {
			SearchRequestDto srd = createSearchRequestObject(assign.getApplicationId());
			List<Map<String, Object>> applicationMap = getApplications(userInfo, srd);
			for (Map<String, Object> innerMap : applicationMap) {
				if (innerMap.containsKey(Constants.STATUS)) {
					assign.setStatus(innerMap.get(Constants.STATUS).toString());
				}
				if (innerMap.containsKey(Constants.FORM_ID)) {
					assign.setFormId(Long.parseLong(innerMap.get(Constants.FORM_ID).toString()));
				}
			}
			WorkflowDto workflowDto = new WorkflowDto(assign, userInfo, Constants.WorkflowActions.ASSIGN_INSPECTOR);
			WorkflowUtil.getNextStateForMyRequest(workflowDto);
			assign.setStatus(workflowDto.getNextState());
			if (assign.getLeadInspector() == null) {
				assign.setLeadInspector(new ArrayList<>());
			}
			if (assign.getAssistingInspector() == null) {
				assign.setAssistingInspector(new ArrayList<>());
			}
			if (assign.getAssistingInspector().size() > 0 || assign.getLeadInspector().size() > 0) {
				List<Long> inspectorsId = new ArrayList<>();
				inspectorsId.addAll(assign.getAssistingInspector());
				inspectorsId.addAll(assign.getLeadInspector());
				Map<String, Map<String, Object>> userMap = getUserDetails(inspectorsId, userInfo.getOrgId(),
						userInfo.getAuthToken());
				// set assigned user meta data
				assign.setAssignedTo(new ArrayList<>());
				for (Long userId : inspectorsId) {
					Assignee assignee = new Assignee();
					assignee.setId(userId);
					String key = String.valueOf(userId);
					if (userMap.containsKey(key)) {
						assignee.setEmailId((String) userMap.get(key).get(Constants.Parameters.EMAIL_ID));
						assignee.setFirstName((String) userMap.get(key).get(Constants.Parameters.FIRST_NAME));
						assignee.setLastName((String) userMap.get(key).get(Constants.Parameters.LAST_NAME));
						if (assign.getLeadInspector().contains(userId)) {
							assignee.setLeadInspector(Boolean.TRUE);
						}
					}
					assign.getAssignedTo().add(assignee);
				}

				IncomingData requestData = new IncomingData();
				assign.setAssignedDate(DateUtils.getYyyyMmDdInUTC());
				requestData.setInspection(assign);
				requestData.setStatus(assign.getStatus());

				
				LOGGER.info(objectMapper.writeValueAsString(requestData));
				LOGGER.info(objectMapper.writeValueAsString(assign));
				Boolean response = formsDao.updateFormData(requestData, assign.getApplicationId());
				appStatusTrack(response,
						(applicationMap != null && applicationMap.size() > 0 ? applicationMap.get(0) : null),
						Constants.WorkflowActions.ASSIGN_INSPECTOR, userInfo);
				return response;
			}
			return Boolean.TRUE;
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "assignApplication", e.getMessage()));
			return Boolean.FALSE;
		}
	}

	/**
	 * Makes rest call with user service to fetch the user details
	 * 
	 * @param userId
	 *            Object
	 * @param authToken
	 *            String
	 * @return
	 */
	private Map<String, Map<String, Object>> getUserDetails(Object userId, Object orgId, String authToken)
			throws Exception {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put(Constants.Parameters.ORG_ID, orgId);
		requestBody.put(Constants.Parameters.SEARCH, new HashMap<String, Object>() {
			{
				put(Constants.Parameters.USER_ID, userId);
			}
		});
		if (!authToken.toUpperCase().contains((Constants.Parameters.BEARER).toUpperCase())) {
			authToken = Constants.Parameters.BEARER + authToken;
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(Constants.Parameters.AUTHORIZATION, authToken);
		Object response = RestService.postRequest(headers,
				appConfig.getUserServiceHost() + appConfig.getGetAllUserApiPath(), requestBody);
		if (response != null) {
			List<Map<String, Object>> userList = objectMapper.convertValue(response,
					new TypeReference<List<Map<String, Object>>>() {
					});
			return userList.stream()
					.collect(Collectors.toMap(obj -> String.valueOf(obj.get(Constants.Parameters.ID)), obj -> obj));
		}
		return null;
	}

	public ConcurrentMap<Long, State> fetchAllStates() {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
		// es call
		SearchRequest searchRequest = new SearchRequest(appConfig.getFormStateIndex()).source(searchSourceBuilder);
		LOGGER.info("Search Request : " + searchRequest);
		return formsDao.fetchAllStates(searchRequest);
	}

	@Override
	public ConcurrentMap<String, List<StateMatrix>> fetchAllStateMatrix() {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
		// es call
		SearchRequest searchRequest = new SearchRequest(appConfig.getFormStateMatrixIndex())
				.source(searchSourceBuilder);
		LOGGER.info("Search Request : " + searchRequest);
		return formsDao.fetchAllStateMatrix(searchRequest);
	}

	@Override
	public Boolean updateApplicationStatus(IncomingData incomingData, UserInfo userInfo, String status) {
		try {
			SearchRequestDto srd = createSearchRequestObject(incomingData.getApplicationId());
			List<Map<String, Object>> applicationMap = getApplications(userInfo, srd);
			for (Map<String, Object> innerMap : applicationMap) {
				if (innerMap.containsKey(Constants.STATUS)) {
					incomingData.setStatus(innerMap.get(Constants.STATUS).toString());
					incomingData.setComments(
							innerMap.containsKey(Constants.Parameters.COMMENTS) ? objectMapper.convertValue(
									innerMap.get(Constants.Parameters.COMMENTS), new TypeReference<List<Object>>() {
									}) : null);
				}
			}
			incomingData.setReviewedDate(DateUtils.getYyyyMmDdInUTC());
			WorkflowDto workflowDto = new WorkflowDto(incomingData, userInfo, status);
			WorkflowUtil.getNextStateForMyRequest(workflowDto);
			incomingData.setStatus(workflowDto.getNextState());
			if (StringUtils.isNotBlank(incomingData.getNotes())) {
				List<Object> commentsList = new ArrayList<>();
				Map<String, Object> commentsMap = new HashMap<>();
				commentsMap.put(Constants.TYPE, incomingData.getStatus().toLowerCase()
						+ Constants.convertToTitleCase(Constants.Parameters.COMMENTS));
				commentsMap.put(Constants.VALUE, incomingData.getNotes());
				commentsMap.put(Constants.BY, userInfo.getId());
				commentsMap.put(Constants.TIMESTAMP, DateUtils.getCurrentTimestamp());
				if (incomingData.getComments() != null) {
					commentsList = incomingData.getComments();
				}
				commentsList.add(commentsMap);
				incomingData.setComments(commentsList);
			}
			Boolean response = formsDao.updateFormData(incomingData, incomingData.getApplicationId());
			appStatusTrack(response,
					(applicationMap != null && applicationMap.size() > 0 ? applicationMap.get(0) : null), status,
					userInfo);
			return response;
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "updateApplicationStatus", e.getMessage()));
			return Boolean.FALSE;
		}

	}

	@Override
	public Boolean submitInspection(IncomingData incomingData, UserInfo userInfo) {
		try {
			Map<String, Object> applicationMap = getApplicationById(incomingData.getApplicationId(), userInfo);
			if (applicationMap != null) {
				IncomingData applicationData = objectMapper.convertValue(applicationMap, IncomingData.class);
				// get workflow next status
				WorkflowDto workflowDto = new WorkflowDto(applicationData, userInfo,
						Constants.WorkflowActions.COMPLETED_INSPECTION);
				WorkflowUtil.getNextStateForMyRequest(workflowDto);

				// update assignee inspection status in data object
				Boolean isLeadIns = Boolean.FALSE;
				Boolean inspectionCompleted = Boolean.TRUE;
				if (applicationData != null && applicationData.getInspection() != null
						&& applicationData.getInspection().getAssignedTo() != null) {
					for (Assignee assignee : applicationData.getInspection().getAssignedTo()) {
						if (assignee.getId().equals(userInfo.getId()) && assignee.getLeadInspector() != null
								&& assignee.getLeadInspector()) {
							isLeadIns = Boolean.TRUE;
							assignee.setStatus(workflowDto.getNextState());
							assignee.setConsentDate(DateUtils.getYyyyMmDdInUTC());
						} else if (StringUtils.isBlank(assignee.getStatus())) {
							inspectionCompleted = Boolean.FALSE;
						}
					}
				}
				// allow only lead inspector to submit inspection details
				if (isLeadIns) {
					incomingData.setInspection(applicationData.getInspection());
					incomingData.setInspectionDate(DateUtils.getYyyyMmDdInUTC());
					incomingData.getInspection().setInspectionDate(DateUtils.getYyyyMmDdInUTC());
					String nextStatus = inspectionCompleted ? workflowDto.getNextState()
							: Status.LEADINSCOMPLETED.name();
					incomingData.getInspection().setStatus(nextStatus);
					if (inspectionCompleted) {
						incomingData.setStatus(workflowDto.getNextState());
						incomingData.setInspectionCompletedDate(DateUtils.getYyyyMmDdInUTC());
						incomingData.getInspection().setInspectionCompletedDate(DateUtils.getYyyyMmDdInUTC());
					}
					Boolean response = saveFormSubmitv1(incomingData, userInfo,
							inspectionCompleted ? Constants.WorkflowActions.COMPLETED_INSPECTION
									: Constants.WorkflowActions.LEAD_INSPECTION_COMPLETED);
					return response;
				}
			}
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "submitInspection", e.getMessage()));
		}
		return Boolean.FALSE;
	}

	/**
	 * Creates an async operation to send notification & update activity logs on
	 * application status changes
	 */
	private void appStatusTrack(Boolean response, Map<String, Object> applicationMap, String action,
			UserInfo userInfo) {
		new Thread(() -> {
			try {
				Thread.sleep(1000);
				if (response != null && response) {
					if (applicationMap != null && applicationMap.size() > 0) {
						IncomingData applicationData = objectMapper.convertValue(applicationMap, IncomingData.class);
						Map<String, Object> updatedAppMap = getApplicationById(applicationData.getApplicationId(),
								userInfo);
						IncomingData updatedAppData = objectMapper.convertValue(updatedAppMap, IncomingData.class);

						// update activity logs
						if (action.equalsIgnoreCase(Status.NEW.name())) {
							activityService.applicationActivity(null, updatedAppData, userInfo);
						} else {
							activityService.applicationActivity(applicationData, updatedAppData, userInfo);
						}

						// send notification
						if (action.equals(Constants.WorkflowActions.ASSIGN_INSPECTOR)) {
							NotificationUtil.SendNotification(updatedAppData, action, userInfo);
						} else {
							NotificationUtil.SendNotification(applicationData, action, userInfo);
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error(String.format(Constants.EXCEPTION, "appStatusTrack", e.getMessage()));
			}
		}).start();
	}

	@Override
	public List<Map<String, Object>> getActivityLogs(String applicationId) {
		try {
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
			BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
			boolBuilder.must().add(QueryBuilders.matchQuery(Constants.Parameters.ID, applicationId));
			boolBuilder.must().add(QueryBuilders.existsQuery("changes.status"));
			searchSourceBuilder.query(boolBuilder).fetchSource(
					new String[] { Constants.Parameters.ID, Constants.Parameters.UPDATED_BY,
							Constants.Parameters.UPDATED_DATE, Constants.Parameters.UPDATED_BY_EMAIL,
							Constants.TIMESTAMP, "changes.status.ChangedTo", "changes.status.action" },
					new String[] {});
			searchSourceBuilder.sort(SortBuilders.fieldSort(Constants.TIMESTAMP).order(SortOrder.DESC));
			SearchRequest searchRequest = new SearchRequest(appConfig.getActivityLogIndex())
					.source(searchSourceBuilder);
			return formsDao.searchResponse(searchRequest);
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "getActivityLogs", e.getMessage()));
			return null;
		}
	}

	@Override
	public Boolean consentApplication(Consent consent, UserInfo userInfo) {
		try {
			Map<String, Object> applicationMap = getApplicationById(consent.getApplicationId(), userInfo);
			if (applicationMap != null) {
				IncomingData applicationData = objectMapper.convertValue(applicationMap, IncomingData.class);
				Boolean inspectionCompleted = Boolean.TRUE;
				if (applicationData != null && applicationData.getInspection() != null
						&& applicationData.getInspection().getAssignedTo() != null) {
					// get workflow next status
					WorkflowDto workflowDto = new WorkflowDto(applicationData, userInfo,
							Constants.WorkflowActions.COMPLETED_INSPECTION);
					WorkflowUtil.getNextStateForMyRequest(workflowDto);

					for (Assignee assignee : applicationData.getInspection().getAssignedTo()) {
						if (assignee.getId().equals(userInfo.getId())) {
							assignee.setConsentApplication(consent.getAgree());
							assignee.setComments(consent.getComments());
							assignee.setStatus(workflowDto.getNextState());
							assignee.setConsentDate(DateUtils.getYyyyMmDdInUTC());
						} else if (StringUtils.isBlank(assignee.getStatus())) {
							inspectionCompleted = Boolean.FALSE;
						}
					}

					// if all assisting inspector had given their consent, move the application to
					// next status
					if (inspectionCompleted) {
						applicationData.setStatus(workflowDto.getNextState());
						applicationData.getInspection().setStatus(workflowDto.getNextState());
						applicationData.setInspectionCompletedDate(DateUtils.getYyyyMmDdInUTC());
						applicationData.getInspection().setInspectionCompletedDate(DateUtils.getYyyyMmDdInUTC());
					}
					Boolean indexed = formsDao.updateFormData(applicationData, consent.getApplicationId());
					appStatusTrack(indexed, objectMapper.convertValue(applicationData, Map.class),
							inspectionCompleted ? Constants.WorkflowActions.COMPLETED_INSPECTION
									: Constants.WorkflowActions.LEAD_INSPECTION_COMPLETED,
							userInfo);
					return indexed;
				}
			}
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "consentApplication", e.getMessage()));
		}
		return Boolean.FALSE;
	}

	@Override
	public void submitBulkInspection(List<IncomingData> inspectionDataList, UserInfo userInfo) {
		new Thread(() -> {
			try {
				for (IncomingData incomingData : inspectionDataList) {
					submitInspection(incomingData, userInfo);
				}
			} catch (Exception e) {
				LOGGER.error(String.format(Constants.EXCEPTION, "submitBulkInspection", e.getMessage()));
			}
		}).start();
	}

	@Override
	public void consentBulkApplication(List<Consent> consentList, UserInfo userInfo) {
		new Thread(() -> {
			try {
				for (Consent consent : consentList) {
					consentApplication(consent, userInfo);
				}
			} catch (Exception e) {
				LOGGER.error(String.format(Constants.EXCEPTION, "consentBulkApplication", e.getMessage()));
			}
		}).start();
	}

	@Override
	public List<Map<String, Object>> getAllPlainForms() {
		try {
			// query builder
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
			BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
			searchSourceBuilder.query(boolBuilder);
			System.out.println(searchSourceBuilder);
			// es call
			SearchRequest searchRequest = new SearchRequest("fs-plain-form").source(searchSourceBuilder);
			LOGGER.info("Search Request : " + searchRequest);
			List<Map<String, Object>> response = formsDao.searchPlainFormResponse(searchRequest);
			return response;

		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "getApplications", e.getMessage()));
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getPlainFormsById(String id) {
		try {
			// query builder
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
			BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
			boolBuilder.must().add(QueryBuilders.termsQuery("_id", id));
			searchSourceBuilder.query(boolBuilder);
			System.out.println(searchSourceBuilder);
			// es call
			SearchRequest searchRequest = new SearchRequest("fs-plain-form").source(searchSourceBuilder);
			LOGGER.info("Search Request : " + searchRequest);
			List<Map<String, Object>> response = formsDao.searchPlainFormResponse(searchRequest);
			return response;

		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "getApplications", e.getMessage()));
		}
		return null;
	}
	
	@Override
	 public ByteArrayInputStream getInstituteFormData(Long orgId) {
		
		List<InstituteFormDataDto> dataList = new ArrayList<InstituteFormDataDto>();
		
		    List<Object[]> dataListDto = instituteCoursesDao.findInstituteForm(orgId);
		    
		    for(Object[] dto : dataListDto) {
		    	InstituteFormDataDto data = new InstituteFormDataDto();
		    	data.setCenterCode(String.valueOf(dto[0]));
		    	data.setDistrictCode(String.valueOf(dto[1]));
		    	String emailId = String.valueOf(dto[2]);
		    	
		    	data.setInstituteName(emailId);
		    	data.setDegree(String.valueOf(dto[3]));
		    	data.setCourse(String.valueOf(dto[4]));
		    	List<Map<String, Object>>  responseData = this.getApplicationForInstitues(emailId);
		    	if(responseData != null && responseData.size()>0) {
		    		for(Map<String, Object> mp : responseData) {
		    			InstituteFormDataDto dInnernal = new InstituteFormDataDto();
		    			try {
							BeanUtils.copyProperties(dInnernal, data);
							dInnernal.setFormsSubmitted(String.valueOf(mp.get("title")));
							dInnernal.setFormsSavedAsDraft(String.valueOf(mp.get("status")));
							dInnernal.setFormsSubmittedTimestamp(String.valueOf(mp.get("timestamp")));
							dataList.add(dInnernal);
							
						} catch (IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
		    		}
		    	}else {
		    		dataList.add(data);
		    	}
		    	
		    	
		    }

		    ByteArrayInputStream in = ExcelHelper.instituteToExcel(dataList);
		    return in;
		  }
	
	public List<Map<String, Object>> getApplicationForInstitues(String emailId){
		SearchRequestDto searchRequestDto = new SearchRequestDto();
		SearchObject searchObject = new SearchObject();
		searchObject.setKey("createdBy");
		searchObject.setValues(emailId);
		
		List<SearchObject> searchObjects = new ArrayList<>();
		searchObjects.add(searchObject);
		
		searchRequestDto.setSearchObjects(searchObjects);
		List<Map<String, Object>>  responseData = this.getApplications(null, searchRequestDto);
		
		return responseData;
	}

}
