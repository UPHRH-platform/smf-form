package com.tarento.formservice.dao;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.elasticsearch.action.search.SearchRequest;
import org.springframework.http.HttpHeaders;

import com.tarento.formservice.model.FormData;
import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.model.State;
import com.tarento.formservice.model.StateMatrix;
import com.tarento.formservice.models.FormDetail;

/**
 * This interface for Portfolio contains the method which executes the Search
 * Request and Delete Request on Elastic Search Repositories
 * 
 * @author Darshan Nagesh
 *
 */
public interface FormsDao {

	Boolean addForm(FormDetail newForm);

	public Boolean addFormData(IncomingData incomingData);

	Boolean updateFormData(Map<String, Object> jsonMap, String id);

	Boolean updateFormData(Object object, String id);

	public Boolean addInteraction(FormData fData, HttpHeaders httpHeaders);

	public Boolean updateInteractions(Map<String, Object> jsonMap, String id);

	List<Map<String, Object>> searchResponse(SearchRequest searchRequest);
	
	List<Map<String, Object>> searchAggregationResponse(SearchRequest searchRequest);
	
	ConcurrentMap<Long, State> fetchAllStates(SearchRequest searchRequest); 
	
	ConcurrentMap<String, StateMatrix> fetchAllStateMatrix(SearchRequest searchRequest);

}
