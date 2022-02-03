package com.tarento.formservice.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.tarento.formservice.models.Form;
import com.tarento.formservice.repository.ElasticSearchRepository;
import com.tarento.formservice.service.FormsService;
import com.tarento.formservice.service.JsonFormsService;
import com.tarento.formservice.utils.AppConfiguration;
import com.tarento.formservice.utils.Constants;

@Service(Constants.ServiceRepositories.JSON_FORMS_SERVICE)
public class JsonFormsServiceImpl implements JsonFormsService {

	public static final Logger LOGGER = LoggerFactory.getLogger(JsonFormsService.class);

	private static final String AUTHORIZATION = "Authorization";

	Gson gson = new Gson();

	@Autowired
	AppConfiguration appConfiguration;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private FormsService formsService;

	@Autowired
	private ElasticSearchRepository elasticRepository;

	public static JsonObject convertToJsonObject(Object payload) {
		GsonBuilder builder = new GsonBuilder();
		return (JsonObject) builder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create().toJsonTree(payload);
	}

	@Override
	public Boolean processJsonForms(Object jsonFormObject, Form form,
			com.tarento.formservice.model.FormDetail formDetails) throws IOException {
		LOGGER.info("FORM ID: {}", formDetails.getFormId());
		com.tarento.formservice.models.FormDetail detail = formsService
				.getFormById(Long.parseLong(formDetails.getFormId()));
		detail.setSecondaryId(form.getSecondaryId());
		detail.setTitle("Title");

		String docId = "";
		SearchRequest searchRequest;
		searchRequest = new SearchRequest(formDetails.getEsIndexName());
		HttpHeaders headers = new HttpHeaders();
		String auth = appConfiguration.getElasticUsername() + ":" + appConfiguration.getElasticPassword();
		byte[] bytes = auth.getBytes(StandardCharsets.UTF_8);
		String base64Encoded = java.util.Base64.getEncoder().encodeToString(bytes);
		String authHeader = "Basic " + base64Encoded;
		headers.add(AUTHORIZATION, authHeader);
		headers.add("Content-Type", "application/json");
		ObjectMapper mapper = new ObjectMapper();

		JsonNode node = mapper.convertValue(jsonFormObject, JsonNode.class);
		// Date to Epoch conversion
		Long epoch = null;
		try {
			epoch = new java.text.SimpleDateFormat("MM-dd-yyyy HH:mm:ss")
					.parse(node.findValue("Date").asText() + " 23:59:59").getTime();
		} catch (ParseException e) {
			LOGGER.info("Encounter an exception while parsing the Date" + e.getMessage());
		}
		node = ((ObjectNode) node).put("Date", epoch);
		if (formDetails.getEsIndexName().equals("kronos-tarento-summary")) {
			if (node.get(Constants.KronosDashboards.DATE) != null
					&& !node.get(Constants.KronosDashboards.DATE).asText().isEmpty()) {
				QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(
						Constants.KronosDashboards.DATE, node.get(Constants.KronosDashboards.DATE).asText()));
				SearchSourceBuilder ssBuilder = new SearchSourceBuilder().query(queryBuilder);
				searchRequest.source(ssBuilder);
				Object searchResponse = elasticRepository.executeMultiSearchRequest(searchRequest);
				JsonNode responseJsonNode = mapper.convertValue(searchResponse, JsonNode.class);
				int hitSize = responseJsonNode.get("responses").get(0).get("response").get("hits").get("hits").size();
				// // check time stamp ? stored - > Override

				if (hitSize > 0) {
					// override the document
					docId = responseJsonNode.get("responses").get(0).get("response").get("hits").get("hits").get(0)
							.get("id").asText();
					Object res;
					HttpEntity<Object> entity = new HttpEntity<>(node, headers);
					res = restTemplate.exchange(
							Constants.HTTP + appConfiguration.getElasticHost() + "/" + formDetails.getEsIndexName()
									+ "/" + formDetails.getEsIndexDocType() + "/" + docId,
							HttpMethod.PUT, entity, Object.class).getBody();

				} else {
					// check time stamp ? stored - > Override IN LOGS index
					QueryBuilder qBuilder = QueryBuilders.boolQuery()
							.must(QueryBuilders.matchQuery("sourceAsMap.Date",
									node.get(Constants.KronosDashboards.DATE).asText()))
							.must(QueryBuilders.matchQuery(Constants.KronosDashboards._INDEX,
									formDetails.getEsIndexName()));
					SearchSourceBuilder sBuilder = new SearchSourceBuilder().query(qBuilder);
					searchRequest.source(sBuilder);
					Object sRes = elasticRepository.executeMultiSearchRequest(searchRequest);
					JsonNode resJsonNode = mapper.convertValue(searchResponse, JsonNode.class);
					int hSize = responseJsonNode.get("responses").get(0).get("response").get("hits").get("hits").size();

					if (hSize <= 0) {
						// check if timestamp is stored for the same month and year with the same IBU
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(Long.parseLong(node.get(Constants.KronosDashboards.DATE).asText()));
						calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
						long startDayEpoch = calendar.getTimeInMillis();
						calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
						long endDayEpoch = calendar.getTimeInMillis();

						QueryBuilder qqBuilder = QueryBuilders.boolQuery().must(QueryBuilders
								.rangeQuery(Constants.KronosDashboards.DATE).gte(startDayEpoch).lte(endDayEpoch));
						SearchSourceBuilder ssb = new SearchSourceBuilder().query(qqBuilder);
						SearchRequest sRequest = new SearchRequest(formDetails.getEsIndexName());
						sRequest.source(ssb);
						Object sResponse = elasticRepository.executeMultiSearchRequest(sRequest);
						JsonNode rJNode = mapper.convertValue(sResponse, JsonNode.class);
						int hitSizeOfRes = rJNode.get("responses").get(0).get("response").get("hits").get("hits")
								.size();

						// Maintain logs - DELETE RECORD - INSERT RECORD To LOGS - INSERT NEW RECORD TO
						// THE DATA INDEX
						if (hitSizeOfRes > 0) {
							// push all the docs to "{name}-logs" index
							for (int i = 0; i < hitSizeOfRes; i++) {
								// Check Date is greater than the stored records
								// Then push the record to the LOGS index & delete it from Data Index
								if (rJNode.get("responses").get(0).get("response").get("hits").get("hits").get(i)
										.get("sourceAsMap").get(Constants.KronosDashboards.DATE)
										.asLong() < node.get(Constants.KronosDashboards.DATE).asLong()) {
									// Store the index
									HttpEntity<Object> entity = new HttpEntity<>(
											mapper.convertValue(rJNode.get("responses").get(0).get("response")
													.get("hits").get("hits").get(i), Map.class),
											headers);
									restTemplate.exchange(
											Constants.HTTP + appConfiguration.getElasticHost() + "/"
													+ formDetails.getAction() + "/" + formDetails.getEsIndexDocType(),
											HttpMethod.POST, entity, Object.class).getBody();
									// delete the docs from the index
									// find the id
									String documentId;
									documentId = rJNode.get("responses").get(0).get("response").get("hits").get("hits")
											.get(i).get("id").asText();
									HttpEntity<?> request = new HttpEntity<>(headers);
									Object res = restTemplate.exchange(
											Constants.HTTP + appConfiguration.getElasticHost() + "/"
													+ formDetails.getEsIndexName() + "/"
													+ formDetails.getEsIndexDocType() + "/" + documentId,
											HttpMethod.DELETE, request, String.class);
									HttpEntity<Object> entity1 = new HttpEntity<>(node, headers);
									Object o = restTemplate.exchange(
											Constants.HTTP + appConfiguration.getElasticHost() + "/"
													+ formDetails.getEsIndexName() + "/"
													+ formDetails.getEsIndexDocType(),
											HttpMethod.POST, entity1, Object.class).getBody();
								}
							}
						} else {
							HttpEntity<Object> entity = new HttpEntity<>(node, headers);
							Object o = restTemplate.exchange(
									Constants.HTTP + appConfiguration.getElasticHost() + "/"
											+ formDetails.getEsIndexName() + "/" + formDetails.getEsIndexDocType(),
									HttpMethod.POST, entity, Object.class).getBody();
						}
					}
				}
			}
		} else if (formDetails.getEsIndexName().equals("kronos-mis-ibu")) {
			if (node.get(Constants.KronosDashboards.DATE) != null
					&& !node.get(Constants.KronosDashboards.DATE).asText().isEmpty()) {
				QueryBuilder queryBuilder = QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery(Constants.KronosDashboards.DATE,
								node.get(Constants.KronosDashboards.DATE).asText()))
						.must(QueryBuilders.matchQuery(Constants.KronosDashboards.IBU,
								node.get(Constants.KronosDashboards.IBU).asText()))
						.must(QueryBuilders.matchQuery(Constants.KronosDashboards.COUNTRY,
								node.get(Constants.KronosDashboards.COUNTRY).asText()));
				SearchSourceBuilder ssBuilder = new SearchSourceBuilder().query(queryBuilder);
				searchRequest.source(ssBuilder);
				Object searchResponse = elasticRepository.executeMultiSearchRequest(searchRequest);
				JsonNode responseJsonNode = mapper.convertValue(searchResponse, JsonNode.class);
				int hitSize = responseJsonNode.get("responses").get(0).get("response").get("hits").get("hits").size();
				// // check time stamp ? stored - > Override

				if (hitSize > 0) {
					// override the document
					docId = responseJsonNode.get("responses").get(0).get("response").get("hits").get("hits").get(0)
							.get("id").asText();
					Object res;
					HttpEntity<Object> entity = new HttpEntity<>(node, headers);
					res = restTemplate.exchange(
							Constants.HTTP + appConfiguration.getElasticHost() + "/" + formDetails.getEsIndexName()
									+ "/" + formDetails.getEsIndexDocType() + "/" + docId,
							HttpMethod.PUT, entity, Object.class).getBody();
				} else {
					// check time stamp ? stored - > Override IN LOGS index
					QueryBuilder qBuilder = QueryBuilders.boolQuery()
							.must(QueryBuilders.matchQuery("sourceAsMap.Date",
									node.get(Constants.KronosDashboards.DATE).asText()))
							.must(QueryBuilders.matchQuery(Constants.KronosDashboards.IBU,
									node.get(Constants.KronosDashboards.IBU).asText()))
							.must(QueryBuilders.matchQuery(Constants.KronosDashboards.COUNTRY,
									node.get(Constants.KronosDashboards.COUNTRY).asText()))
							.must(QueryBuilders.matchQuery(Constants.KronosDashboards._INDEX,
									formDetails.getEsIndexName()));
					SearchSourceBuilder sBuilder = new SearchSourceBuilder().query(qBuilder);
					searchRequest.source(sBuilder);
					Object sRes = elasticRepository.executeMultiSearchRequest(searchRequest);
					JsonNode resJsonNode = mapper.convertValue(searchResponse, JsonNode.class);
					int hSize = responseJsonNode.get("responses").get(0).get("response").get("hits").get("hits").size();

					if (hSize <= 0) {
						// check if timestamp is stored for the same month and year with the same IBU
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(Long.parseLong(node.get(Constants.KronosDashboards.DATE).asText()));
						calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
						long startDayEpoch = calendar.getTimeInMillis();
						calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
						long endDayEpoch = calendar.getTimeInMillis();

						QueryBuilder qqBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.matchQuery(Constants.KronosDashboards.IBU,
										node.get(Constants.KronosDashboards.IBU).asText()))
								.must(QueryBuilders.matchQuery(Constants.KronosDashboards.COUNTRY,
										node.get(Constants.KronosDashboards.COUNTRY).asText()))
								.must(QueryBuilders.rangeQuery(Constants.KronosDashboards.DATE).gte(startDayEpoch)
										.lte(endDayEpoch));
						SearchSourceBuilder ssb = new SearchSourceBuilder().query(qqBuilder);
						SearchRequest sRequest = new SearchRequest(formDetails.getEsIndexName());
						sRequest.source(ssb);
						Object sResponse = elasticRepository.executeMultiSearchRequest(sRequest);
						JsonNode rJNode = mapper.convertValue(sResponse, JsonNode.class);
						int hitSizeOfRes = rJNode.get("responses").get(0).get("response").get("hits").get("hits")
								.size();

						// Maintain logs - DELETE RECORD - INSERT RECORD To LOGS - INSERT NEW RECORD TO
						// THE DATA INDEX
						if (hitSizeOfRes > 0) {
							// push all the docs to "{name}-logs" index
							for (int i = 0; i < hitSizeOfRes; i++) {
								// Check Date is greater than the stored records
								// Then push the record to the LOGS index & delete it from Data Index
								if (rJNode.get("responses").get(0).get("response").get("hits").get("hits").get(i)
										.get("sourceAsMap").get(Constants.KronosDashboards.DATE)
										.asLong() < node.get(Constants.KronosDashboards.DATE).asLong()) {
									// Store the index
									HttpEntity<Object> entity = new HttpEntity<>(
											mapper.convertValue(rJNode.get("responses").get(0).get("response")
													.get("hits").get("hits").get(i), Map.class),
											headers);
									restTemplate.exchange(
											Constants.HTTP + appConfiguration.getElasticHost() + "/"
													+ formDetails.getAction() + "/" + formDetails.getEsIndexDocType(),
											HttpMethod.POST, entity, Object.class).getBody();
									// delete the docs from the index
									// find the id
									String documentId;
									documentId = rJNode.get("responses").get(0).get("response").get("hits").get("hits")
											.get(i).get("id").asText();
									HttpEntity<?> request = new HttpEntity<>(headers);
									Object res = restTemplate.exchange(
											Constants.HTTP + appConfiguration.getElasticHost() + "/"
													+ formDetails.getEsIndexName() + "/"
													+ formDetails.getEsIndexDocType() + "/" + documentId,
											HttpMethod.DELETE, request, String.class);
									HttpEntity<Object> entity1 = new HttpEntity<>(node, headers);
									Object o = restTemplate.exchange(
											Constants.HTTP + appConfiguration.getElasticHost() + "/"
													+ formDetails.getEsIndexName() + "/"
													+ formDetails.getEsIndexDocType(),
											HttpMethod.POST, entity1, Object.class).getBody();
								}
							}
						} else {
							HttpEntity<Object> entity = new HttpEntity<>(node, headers);
							Object o = restTemplate.exchange(
									Constants.HTTP + appConfiguration.getElasticHost() + "/"
											+ formDetails.getEsIndexName() + "/" + formDetails.getEsIndexDocType(),
									HttpMethod.POST, entity, Object.class).getBody();
						}
					}
				}
			}
		}
		return Boolean.TRUE;
	}
}
