package com.tarento.formservice.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.RandomStringUtils;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarento.formservice.dao.FormsDao;
import com.tarento.formservice.model.ActivityLogs;
import com.tarento.formservice.model.FormData;
import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.model.State;
import com.tarento.formservice.model.StateMatrix;
import com.tarento.formservice.models.FormDetail;
import com.tarento.formservice.repository.ElasticSearchRepository;
import com.tarento.formservice.utils.AppConfiguration;
import com.tarento.formservice.utils.Constants;

@Repository(Constants.ServiceRepositories.FORM_REPO)
public class FormsDaoImpl implements FormsDao {

	public static final Logger LOGGER = LoggerFactory.getLogger(FormsDaoImpl.class);

	@Autowired
	AppConfiguration appConfig;

	@Autowired
	ElasticSearchRepository elasticsearchRepo;

	@Override
	public Boolean addForm(FormDetail newForm) {
		return elasticsearchRepo.writeDatatoElastic(newForm, newForm.getId().toString(), appConfig.getFormIndex(),
				appConfig.getFormIndexType());
	}

	@Override
	public Boolean updateForm(FormDetail newForm) {
		return elasticsearchRepo.updateElasticData(newForm, newForm.getId().toString(), appConfig.getFormIndex(),
				appConfig.getFormIndexType());
	}

	@Override
	public Boolean addFormData(IncomingData incomingData) {
		incomingData.setApplicationId(RandomStringUtils.random(15, Boolean.TRUE, Boolean.TRUE));
		return elasticsearchRepo.writeDatatoElastic(incomingData, incomingData.getApplicationId(),
				appConfig.getFormDataIndex(), appConfig.getFormIndexType());

	}

	@Override
	public Boolean updateFormData(Map<String, Object> jsonMap, String id) {
		return elasticsearchRepo.updateElasticData(jsonMap, id, appConfig.getFormDataIndex(),
				appConfig.getFormIndexType());
	}

	@Override
	public Boolean updateFormData(Object object, String id) {
		return elasticsearchRepo.updateElasticData(object, id, appConfig.getFormDataIndex(),
				appConfig.getFormIndexType());
	}

	@Override
	public List<Map<String, Object>> searchResponse(SearchRequest searchRequest) {
		try {
			List<Map<String, Object>> responseData = new ArrayList<>();
			MultiSearchResponse response = elasticsearchRepo.executeMultiSearchRequest(searchRequest);
			SearchResponse searchResponse = response.getResponses()[0].getResponse();
			SearchHit[] hit = searchResponse.getHits().getHits();
			for (SearchHit hits : hit) {
				Map<String, Object> sourceAsMap = hits.getSourceAsMap();
				sourceAsMap.put(Constants.APPLICATION_ID, hits.getId());
				responseData.add(sourceAsMap);
			}
			return responseData;
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "searchResponse", e.getMessage()));
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> searchAggregationResponse(SearchRequest searchRequest, String aggregationName) {
		try {
			List<Map<String, Object>> responseData = new ArrayList<>();
			MultiSearchResponse response = elasticsearchRepo.executeMultiSearchRequest(searchRequest);
			SearchResponse searchResponse = response.getResponses()[0].getResponse();
			Aggregations aggregations = searchResponse.getAggregations();
			if (!aggregationName.equals("Total Pending")) {
				ParsedFilter filters = aggregations.get(aggregationName);
				Aggregations subAggregations = filters.getAggregations();
				ParsedCardinality value = subAggregations.get("Count");
				Map<String, Object> eachRecordMap = new HashMap<String, Object>();
				eachRecordMap.put(aggregationName, value.getValue());
				responseData.add(eachRecordMap);
			} else {
				ParsedStringTerms subjects = aggregations.get(aggregationName);
				for (Terms.Bucket bucket : subjects.getBuckets()) {
					String key = (String) bucket.getKey();
					long docCount = bucket.getDocCount();
					Map<String, Object> eachRecordMap = new HashMap<String, Object>();
					eachRecordMap.put(key, docCount);
					responseData.add(eachRecordMap);
				}
			}

			return responseData;
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "searchResponse", e.getMessage()));
			return null;
		}
	}

	@Override
	public ConcurrentMap<Long, State> fetchAllStates(SearchRequest searchRequest) {
		ConcurrentMap<Long, State> stateMap = new ConcurrentHashMap<Long, State>();
		try {
			MultiSearchResponse response = elasticsearchRepo.executeMultiSearchRequest(searchRequest);
			SearchResponse searchResponse = response.getResponses()[0].getResponse();
			SearchHit[] hit = searchResponse.getHits().getHits();
			for (SearchHit hits : hit) {
				Map<String, Object> sourceAsMap = hits.getSourceAsMap();
				State eachState = new ObjectMapper().convertValue(sourceAsMap, State.class);
				stateMap.put(eachState.getId(), eachState);
			}
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "fetchAllStates : Not able to load states !!! ",
					e.getMessage()));
			return null;
		}
		return stateMap;
	}

	@Override
	public ConcurrentMap<String, List<StateMatrix>> fetchAllStateMatrix(SearchRequest searchRequest) {
		ConcurrentMap<String, List<StateMatrix>> stateMatrixMap = new ConcurrentHashMap<String, List<StateMatrix>>();
		try {
			MultiSearchResponse response = elasticsearchRepo.executeMultiSearchRequest(searchRequest);
			SearchResponse searchResponse = response.getResponses()[0].getResponse();
			SearchHit[] hit = searchResponse.getHits().getHits();
			for (SearchHit hits : hit) {
				Map<String, Object> sourceAsMap = hits.getSourceAsMap();
				StateMatrix eachStateMatrix = new ObjectMapper().convertValue(sourceAsMap, StateMatrix.class);
				if (stateMatrixMap.containsKey(eachStateMatrix.getAction())) {
					List<StateMatrix> stateMatrixList = stateMatrixMap.get(eachStateMatrix.getAction());
					stateMatrixList.add(eachStateMatrix);
				} else {
					List<StateMatrix> stateMatrixList = new ArrayList<StateMatrix>();
					stateMatrixList.add(eachStateMatrix);
					stateMatrixMap.put(eachStateMatrix.getAction(), stateMatrixList);
				}
			}
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION,
					"fetchAllStateMatrix : Not able to load state matrices !!! ", e.getMessage()));
			return null;
		}
		return stateMatrixMap;
	}

	@Override
	public Boolean addLogs(ActivityLogs activityLogs) {
		return elasticsearchRepo.writeDatatoElastic(activityLogs,
				RandomStringUtils.random(15, Boolean.TRUE, Boolean.TRUE), appConfig.getActivityLogIndex(),
				appConfig.getActivityLogIndexType());
	}

}
