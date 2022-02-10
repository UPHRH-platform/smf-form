package com.tarento.formservice.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.tarento.formservice.dao.FormsDao;
import com.tarento.formservice.model.AssignApplication;
import com.tarento.formservice.executor.StateMatrixManager;
import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.model.KeyValue;
import com.tarento.formservice.model.KeyValueList;
import com.tarento.formservice.model.ReplyFeedbackDto;
import com.tarento.formservice.model.ResponseData;
import com.tarento.formservice.model.Result;
import com.tarento.formservice.model.Role;
import com.tarento.formservice.model.Roles;
import com.tarento.formservice.model.SearchObject;
import com.tarento.formservice.model.SearchRequestDto;
import com.tarento.formservice.model.State;
import com.tarento.formservice.model.StateMatrix;
import com.tarento.formservice.model.UserInfo;
import com.tarento.formservice.model.VerifyFeedbackDto;
import com.tarento.formservice.model.Vote;
import com.tarento.formservice.model.VoteFeedbackDto;
import com.tarento.formservice.models.Form;
import com.tarento.formservice.models.FormDetail;
import com.tarento.formservice.repository.ElasticSearchRepository;
import com.tarento.formservice.service.FormsService;
import com.tarento.formservice.utils.AppConfiguration;
import com.tarento.formservice.utils.CloudStorage;
import com.tarento.formservice.utils.Constants;
import com.tarento.formservice.utils.DateUtils;

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

	@Override
	public Form createForm(FormDetail newForm) throws IOException {
		if (newForm.getId() != null)
			performVersionCheck(newForm);

		if (newForm.getId() != null) {
			newForm.setUpdatedDate(new Date().getTime());
		} else {
			newForm.setId(new Date().getTime());
			newForm.setUpdatedDate(new Date().getTime());
			newForm.setVersion(1);
		}
		return (formsDao.addForm(newForm)) ? newForm : null;
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
		sRequest = new SearchRequest(appConfig.getFormIndex()).types(appConfig.getFormIndexType())
				.source(searchSourceBuilder);
		return sRequest;
	}

	@Override
	public List<Form> getAllForms() {
		List<Form> formList = new ArrayList<>();
		SearchRequest searchRequest = buildQueryForGetAllForms();
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

	private SearchRequest buildQueryForGetAllForms() {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0)
				.aggregation(AggregationBuilders.terms("UniqueFormId").field("id").size(100)
						.order(BucketOrder.key(Boolean.TRUE))
						.subAggregation(AggregationBuilders.topHits("LatestVersion").from(0).size(1)
								.version(Boolean.FALSE).explain(Boolean.FALSE)
								.sort(SortBuilders.fieldSort("version").order(SortOrder.DESC))));
		return new SearchRequest(appConfig.getFormIndex()).types(appConfig.getFormIndexType())
				.source(searchSourceBuilder);
	}

	private SearchRequest buildQueryForGetQueryById(Long id) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0)
				.query(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("id", id)))
				.aggregation(AggregationBuilders.terms("UniqueFormId").field("id").size(100)
						.subAggregation(AggregationBuilders.topHits("LatestVersion").from(0).size(1)
								.version(Boolean.FALSE).explain(Boolean.FALSE)
								.sort(SortBuilders.fieldSort("version").order(SortOrder.DESC))));
		return new SearchRequest(appConfig.getFormIndex()).types(appConfig.getFormIndexType())
				.source(searchSourceBuilder);
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
		searchSourceBuilder.query(boolQuery).sort(SortBuilders.fieldSort(Constants.TIMESTAMP).order(SortOrder.DESC))
				.size(1000);
		return new SearchRequest(appConfig.getFormDataIndex()).types(appConfig.getFormDataIndexType())
				.source(searchSourceBuilder);
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
		searchSourceBuilder.query(boolQuery).sort(SortBuilders.fieldSort(Constants.TIMESTAMP).order(SortOrder.DESC))
				.size(1000);
		return new SearchRequest(appConfig.getFormDataIndex()).types(appConfig.getFormDataIndexType())
				.source(searchSourceBuilder);

	}

	@Override
	public Boolean saveFormSubmit(IncomingData incomingData) throws IOException {
		return formsDao.addFormData(incomingData);
	}

	@Override
	public List<Map<String, Object>> getFeedbacksByFormId(Long id, String approved, String challenged, Long agentId,
			Long customerId, UserInfo userInfo, Boolean challengeStatus) {
		SearchRequest searchRequest = buildQueryForGetFeedbacks(id, approved, challenged, agentId, customerId, userInfo,
				challengeStatus);
		return formsDao.searchResponse(searchRequest);

	}

	@Override
	public List<Map<String, Object>> getFeedbacksByFormId(Long id) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if (id != null && id > 0) {
			boolQuery.must(QueryBuilders.matchQuery("id", id));
		}
		searchSourceBuilder.query(boolQuery).sort(SortBuilders.fieldSort(Constants.TIMESTAMP).order(SortOrder.DESC))
				.size(1000);
		SearchRequest searchRequest = new SearchRequest(appConfig.getFormDataIndex())
				.types(appConfig.getFormIndexType()).source(searchSourceBuilder);
		return formsDao.searchResponse(searchRequest);
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
		return formsDao.updateFormData(jsonMap, verifyFeedbackDto.getId());
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

	@Override
	public List<Map<String, Object>> getFeedbacks(String approved, String challenged, Boolean challengeStatus) {
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
		searchSourceBuilder.query(boolQuery).sort(SortBuilders.fieldSort(Constants.TIMESTAMP).order(SortOrder.DESC))
				.size(1000);
		SearchRequest searchRequest = new SearchRequest(appConfig.getFormDataIndex())
				.types(appConfig.getFormDataIndexType()).source(searchSourceBuilder);

		return formsDao.searchResponse(searchRequest);
	}

	public Boolean challengeFeedback(String id, String reason) throws IOException {
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("challengeStatus", true);
		jsonMap.put("reasonForChallenge", reason);
		return formsDao.updateFormData(jsonMap, id);
	}

	@Override
	public Boolean voteFeedback(UserInfo userInfo, VoteFeedbackDto voteFeedbackDto) throws IOException {
		Map<String, Object> jsonMap = new HashMap<>();
		SearchRequest searchRequest = buildQueryForGetFeedbackById(voteFeedbackDto.getRecordId());
		MultiSearchResponse response = elasticRepository.executeMultiSearchRequest(searchRequest);
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
		return formsDao.updateFormData(jsonMap, voteFeedbackDto.getRecordId());
	}

	private SearchRequest buildQueryForGetFeedbackById(String recordId) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(10)
				.query(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("_id", recordId)));
		return new SearchRequest(appConfig.getFormDataIndex()).types(appConfig.getFormDataIndexType())
				.source(searchSourceBuilder);
	}

	@Override
	public Boolean replyFeedback(UserInfo userInfo, ReplyFeedbackDto replyFeedbackDto) throws IOException {
		Map<String, Object> jsonMap = new HashMap<>();
		replyFeedbackDto.setUserId(userInfo.getId());
		if (StateMatrixManager.getUserData().get(userInfo.getId()) != null) {
			replyFeedbackDto
					.setUsername(StateMatrixManager.getUserData().get(userInfo.getId()).getUsername().toString());
		} else {
			ResponseData data = fetchUserInfo(userInfo.getId());
			if (data != null) {
				StateMatrixManager.getUserData().put(userInfo.getId(), data);
				replyFeedbackDto.setUsername(data.getUsername().toString());
			}
		}
		replyFeedbackDto.setReplyDate(new Date().getTime());
		SearchRequest searchRequest = buildQueryForGetFeedbackById(replyFeedbackDto.getRecordId());

		MultiSearchResponse response = elasticRepository.executeMultiSearchRequest(searchRequest);
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
		return formsDao.updateFormData(jsonMap, replyFeedbackDto.getRecordId());
	}

	@Override
	public List<Map<String, Object>> getApplications(UserInfo userInfo, SearchRequestDto searchRequestDto) {
		try {
			// query builder
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
			BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
			setRoleBasedSearchObject(userInfo, searchRequestDto);
			if (searchRequestDto != null && searchRequestDto.getSearchObjects() != null) {
				for (SearchObject objects : searchRequestDto.getSearchObjects()) {
					String key = objects.getKey();
					Object values = objects.getValues();
					if (Constants.ElasticSearchFields.MAPPING.containsKey(key)) {
						boolBuilder.must()
								.add(QueryBuilders.termsQuery(Constants.ElasticSearchFields.MAPPING.get(key), values));
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
			searchSourceBuilder.query(boolBuilder);
			searchSourceBuilder.sort(Constants.TIMESTAMP, SortOrder.DESC);
			System.out.println(searchSourceBuilder);
			// es call
			SearchRequest searchRequest = new SearchRequest(appConfig.getFormDataIndex())
					.types(appConfig.getFormIndexType()).source(searchSourceBuilder);
			LOGGER.info("Search Request : " + searchRequest);
			return formsDao.searchResponse(searchRequest);

		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "getApplications", e.getMessage()));
		}
		return null;
	}

	private void setRoleBasedSearchObject(UserInfo userInfo, SearchRequestDto searchRequestDto) {
		if (userInfo != null && userInfo.getRoles() != null) {
			for (Role role : userInfo.getRoles()) {
				SearchObject roleBasedSearch = new SearchObject();
				if (role.getName().equals(Roles.Institution.name())) {
					roleBasedSearch.setKey(Constants.CREATED_BY);
					roleBasedSearch.setValues(userInfo.getEmailId());
				} else if (role.getName().equals(Roles.Inspector.name())) {
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

	@Override
	public KeyValueList getApplicationsStatusCount() {
		try {
			// query builder
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
			searchSourceBuilder.aggregation(AggregationBuilders.terms("Total Pending")
					.field(Constants.ElasticSearchFields.MAPPING.get(Constants.STATUS)));

			SearchRequest searchRequest = new SearchRequest(appConfig.getFormDataIndex())
					.types(appConfig.getFormIndexType()).source(searchSourceBuilder);
			LOGGER.info("Search Request : " + searchRequest);
			List<Map<String, Object>> responseNode = formsDao.searchAggregationResponse(searchRequest);
			return translateResponse(responseNode);
		} catch (Exception ex) {
			LOGGER.error(String.format(Constants.EXCEPTION, "getApplicationsStatusCount", ex.getMessage()));
		}
		return null;

	}

	KeyValueList translateResponse(List<Map<String, Object>> responseNode) {
		KeyValueList list = new KeyValueList();
		List<KeyValue> listOfKeyValuePairs = new ArrayList<KeyValue>();
		for (Map<String, Object> eachMap : responseNode) {
			List<KeyValue> keyValueList = eachMap.entrySet().stream()
					.map(entry -> new KeyValue(entry.getKey().equals("New") ? "Total Pending" : entry.getKey(),
							entry.getValue()))
					.collect(Collectors.toList());
			listOfKeyValuePairs.addAll(keyValueList);
		}
		list.setKeyValues(listOfKeyValuePairs);
		return list;
	}

	@Override
	public Boolean saveFormSubmitv1(IncomingData incomingData) {
		Boolean indexed = Boolean.FALSE;
		try {
			if (StringUtils.isBlank(incomingData.getApplicationId())) {
				incomingData.setStatus(Constants.ApplicationStatus.SUBMITTED);
				incomingData.setTimestamp(DateUtils.getCurrentTimestamp());
				incomingData.setCreatedDate(DateUtils.getYyyyMmDdInUTC());
				indexed = formsDao.addFormData(incomingData);
			} else {
				incomingData.setUpdatedDate(DateUtils.getYyyyMmDdInUTC());
				indexed = formsDao.updateFormData(incomingData, incomingData.getApplicationId());
			}
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "saveFormSubmitv1", e.getMessage()));
		}
		return indexed;
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
	public Boolean reviewApplication(IncomingData incomingData) {
		try {
			IncomingData requestData = new IncomingData();
			requestData.setStatus(incomingData.getStatus());
			requestData.setComments(incomingData.getComments());
			requestData.setReviewedBy(incomingData.getReviewedBy());
			requestData.setReviewedDate(DateUtils.getYyyyMmDdInUTC());

			return formsDao.updateFormData(requestData, incomingData.getApplicationId());
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "reviewApplication", e.getMessage()));
			return Boolean.FALSE;
		}

	}

	@Override
	public Boolean assignApplication(AssignApplication assign) {
		try {
			IncomingData requestData = new IncomingData();
			assign.setAssignedDate(DateUtils.getYyyyMmDdInUTC());
			requestData.setInspection(assign);
			requestData.setStatus(assign.getStatus());

			return formsDao.updateFormData(requestData, assign.getApplicationId());
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "assignApplication", e.getMessage()));
			return Boolean.FALSE;
		}
	}

	public ConcurrentMap<Long, State> fetchAllStates() {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
		// es call
		SearchRequest searchRequest = new SearchRequest(appConfig.getFormStateIndex())
				.types(appConfig.getFormIndexType()).source(searchSourceBuilder);
		LOGGER.info("Search Request : " + searchRequest);
		return formsDao.fetchAllStates(searchRequest);
	}

	@Override
	public ConcurrentMap<String, StateMatrix> fetchAllStateMatrix() {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
		// es call
		SearchRequest searchRequest = new SearchRequest(appConfig.getFormStateMatrixIndex())
				.types(appConfig.getFormIndexType()).source(searchSourceBuilder);
		LOGGER.info("Search Request : " + searchRequest);
		return formsDao.fetchAllStateMatrix(searchRequest);
	}

}
