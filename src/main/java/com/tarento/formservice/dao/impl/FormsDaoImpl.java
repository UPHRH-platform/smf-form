package com.tarento.formservice.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;

import com.tarento.formservice.dao.FormsDao;
import com.tarento.formservice.model.FormData;
import com.tarento.formservice.model.IncomingData;
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
	public Boolean addFormData(IncomingData incomingData) {
		return elasticsearchRepo.writeDatatoElastic(incomingData,
				RandomStringUtils.random(15, Boolean.TRUE, Boolean.TRUE), appConfig.getFormDataIndex(),
				appConfig.getFormDataIndexType());

	}

	@Override
	public Boolean updateFormData(Map<String, Object> jsonMap, String id) {
		return elasticsearchRepo.updateElasticData(jsonMap, id, appConfig.getFormDataIndex(),
				appConfig.getFormDataIndexType());
	}

	@Override
	public Boolean updateFormData(Object object, String id) {
		return elasticsearchRepo.updateElasticData(object, id, appConfig.getFormDataIndex(),
				appConfig.getFormDataIndexType());
	}

	@Override
	public Boolean addInteraction(FormData fData, HttpHeaders httpHeaders) {
		return elasticsearchRepo.writeDatatoElastic(fData, RandomStringUtils.random(15, Boolean.TRUE, Boolean.TRUE),
				appConfig.getFormInteractionIndex(), appConfig.getFormIndexType());
	}

	@Override
	public Boolean updateInteractions(Map<String, Object> jsonMap, String id) {
		return elasticsearchRepo.updateElasticData(jsonMap, id, appConfig.getFormInteractionIndex(),
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

}
