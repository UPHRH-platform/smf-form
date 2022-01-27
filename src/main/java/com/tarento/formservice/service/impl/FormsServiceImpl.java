package com.tarento.formservice.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.tarento.formservice.dao.FormsDao;
import com.tarento.formservice.executor.MasterDataManager;
import com.tarento.formservice.model.FormData;
import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.model.ReplyFeedbackDto;
import com.tarento.formservice.model.ResponseData;
import com.tarento.formservice.model.Result;
import com.tarento.formservice.model.Role;
import com.tarento.formservice.model.UserInfo;
import com.tarento.formservice.model.VerifyFeedbackDto;
import com.tarento.formservice.model.Vote;
import com.tarento.formservice.model.VoteFeedbackDto;
import com.tarento.formservice.models.Form;
import com.tarento.formservice.models.FormDetail;
import com.tarento.formservice.repository.ElasticSearchRepository;
import com.tarento.formservice.service.FormsService;
import com.tarento.formservice.utils.CloudStorage;
import com.tarento.formservice.utils.Constants;

@Service(Constants.ServiceRepositories.FORM_SERVICE)
public class FormsServiceImpl implements FormsService {

	public static final Logger LOGGER = LoggerFactory.getLogger(FormsServiceImpl.class);
	private static final String AUTHORIZATION = "Authorization";
	private static final String US_ASCII = "US-ASCII";
	private static final String BASIC_AUTH = "Basic %s";
	private final String indexServiceHost;
	private final String interactionIndexName;
	private final String userName;
	private final String password;
	@SuppressWarnings("unused")
	private final String easIndexName;
	@SuppressWarnings("unused")
	private final String easDocType;
	Gson gson = new Gson();

	public FormsServiceImpl(@Value("${services.esindexer.host}") String indexServiceHost,
			@Value("${services.esindexer.username}") String userName,
			@Value("${services.esindexer.password}") String password,
			@Value("${es.fs.forms.index.name}") String easIndexName,
			@Value("${es.fs.interactions.index.name}") String interactionIndexName,
			@Value("${es.fs.forms.document.type}") String easDocumentType) {
		this.indexServiceHost = indexServiceHost;
		this.userName = userName;
		this.password = password;
		this.easIndexName = easIndexName;
		this.interactionIndexName = interactionIndexName;
		this.easDocType = easDocumentType;
	}

	@Autowired
	private ElasticSearchRepository elasticRepository;

	@Autowired
	private FormsDao formsDao;

	@SuppressWarnings("unused")
	private MultiSearchResponse executeElasticSearchQuery(String dataContext, String dataContextVersion,
			String searchParameter, String indexName, String docType) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
		BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
		if (StringUtils.isNotBlank(dataContext)) {
			boolBuilder.must().add(QueryBuilders.matchQuery(Constants.Parameters.CONTEXT, dataContext));
		}
		if (StringUtils.isNotBlank(dataContextVersion)) {
			boolBuilder.must().add(QueryBuilders.matchQuery(Constants.Parameters.CONTEXT_VERSION, dataContextVersion));
		}
		if (StringUtils.isNotBlank(searchParameter)) {
			BoolQueryBuilder subBoolBuilder = QueryBuilders.boolQuery();
			subBoolBuilder.should().add(QueryBuilders.wildcardQuery(Constants.PortfolioConstants.VALUE_KEYWORD,
					"*" + searchParameter + "*"));
			boolBuilder.must().add(subBoolBuilder);
		}

		searchSourceBuilder.query(boolBuilder);
		return formsDao
				.executeMultiSearchRequest(new SearchRequest(indexName).types(docType).source(searchSourceBuilder));
	}

	/**
	 * A helper method to create the headers for Rest Connection with UserName and
	 * Password
	 * 
	 * @return HttpHeaders
	 */
	private HttpHeaders getHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTHORIZATION, getBase64Value(userName, password));
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	/**
	 * Helper Method to create the Base64Value for headers
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */

	private String getBase64Value(String userName, String password) {
		String authString = String.format("%s:%s", userName, password);
		byte[] encodedAuthString = Base64.encodeBase64(authString.getBytes(Charset.forName(US_ASCII)));
		return String.format(BASIC_AUTH, new String(encodedAuthString));
	}

	@Override
	public Form createForm(FormDetail newForm) throws IOException {
		if (newForm.getId() != null)
			performVersionCheck(newForm);
		return (formsDao.addNewForm(newForm, getHttpHeaders())) ? newForm : null;
	}

	private void performVersionCheck(Form newForm) {
		MultiSearchResponse response = formsDao.executeMultiSearchRequest(createRequestForVersionCheck(newForm));
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
		sRequest = new SearchRequest("fs-forms").types("forms").source(searchSourceBuilder);
		return sRequest;
	}

	@Override
	public List<Form> getAllForms() {
		List<Form> formList = new ArrayList<>();
		SearchRequest searchRequest = buildQueryForGetAllForms();
		MultiSearchResponse response = formsDao.executeMultiSearchRequest(searchRequest);
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
							Form form = gson.fromJson(eachInnerHit.findValue("sourceAsMap").toString(), Form.class);
							form.setNumberOfRecords((long) randInt(1, 1000));
							LOGGER.info("Each Form : {}", gson.toJson(form));
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
		MultiSearchResponse response = formsDao.executeMultiSearchRequest(searchRequest);
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

	private SearchRequest buildQueryForGetAllForms() {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0)
				.aggregation(AggregationBuilders.terms("UniqueFormId").field("id").size(100)
						.subAggregation(AggregationBuilders.topHits("LatestVersion").from(0).size(1)
								.version(Boolean.FALSE).explain(Boolean.FALSE)
								.sort(SortBuilders.fieldSort("version").order(SortOrder.DESC))));
		return new SearchRequest("fs-forms").types("forms").source(searchSourceBuilder);
	}

	private SearchRequest buildQueryForGetCount() {
		BoolQueryBuilder approvalPendingBoolBuilder = QueryBuilders.boolQuery();
		approvalPendingBoolBuilder.must().add(QueryBuilders.termQuery("approval.keyword", ""));
		BoolQueryBuilder approvalAddressedboolBuilder = QueryBuilders.boolQuery();
		approvalAddressedboolBuilder.must().add(
				QueryBuilders.termsQuery("approval.keyword", new ArrayList<>(Arrays.asList("APPROVED", "REJECTED"))));
		BoolQueryBuilder challengePendingboolBuilder = QueryBuilders.boolQuery();
		BoolQueryBuilder newBoolQuery = QueryBuilders.boolQuery();
		newBoolQuery.filter(QueryBuilders.termsQuery("approval.keyword", new ArrayList<>(Arrays.asList("APPROVED"))));
		newBoolQuery.filter(QueryBuilders.termQuery("challenge.keyword", ""));
		newBoolQuery.should(QueryBuilders.matchQuery("challengeStatus", true));
		challengePendingboolBuilder.must().add(newBoolQuery);
		BoolQueryBuilder challengeAddressedBoolBuilder = QueryBuilders.boolQuery();
		BoolQueryBuilder challengeBoolQuery = QueryBuilders.boolQuery();
		challengeBoolQuery.filter(QueryBuilders.termsQuery("challenge.keyword",
				new ArrayList<>(Arrays.asList("OVERRULED", "SUSTAINED"))));
		challengeBoolQuery.should(QueryBuilders.matchQuery("challengeStatus", true));
		challengeAddressedBoolBuilder.must().add(challengeBoolQuery);

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
		searchSourceBuilder.aggregation(AggregationBuilders.filter("Approval Pending", approvalPendingBoolBuilder)
				.subAggregation(AggregationBuilders.count("Approval Pending Count").field("id")));
		searchSourceBuilder.aggregation(AggregationBuilders.filter("Approval Addressed", approvalAddressedboolBuilder)
				.subAggregation(AggregationBuilders.count("Approval Addressed Count").field("id")));
		searchSourceBuilder.aggregation(AggregationBuilders.filter("Challenge Pending", challengePendingboolBuilder)
				.subAggregation(AggregationBuilders.count("Challenge Pending Count").field("id")));
		searchSourceBuilder.aggregation(AggregationBuilders.filter("Challenge Addressed", challengeAddressedBoolBuilder)
				.subAggregation(AggregationBuilders.count("Challenge Addressed Count").field("id")));
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		return new SearchRequest("fs-forms-data").types("forms").source(searchSourceBuilder);
	}

	private SearchRequest buildQueryForAgentOverview(Long agentId) {

		BoolQueryBuilder reviewsReceivedQuery = QueryBuilders.boolQuery();
		reviewsReceivedQuery.must().add(QueryBuilders.termQuery("agentId", agentId));

		BoolQueryBuilder reviewsChallenged = QueryBuilders.boolQuery();
		BoolQueryBuilder newBoolQuery = QueryBuilders.boolQuery();
		newBoolQuery.filter(QueryBuilders.termQuery("agentId", 397));
		newBoolQuery.should(QueryBuilders.matchQuery("challengeStatus", true));
		reviewsChallenged.must().add(newBoolQuery);

		BoolQueryBuilder averageRating = QueryBuilders.boolQuery();
		averageRating.must().add(QueryBuilders.termQuery("agentId", agentId));
		averageRating.must().add(QueryBuilders.termQuery("approval.keyword", "APPROVED"));

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
		searchSourceBuilder.aggregation(AggregationBuilders.filter("Reviews Received", reviewsReceivedQuery)
				.subAggregation(AggregationBuilders.count("Reviews Received Count").field("id")));

		searchSourceBuilder.aggregation(AggregationBuilders.filter("Reviews Challenged", reviewsChallenged)
				.subAggregation(AggregationBuilders.count("Reviews Challenged Count").field("id")));

		searchSourceBuilder.aggregation(AggregationBuilders.filter("Average Rating", averageRating)
				.subAggregation(AggregationBuilders.avg("Average Rating Value")
						.field("dataObject.How would you rate your overall experience with our agent? (Mandatory)")));

		return new SearchRequest("fs-forms-data").types("forms").source(searchSourceBuilder);
	}

	private SearchRequest buildQueryForAgentOverviewForInteractions(Long agentId) {

		BoolQueryBuilder agentBool = QueryBuilders.boolQuery();
		agentBool.must().add(QueryBuilders.termQuery("agent", agentId));

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
		searchSourceBuilder.aggregation(AggregationBuilders.filter("Interactions", agentBool)
				.subAggregation(AggregationBuilders.count("Interactions Count").field("id")));

		return new SearchRequest("fs-interactions").types("forms").source(searchSourceBuilder);
	}

	private SearchRequest buildQueryForGetAllCharts() {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(100);
		return new SearchRequest("vt-chart").types("forms").source(searchSourceBuilder);
	}

	private SearchRequest buildQueryForGetAllDashboards() {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(100);
		return new SearchRequest("vt-dashboard").types("forms").source(searchSourceBuilder);
	}

	private SearchRequest buildQueryForGetQueryById(Long id) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0)
				.query(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("id", id)))
				.aggregation(AggregationBuilders.terms("UniqueFormId").field("id").size(100)
						.subAggregation(AggregationBuilders.topHits("LatestVersion").from(0).size(1)
								.version(Boolean.FALSE).explain(Boolean.FALSE)
								.sort(SortBuilders.fieldSort("version").order(SortOrder.DESC))));
		return new SearchRequest("fs-forms").types("forms").source(searchSourceBuilder);
	}

	private SearchRequest buildQueryForGetFeedbacks(Long id, String approved, String challenged, Long agentId,
			Long customerId, UserInfo userInfo, Boolean challengeStatus) {
		if (StringUtils.isNotBlank(challenged)) {
			challengeStatus = Boolean.TRUE;
		}
		for (Role role : userInfo.getRoles()) {
			if (role.getName().equals("Customer")) {
				customerId = userInfo.getId();
				return buildQueryForCustomerFeedbacks(id, approved, challenged, challengeStatus);
			} else if (role.getName().equals("Agent")) {
				agentId = userInfo.getId();
			}
		}
		return buildQueryForGetFeedbacksGeneral(id, approved, challenged, agentId, customerId, challengeStatus);
	}

	private SearchRequest buildQueryForCustomerFeedbacks(Long id, String approved, String challenged,
			Boolean challengeStatus) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if (id == null && approved == null && challenged == null) {
			boolQuery.must(QueryBuilders.matchAllQuery());
		}
		if (id != null && id > 0) {
			boolQuery.must(QueryBuilders.matchQuery("id", id));
		}
		if (approved != null) {
			if (approved.equalsIgnoreCase("APPROVED"))
				boolQuery.filter(QueryBuilders.termQuery("approval.keyword", "APPROVED"));
			else if (approved.equalsIgnoreCase("REJECTED"))
				boolQuery.filter(QueryBuilders.termQuery("approval.keyword", "REJECTED"));
			else if (approved.equalsIgnoreCase("PENDING"))
				boolQuery.filter(QueryBuilders.termQuery("approval.keyword", ""));
		}
		if (challengeStatus != null && challengeStatus) {
			boolQuery.filter(QueryBuilders.termQuery("approval.keyword", "APPROVED"));
			if (challenged != null) {
				if (challenged.equalsIgnoreCase("OVERRULED"))
					boolQuery.filter(QueryBuilders.termQuery("challenge.keyword", "OVERRULED"));
				else if (challenged.equalsIgnoreCase("SUSTAINED"))
					boolQuery.filter(QueryBuilders.termQuery("challenge.keyword", "SUSTAINED"));
				else if (challenged.equalsIgnoreCase("PENDING"))
					boolQuery.filter(QueryBuilders.matchQuery("challengeStatus", Boolean.TRUE));
			}
		}
		if (approved == null && challengeStatus == null) {
			boolQuery.must(QueryBuilders.termQuery("approval.keyword", "APPROVED"));
			BoolQueryBuilder bool2Query = new BoolQueryBuilder();
			bool2Query.must(QueryBuilders.matchQuery("challengeStatus", Boolean.TRUE));
			bool2Query.filter(QueryBuilders.termQuery("challenge.keyword", "OVERRULED"));
			BoolQueryBuilder bool3Query = new BoolQueryBuilder();
			bool3Query.should(QueryBuilders.termQuery("challengeStatus", Boolean.FALSE));
			bool3Query.should(bool2Query);
			boolQuery.must(bool3Query);
		}
		searchSourceBuilder.query(boolQuery).sort(SortBuilders.fieldSort("timestamp").order(SortOrder.DESC)).size(1000);
		return new SearchRequest("fs-forms-data").types("forms").source(searchSourceBuilder);
	}

	private SearchRequest buildQueryForGetFeedbacksGeneral(Long id, String approved, String challenged, Long agentId,
			Long customerId, Boolean challengeStatus) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if (id == null && approved == null && challenged == null && agentId == null && customerId == null) {
			boolQuery.must(QueryBuilders.matchAllQuery());
		}
		if (id != null && id > 0) {
			boolQuery.must(QueryBuilders.matchQuery("id", id));
		}
		if (customerId != null && customerId > 0) {
			MatchQueryBuilder approvalMatch = QueryBuilders.matchQuery("approval.keyword", "APPROVED");
			boolQuery.must(approvalMatch);
			MatchQueryBuilder blankChallengeStatusMatch = QueryBuilders.matchQuery("challenge.keyword", "");
			MatchQueryBuilder overruledChallengeStatusMatch = QueryBuilders.matchQuery("challenge.keyword",
					"SUSTAINED");
			boolQuery.should(blankChallengeStatusMatch);
			boolQuery.should(overruledChallengeStatusMatch);
		}
		if (agentId != null && agentId > 0) {
			boolQuery.must(QueryBuilders.matchQuery("agentId", agentId));
		}
		if (approved != null) {
			if (approved.equalsIgnoreCase("APPROVED"))
				boolQuery.must(QueryBuilders.matchQuery("approval.keyword", "APPROVED"));
			else if (approved.equalsIgnoreCase("REJECTED"))
				boolQuery.must(QueryBuilders.matchQuery("approval.keyword", "REJECTED"));
			else if (approved.equalsIgnoreCase("PENDING"))
				boolQuery.must(QueryBuilders.matchQuery("approval.keyword", ""));
		}
		if (challengeStatus != null && challengeStatus) {
			boolQuery.must(QueryBuilders.matchQuery("approval.keyword", "APPROVED"));
			if (challenged != null) {
				if (challenged.equalsIgnoreCase("OVERRULED"))
					boolQuery.must(QueryBuilders.matchQuery("challenge.keyword", "OVERRULED"));
				else if (challenged.equalsIgnoreCase("SUSTAINED"))
					boolQuery.filter(QueryBuilders.termQuery("challenge.keyword", "SUSTAINED"));
				else if (challenged.equalsIgnoreCase("PENDING"))
					boolQuery.filter(QueryBuilders.matchQuery("challengeStatus", Boolean.TRUE));
			}

		}
		searchSourceBuilder.query(boolQuery).sort(SortBuilders.fieldSort("timestamp").order(SortOrder.DESC)).size(1000);
		return new SearchRequest("fs-forms-data").types("forms").source(searchSourceBuilder);

	}

	private SearchRequest buildQueryForGetFeedbackById(Long id) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if (id != null && id > 0) {
			boolQuery.must(QueryBuilders.matchQuery("id", id));
		}
		searchSourceBuilder.query(boolQuery).sort(SortBuilders.fieldSort("timestamp").order(SortOrder.DESC)).size(1000);
		return new SearchRequest("fs-forms-data").types("forms").source(searchSourceBuilder);
	}

	private SearchRequest buildQueryForGetAllFeedbacks(String approved, String challenged, Boolean challengeStatus) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery());
		if (approved != null) {
			if (approved.equalsIgnoreCase("APPROVED"))
				boolQuery.filter(QueryBuilders.termQuery("approval.keyword", "APPROVED"));
			else if (approved.equalsIgnoreCase("REJECTED"))
				boolQuery.filter(QueryBuilders.termQuery("approval.keyword", "REJECTED"));
			else if (approved.equalsIgnoreCase("PENDING"))
				boolQuery.filter(QueryBuilders.termQuery("approval.keyword", ""));
		}
		if (challengeStatus != null && challengeStatus) {
			boolQuery.filter(QueryBuilders.termQuery("approval.keyword", "APPROVED"));
			boolQuery.filter(QueryBuilders.termQuery("challengeStatus", true));
			if (challenged != null) {
				if (challenged.equalsIgnoreCase("OVERRULED"))
					boolQuery.filter(QueryBuilders.termQuery("challenge.keyword", "OVERRULED"));
				else if (challenged.equalsIgnoreCase("SUSTAINED"))
					boolQuery.filter(QueryBuilders.termQuery("challenge.keyword", "SUSTAINED"));
			} else {
				boolQuery.filter(QueryBuilders.termQuery("challenge.keyword", ""));
			}
		} else if (challengeStatus != null && !challengeStatus) {
			boolQuery.filter(QueryBuilders.termQuery("challengeStatus", false));
		}
		searchSourceBuilder.query(boolQuery).sort(SortBuilders.fieldSort("timestamp").order(SortOrder.DESC)).size(1000);
		return new SearchRequest("fs-forms-data").types("forms").source(searchSourceBuilder);
	}

	private SearchRequest buildQueryForGetChartById(Long id) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(100)
				.query(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("id", id)));
		return new SearchRequest("vt-chart").types("forms").source(searchSourceBuilder);
	}

	private SearchRequest buildQueryForGetDashboarById(Long id) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(100)
				.query(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("id", id)));
		return new SearchRequest("vt-dashboard").types("forms").source(searchSourceBuilder);
	}

	public static JsonObject convertToJsonObject(Object payload) {
		GsonBuilder builder = new GsonBuilder();
		return (JsonObject) builder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create().toJsonTree(payload);
	}

	@Override
	public Boolean saveFormSubmit(IncomingData incomingData) throws IOException {
		return formsDao.saveFormSubmit(incomingData, getHttpHeaders());
	}

	@Override
	public List<IncomingData> getFeedbacksByFormId(Long id, String approved, String challenged, Long agentId,
			Long customerId, UserInfo userInfo, Boolean challengeStatus) {

		List<IncomingData> incomingData = new ArrayList<>();
		SearchRequest searchRequest = buildQueryForGetFeedbacks(id, approved, challenged, agentId, customerId, userInfo,
				challengeStatus);
		MultiSearchResponse response = formsDao.executeMultiSearchRequest(searchRequest);
		SearchResponse searchResponse = response.getResponses()[0].getResponse();
		JsonNode responseNode = null;
		if (searchResponse != null && searchResponse.getHits() != null) {
			responseNode = new ObjectMapper().convertValue(searchResponse.getHits(), JsonNode.class);
			if (responseNode.has("hits")) {
				JsonNode innerHits = responseNode.findValue("hits");
				for (JsonNode eachInnerHit : innerHits) {
					String documentId = eachInnerHit.findValue("id").asText();
					IncomingData form = new IncomingData();
					form = gson.fromJson(eachInnerHit.findValue("sourceAsMap").toString(), IncomingData.class);
					form.setRecordId(documentId);
					incomingData.add(form);
					LOGGER.info("Each Form : {}", gson.toJson(form));
				}
			}
		}
		return incomingData;
	}

	@Override
	public List<IncomingData> getFeedbacksByFormId(Long id) {
		List<IncomingData> incomingData = new ArrayList<>();
		SearchRequest searchRequest = buildQueryForGetFeedbackById(id);
		MultiSearchResponse response = formsDao.executeMultiSearchRequest(searchRequest);
		SearchResponse searchResponse = response.getResponses()[0].getResponse();
		JsonNode responseNode = null;
		if (searchResponse != null && searchResponse.getHits() != null) {
			responseNode = new ObjectMapper().convertValue(searchResponse.getHits(), JsonNode.class);
			if (responseNode.has("hits")) {
				JsonNode innerHits = responseNode.findValue("hits");
				for (JsonNode eachInnerHit : innerHits) {
					String documentId = eachInnerHit.findValue("id").asText();
					IncomingData form = new IncomingData();
					form = gson.fromJson(eachInnerHit.findValue("sourceAsMap").toString(), IncomingData.class);
					form.setRecordId(documentId);
					incomingData.add(form);
					LOGGER.info("Each Form : {}", gson.toJson(form));
				}
			}
		}
		return incomingData;
	}

	@Override
	public Boolean verifyFeedback(UserInfo userInfo, VerifyFeedbackDto verifyFeedbackDto) throws IOException {
		Map<String, Object> jsonMap = new HashMap<>();
		if (verifyFeedbackDto.getCondition().equalsIgnoreCase("APPROVAL")
				&& !StringUtils.isBlank(verifyFeedbackDto.getStatus())
				&& (verifyFeedbackDto.getStatus().equalsIgnoreCase("APPROVED")
						|| verifyFeedbackDto.getStatus().equalsIgnoreCase("REJECTED"))) {
			jsonMap.put("approval", verifyFeedbackDto.getStatus());
			jsonMap.put("approvedTime", new Date().getTime());
			jsonMap.put("approvedBy", userInfo.getId());
		} else if (verifyFeedbackDto.getCondition().equalsIgnoreCase("CHALLENGE")
				&& !StringUtils.isBlank(verifyFeedbackDto.getStatus())
				&& (verifyFeedbackDto.getStatus().equalsIgnoreCase("OVERRULED")
						|| verifyFeedbackDto.getStatus().equalsIgnoreCase("SUSTAINED"))) {
			jsonMap.put("challenge", verifyFeedbackDto.getStatus());
			jsonMap.put("challengeStatus", true);
			jsonMap.put("challengeVerifiedTime", new Date().getTime());
			jsonMap.put("challengeVerifiedBy", userInfo.getId());
		}
		return formsDao.verifyFeedback(jsonMap, verifyFeedbackDto.getId());
	}

	private String encodeFormsData(FormData fData) {
		return new String(Base64.encodeBase64(new Gson().toJson(fData).getBytes(Charset.forName(US_ASCII))));
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

	private SearchRequest buildQueryForInteractions(Long startDate, Long endDate) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
		if (startDate != null & endDate != null) {
			BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
			boolQuery.must(QueryBuilders.matchQuery("linkSentDate", 0));
			boolQuery.must(QueryBuilders.rangeQuery("interactionDate").gte(startDate).lte(endDate));
			searchSourceBuilder.query(boolQuery);
		}
		return new SearchRequest(interactionIndexName).types("forms").source(searchSourceBuilder);
	}

	@Override
	public List<IncomingData> getFeedbacks(String approved, String challenged, Boolean challengeStatus) {
		List<IncomingData> incomingData = new ArrayList<>();

		SearchRequest searchRequest = buildQueryForGetAllFeedbacks(approved, challenged, challengeStatus);
		MultiSearchResponse response = formsDao.executeMultiSearchRequest(searchRequest);
		SearchResponse searchResponse = response.getResponses()[0].getResponse();
		JsonNode responseNode = null;
		if (searchResponse != null && searchResponse.getHits() != null) {
			responseNode = new ObjectMapper().convertValue(searchResponse.getHits(), JsonNode.class);
			if (responseNode.has("hits")) {
				JsonNode innerHits = responseNode.findValue("hits");
				for (JsonNode eachInnerHit : innerHits) {
					IncomingData form = new IncomingData();
					form = gson.fromJson(eachInnerHit.findValue("sourceAsMap").toString(), IncomingData.class);
					incomingData.add(form);
					LOGGER.info("Each Form : {}", gson.toJson(form));
				}
			}
		}
		return incomingData;
	}

	public Boolean challengeFeedback(String id, String reason) throws IOException {
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("challengeStatus", true);
		jsonMap.put("reasonForChallenge", reason);
		return formsDao.challengeFeedback(jsonMap, id);
	}

	@Override
	public Boolean voteFeedback(UserInfo userInfo, VoteFeedbackDto voteFeedbackDto) throws IOException {
		Map<String, Object> jsonMap = new HashMap<>();
		SearchRequest searchRequest = buildQueryForGetFeedbackById(voteFeedbackDto.getRecordId());
		MultiSearchResponse response = formsDao.executeMultiSearchRequest(searchRequest);
		SearchResponse searchResponse = response.getResponses()[0].getResponse();
		JsonNode responseNode = null;
		IncomingData form = new IncomingData();
		if (searchResponse != null && searchResponse.getHits() != null) {
			responseNode = new ObjectMapper().convertValue(searchResponse.getHits(), JsonNode.class);
			if (responseNode.has("hits")) {
				JsonNode innerHits = responseNode.findValue("hits");
				for (JsonNode eachInnerHit : innerHits) {
					form = gson.fromJson(eachInnerHit.findValue("sourceAsMap").toString(), IncomingData.class);
					LOGGER.info("Each Form : {}", gson.toJson(form));
				}
			}
		}
		if (voteFeedbackDto.getAction().equals("DO")) {
			if (voteFeedbackDto.getVote().equals("UP")) {
				Vote vote = new Vote();
				vote.setVoteDate(new Date().getTime());
				vote.setCustomerId(voteFeedbackDto.getCustomerId());
				vote.setVote(voteFeedbackDto.getVote());
				List<Vote> updatedVotes = new ArrayList<>();
				Long upvotesCount = 0l;
				if (form.getUpvotes() != null && form.getUpvotes().size() > 0) {
					updatedVotes = form.getUpvotes();
					upvotesCount = form.getUpvoteCount();
				}
				updatedVotes.add(vote);
				upvotesCount = upvotesCount + 1l;
				jsonMap.put("upvotes", updatedVotes);
				jsonMap.put("upvoteCount", upvotesCount);
			} else if (voteFeedbackDto.getVote().equals("DOWN")) {
				Vote vote = new Vote();
				vote.setVoteDate(new Date().getTime());
				vote.setCustomerId(voteFeedbackDto.getCustomerId());
				vote.setVote(voteFeedbackDto.getVote());
				List<Vote> updatedVotes = new ArrayList<>();
				Long downvotesCount = 0l;
				if (form.getDownvotes() != null && form.getDownvotes().size() > 0) {
					updatedVotes = form.getDownvotes();
					downvotesCount = form.getDownvoteCount();
				}
				updatedVotes.add(vote);
				downvotesCount = downvotesCount + 1l;
				jsonMap.put("downvotes", updatedVotes);
				jsonMap.put("downvoteCount", downvotesCount);
			}
		} else if (voteFeedbackDto.getAction().equals("UNDO")) {
			Map<Long, Vote> votesMap = new HashMap<Long, Vote>();
			Long upvotesCount = 0l;
			Long downvotesCount = 0l;
			if (voteFeedbackDto.getVote().equals("UP")) {
				if (form.getUpvotes() != null && form.getUpvotes().size() > 0) {
					upvotesCount = form.getUpvoteCount();
					for (Vote vote : form.getUpvotes()) {
						if (!vote.getCustomerId().equals(voteFeedbackDto.getCustomerId())) {
							votesMap.put(vote.getCustomerId(), vote);
						} else {
							upvotesCount = upvotesCount - 1l;
						}
					}
				}
				List<Vote> finalUpvotesList = new ArrayList(votesMap.values());
				jsonMap.put("upvotes", finalUpvotesList);
				jsonMap.put("upvoteCount", upvotesCount);
			} else if (voteFeedbackDto.getVote().equals("DOWN")) {
				if (form.getDownvotes() != null && form.getDownvotes().size() > 0) {
					downvotesCount = form.getDownvoteCount();
					for (Vote vote : form.getDownvotes()) {
						if (!vote.getCustomerId().equals(voteFeedbackDto.getCustomerId())) {
							votesMap.put(vote.getCustomerId(), vote);
						} else {
							downvotesCount = downvotesCount - 1l;
						}
					}
				}

				List<Vote> finalDownvotesList = new ArrayList(votesMap.values());
				jsonMap.put("downvotes", finalDownvotesList);
				jsonMap.put("downvoteCount", downvotesCount);
			}
		}
		return formsDao.voteFeedback(jsonMap, voteFeedbackDto.getRecordId());
	}

	private SearchRequest buildQueryForGetFeedbackById(String recordId) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(10)
				.query(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("_id", recordId)));
		return new SearchRequest("fs-forms-data").types("forms").source(searchSourceBuilder);
	}

	@Override
	public Boolean replyFeedback(UserInfo userInfo, ReplyFeedbackDto replyFeedbackDto) throws IOException {
		Map<String, Object> jsonMap = new HashMap<>();
		replyFeedbackDto.setUserId(userInfo.getId());
		if (MasterDataManager.getUserData().get(userInfo.getId()) != null) {
			replyFeedbackDto
					.setUsername(MasterDataManager.getUserData().get(userInfo.getId()).getUsername().toString());
		} else {
			ResponseData data = fetchUserInfo(userInfo.getId());
			if (data != null) {
				MasterDataManager.getUserData().put(userInfo.getId(), data);
				replyFeedbackDto.setUsername(data.getUsername().toString());
			}
		}
		replyFeedbackDto.setReplyDate(new Date().getTime());
		SearchRequest searchRequest = buildQueryForGetFeedbackById(replyFeedbackDto.getRecordId());
		MultiSearchResponse response = formsDao.executeMultiSearchRequest(searchRequest);
		SearchResponse searchResponse = response.getResponses()[0].getResponse();
		JsonNode responseNode = null;
		IncomingData form = new IncomingData();
		if (searchResponse != null && searchResponse.getHits() != null) {
			responseNode = new ObjectMapper().convertValue(searchResponse.getHits(), JsonNode.class);
			if (responseNode.has("hits")) {
				JsonNode innerHits = responseNode.findValue("hits");
				for (JsonNode eachInnerHit : innerHits) {
					form = gson.fromJson(eachInnerHit.findValue("sourceAsMap").toString(), IncomingData.class);
					LOGGER.info("Each Form : {}", gson.toJson(form));
				}
			}
		}
		List<ReplyFeedbackDto> replies = new ArrayList<>();
		if (replyFeedbackDto.getReply() != null && replyFeedbackDto.getReply() != "")
			replies.add(replyFeedbackDto);
		jsonMap.put("replies", replies);
		return formsDao.replyFeedback(jsonMap, replyFeedbackDto.getRecordId());
	}

	@Override
	public Boolean saveFormSubmit(IncomingData incomingData, Map<String, MultipartFile> multipartFiles) {
		try {
			// upload the attached files
			Map<String, List<Map<String, String>>> attachments = new HashMap<>();
			if (multipartFiles != null) {
				for (Map.Entry<String, MultipartFile> entry : multipartFiles.entrySet()) {
					String folderPath = Constants.UP_SMF + "/" + entry.getKey();
					File file = new File(entry.getValue().getOriginalFilename());
					file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(entry.getValue().getBytes());
					fos.close();
					Map<String, String> uploadedFile = CloudStorage.uploadFile(folderPath, file);
					file.delete();

					if (uploadedFile != null) {
						if (attachments.containsKey(entry.getKey())) {
							attachments.get(entry.getKey()).add(uploadedFile);
						} else {
							attachments.put(entry.getKey(), new ArrayList<>(Arrays.asList(uploadedFile)));
						}
					} else {
						LOGGER.info("Uploading " + entry.getValue().getOriginalFilename() + " file got failed");
					}
				}
			} else {
				LOGGER.info("No attachments found in the form request");
			}

			incomingData.setAttachments(attachments);
			formsDao.saveFormSubmit(incomingData, getHttpHeaders());
			return Boolean.TRUE;

		} catch (Exception e) {
			LOGGER.error(String.format("Exception in saveFormSubmit: %s", e.getMessage()));
		}
		return Boolean.FALSE;
	}

	@Override
	public List<IncomingData> getApplications(String formId, String applicationId) {
		try {
			if (StringUtils.isNotBlank(formId) || StringUtils.isNotBlank(applicationId)) {
				List<IncomingData> responseData = new ArrayList<>();
				SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
				if (StringUtils.isNotBlank(formId)) {
					searchSourceBuilder.query(QueryBuilders.matchQuery(Constants.FORM_ID, formId));
				} else if (StringUtils.isNotBlank(applicationId)) {
					searchSourceBuilder.query(QueryBuilders.matchQuery(Constants._ID, applicationId));
				}
				SearchRequest searchRequest = new SearchRequest("fs-forms-data").types("forms")
						.source(searchSourceBuilder);

				MultiSearchResponse response = formsDao.executeMultiSearchRequest(searchRequest);
				SearchResponse searchResponse = response.getResponses()[0].getResponse();
				SearchHit[] hit = searchResponse.getHits().getHits();
				for (SearchHit hits : hit) {
					Map<String, Object> sourceAsMap = hits.getSourceAsMap();
					sourceAsMap.put(Constants.APPLICATION_ID, hits.getId());
					responseData.add(new ObjectMapper().convertValue(sourceAsMap, IncomingData.class));
				}

				return responseData;
			}
		} catch (Exception e) {
			LOGGER.error(String.format("Exception in getApplications: %s", e.getMessage()));
		}
		return null;
	}
}
