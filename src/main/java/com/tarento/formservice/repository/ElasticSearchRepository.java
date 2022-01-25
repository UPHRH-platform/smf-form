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
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

/**
 * This Repository Class is used to perform the transactions of storing the data
 * into the Elastic Search Repository
 * 
 * @author Darshan Nagesh
 *
 */
@Service
public class ElasticSearchRepository {

	public static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchRepository.class);

	private final RestTemplate restTemplate;
	private RestHighLevelClient client;
	private String elasticHost;
	private int elasticPort;
	private String elasticUsername;
	private String elasticPassword;

	public ElasticSearchRepository(RestTemplate restTemplate, @Value("${services.esindexer.host}") String elasticHost,
			@Value("${services.esindexer.host.port}") int elasticPort,
			@Value("${services.esindexer.username}") String elasticUsername,
			@Value("${services.esindexer.password}") String elasticPassword) {
		this.restTemplate = restTemplate;
		this.elasticHost = elasticHost;
		this.elasticPort = elasticPort;
		this.elasticUsername = elasticUsername;
		this.elasticPassword = elasticPassword;
		this.client = connectToElasticSearch();
	}

	private RestHighLevelClient connectToElasticSearch() {
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(elasticUsername, elasticPassword));

		HttpClientConfigCallback httpClientConfigCallback = new HttpClientConfigCallback() {
			@Override
			public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
				return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
			}
		};
		return new RestHighLevelClient(RestClient.builder(new HttpHost(elasticHost, elasticPort))
				.setHttpClientConfigCallback(httpClientConfigCallback));
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

	public Boolean writeDatatoElastic(Object object, String id, String indexName, String documentType)
			throws IOException {
		try {
			IndexRequest indexRequest = new IndexRequest(indexName, documentType, id).source(new Gson().toJson(object),
					XContentType.JSON);
			IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
			if (!StringUtils.isBlank(response.toString()))
				LOGGER.info("Response : {}", response);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	// Update ES Data
	public Boolean updateElasticData(Object object, String id, String indexName, String documentType)
			throws IOException {
		try {
			UpdateRequest updateRequest = new UpdateRequest(indexName, documentType, id).doc(new Gson().toJson(object),
					XContentType.JSON);
			UpdateResponse response = client.update(updateRequest, RequestOptions.DEFAULT);
			if (!StringUtils.isBlank(response.toString()))
				LOGGER.info("Updated Response : {}", response.getResult());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
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
			LOGGER.error(e.getMessage());
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
}
