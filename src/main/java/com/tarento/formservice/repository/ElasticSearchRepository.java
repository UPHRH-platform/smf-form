package com.tarento.formservice.repository;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.tarento.formservice.models.DataObject;
import com.tarento.formservice.utils.AppConfiguration;
import com.tarento.formservice.utils.Constants;

/**
 * This Repository Class is used to perform the transactions of storing the data
 * into the Elastic Search Repository
 * 
 * @author Darshan Nagesh
 *
 */
@Service(Constants.ServiceRepositories.ELASTICSEARCH_REPO)
public class ElasticSearchRepository {

	public static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchRepository.class);
	String daoImplMarker = Constants.ServiceRepositories.ELASTICSEARCH_REPO;
	Marker marker = MarkerFactory.getMarker(daoImplMarker);

	private RestTemplate restTemplate;
	private AppConfiguration appConfig;

	private RestHighLevelClient client;

	@Autowired
	private ElasticSearchRepository(AppConfiguration appConfiguration, RestTemplate restTemp) {
		appConfig = appConfiguration;
		restTemplate = restTemp;
		client = connectToElasticSearch();
	}

	private RestHighLevelClient connectToElasticSearch() {
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(appConfig.getElasticUsername(), appConfig.getElasticPassword()));

		HttpClientConfigCallback httpClientConfigCallback = new HttpClientConfigCallback() {
			@Override
			public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
				return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
			}
		};
		return new RestHighLevelClient(
				RestClient.builder(new HttpHost(appConfig.getElasticHost(), appConfig.getElasticPort())));
	}

	/**
	 * Based on the Transaction Index Data Obtained and the URL with Headers, this
	 * method will put the Data obtained on the Elastic Search Database and returns
	 * the response in the form of Positive or Negative outcome (True Or False)
	 * 
	 * @param transactionIndex
	 * @param url
	 * @param headers
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Boolean saveMyDataObject(Object object, String url, HttpHeaders headers) {
		ResponseEntity<Map> map = null;
		try {
			map = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(object, headers), Map.class);
		} catch (final HttpClientErrorException httpClientErrorException) {
			LOGGER.error("Error :", httpClientErrorException);
		} catch (HttpServerErrorException httpServerErrorException) {
			LOGGER.error("Error :", httpServerErrorException);
		} catch (Exception e) {
			LOGGER.error("Error: ", e);
		}
		if (map != null && map.getStatusCode() != null && (map.getStatusCode() == HttpStatus.OK)
				|| (map.getStatusCode() == HttpStatus.CREATED)) {
			return true;
		}
		return false;
	}

	public Boolean writeDatatoElastic(Object object, String id, String indexName, String documentType) {
		try {
			IndexRequest indexRequest = new IndexRequest(indexName, documentType, id).source(new Gson().toJson(object),
					XContentType.JSON);
			IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
			if (!StringUtils.isBlank(response.toString()))
				LOGGER.info("Response : {}", response);
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "writeDatatoElastic", e.getMessage()));
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	// Update ES Data
	public Boolean updateElasticData(Object object, String id, String indexName, String documentType) {
		try {
			UpdateRequest updateRequest = new UpdateRequest(indexName, documentType, id).doc(new Gson().toJson(object),
					XContentType.JSON);
			UpdateResponse response = client.update(updateRequest, RequestOptions.DEFAULT);
			if (!StringUtils.isBlank(response.toString()))
				LOGGER.info("Updated Response : {}", response.getResult());
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "updateElasticData", e.getMessage()));
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	public Boolean writeBulkDatatoElastic(BulkRequest request) throws IOException {
		try {
			BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
			if (!StringUtils.isBlank(response.toString()))
				LOGGER.info("Response : {}", response);
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "writeBulkDatatoElastic", e.getMessage()));
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	/**
	 * Based on the Transaction Index Data Obtained and the URL with Headers, this
	 * method will put the Data obtained on the Elastic Search Database and returns
	 * the response in the form of Positive or Negative outcome (True Or False)
	 * 
	 * @param transactionIndex
	 * @param url
	 * @param headers
	 * @return
	 */
	public Boolean saveData(DataObject dataObject, String url, HttpHeaders headers) {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> map = null;
		try {
			map = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(dataObject, headers), Map.class);
		} catch (final HttpClientErrorException httpClientErrorException) {
			LOGGER.error("Error:", httpClientErrorException);
		} catch (HttpServerErrorException httpServerErrorException) {
			LOGGER.error("Error : ", httpServerErrorException);
		} catch (Exception e) {
			LOGGER.error("Error : ", e);
		}
		if (map != null && map.getStatusCode() != null
				&& ((map.getStatusCode() == HttpStatus.OK) || (map.getStatusCode() == HttpStatus.CREATED))) {
			return true;
		}
		return false;
	}

	/**
	 * This method receives the Search Request which already contains a query to be
	 * searched with. Method will execute the Search Request by using the
	 * RestHighLevelClient and sends the reponse back
	 * 
	 * @param searchRequest
	 * @return
	 */
	public MultiSearchResponse executeMultiSearchRequest(SearchRequest searchRequest) {
		MultiSearchResponse response = null;
		try {
			MultiSearchRequest multiRequest = new MultiSearchRequest();
			multiRequest.add(searchRequest);

			response = client.msearch(multiRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			LOGGER.error(marker, "Encountered an error while connecting : ", e);
			LOGGER.error(marker, "Error Message to report :{} ", e.getMessage());
		}
		return response;
	}

	public SearchResponse executeSearchRequest(SearchRequest searchRequest) {
		SearchResponse response = null;
		try {
			response = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			LOGGER.error(marker, "Encountered an error while connecting : ", e);
			LOGGER.error(marker, "Error Message to report : {}", e.getMessage());
		}
		return response;
	}
}
