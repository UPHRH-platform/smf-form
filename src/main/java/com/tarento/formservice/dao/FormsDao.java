package com.tarento.formservice.dao;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.http.HttpHeaders;

import com.tarento.formservice.model.FormData;
import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.models.FormDetail;

/**
 * This interface for Portfolio contains the method which executes the Search
 * Request and Delete Request on Elastic Search Repositories
 * 
 * @author Darshan Nagesh
 *
 */
public interface FormsDao {

	/**
	 * This method receives the Search Request which already contains a query to be
	 * searched with. Method will execute the Search Request by using the
	 * RestHighLevelClient and sends the reponse back
	 * 
	 * @param searchRequest
	 * @return
	 */
	public MultiSearchResponse executeMultiSearchRequest(SearchRequest searchRequest);

	/**
	 * This method receives a list of Project Information records to be deleted. It
	 * gets invoked when a project information update is fired. On update, it
	 * removes the existing record and replaces with a new record.
	 * 
	 * @param deleteRequestList
	 * @return
	 */
	public Boolean addNewForm(FormDetail newForm, HttpHeaders httpHeaders) throws IOException;

	public Boolean addInteraction(FormData fData, HttpHeaders httpHeaders) throws IOException;

	public SearchResponse executeSearchRequest(SearchRequest searchRequest);

	public Boolean saveFormSubmit(IncomingData incomingData, HttpHeaders httpHeaders) throws IOException;

	public Boolean verifyFeedback(Map<String, Object> jsonMap, String id) throws IOException;

	public Boolean challengeFeedback(Map<String, Object> jsonMap, String id) throws IOException;

	public Boolean voteFeedback(Map<String, Object> jsonMap, String id) throws IOException;

	public Boolean replyFeedback(Map<String, Object> jsonMap, String id) throws IOException;

	public Boolean updateInteractions(Map<String, Object> jsonMap, String id) throws IOException;

	Boolean updateInteraction(Map<String, Object> jsonMap, String id, HttpHeaders httpHeaders) throws IOException;

	UpdateRequest addBulkUpdateRequest(String id, Map<String, Object> jsonMap) throws IOException;

	public Boolean updateBulkRequest(BulkRequest request) throws IOException;

}
