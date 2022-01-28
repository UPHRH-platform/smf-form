package com.tarento.formservice.dao.impl;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.tarento.formservice.dao.FormsDao;
import com.tarento.formservice.model.FormData;
import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.models.FormDetail;
import com.tarento.formservice.utils.Constants;

@Repository(Constants.ServiceRepositories.FORM_REPO)
public class FormsDaoImpl implements FormsDao {

	public static final Logger logger = LoggerFactory.getLogger(FormsDaoImpl.class);
	String daoImplMarker = Constants.ServiceRepositories.FORM_REPO + Constants.Markers.DAO_IMPL;
	Marker marker = MarkerFactory.getMarker(daoImplMarker);
	private RestHighLevelClient client;
	private String elasticHost;
	@SuppressWarnings("unused")
	private int elasticPort;
	private String elasticUsername;
	private String elasticPassword;
	private String formsIndexName;
	private String interactionIndexName;
	private String formsDocumentType;

	public FormsDaoImpl(@Value("${services.esindexer.host}") String elasticHost,
			@Value("${services.esindexer.username}") String elasticUsername,
			@Value("${services.esindexer.password}") String elasticPassword,
			@Value("${es.fs.forms.index.name}") String formsIndexName,
			@Value("${es.fs.interactions.index.name}") String interactionIndexName,
			@Value("${es.fs.forms.document.type}") String formsDocumentType,
			@Value("${services.esindexer.host.port}") int elasticPort) {
		this.elasticUsername = elasticUsername;
		this.elasticPassword = elasticPassword;
		this.elasticHost = elasticHost;
		this.elasticPort = elasticPort;
		this.formsIndexName = formsIndexName;
		this.interactionIndexName = interactionIndexName;
		this.formsDocumentType = formsDocumentType;
		this.client = connectToElasticSearch();
	}

	private RestHighLevelClient connectToElasticSearch() {
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(elasticUsername, elasticPassword));
		// SET SSL CONTEXT
		// RestClient restClient = RestClient.builder(new HttpHost(esHost, esPort,
		// "https"))
		// .setHttpClientConfigCallback(httpClientBuilder -> {
		// HttpAsyncClientBuilder httpAsyncClientBuilder =
		// httpClientBuilder.setSSLContext(sslcontext);
		// return httpAsyncClientBuilder;
		// })
		// .build();

		HttpClientConfigCallback httpClientConfigCallback = new HttpClientConfigCallback() {
			@Override
			public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
				return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
			}
		};
		return new RestHighLevelClient(RestClient.builder(new HttpHost(elasticHost, elasticPort))
				.setHttpClientConfigCallback(httpClientConfigCallback));
	}

	@Override
	public MultiSearchResponse executeMultiSearchRequest(SearchRequest searchRequest) {
		MultiSearchRequest multiRequest = new MultiSearchRequest();
		MultiSearchResponse response = null;
		// logger.info(marker, "ES Query is : {}", searchRequest.source());
		multiRequest.add(searchRequest);
		try {
			response = client.msearch(multiRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			logger.error(marker, "Encountered an error while connecting : ", e);
			logger.error(marker, "Error Message to report :{} ", e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean addNewForm(FormDetail newForm, HttpHeaders httpHeaders) throws IOException {
		if (newForm.getId() != null) {
			newForm.setUpdatedDate(new Date().getTime());
		} else {
			newForm.setId(new Date().getTime());
			newForm.setUpdatedDate(new Date().getTime());
			newForm.setVersion(1);
		}
		IndexRequest indexRequest = new IndexRequest().index(formsIndexName).type(formsDocumentType)
				.source(new Gson().toJson(newForm), XContentType.JSON);
		logger.info("Index Request Description: {} ", indexRequest.getDescription());
		IndexResponse response = null;
		response = client.index(indexRequest, RequestOptions.DEFAULT);

		return (response != null);
	}

	@Override
	public SearchResponse executeSearchRequest(SearchRequest searchRequest) {
		SearchResponse response = null;
		logger.info(marker, "ES Query is : {}", searchRequest.source());
		try {
			response = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			logger.error(marker, "Encountered an error while connecting : ", e);
			logger.error(marker, "Error Message to report : {}", e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean saveFormSubmit(IncomingData incomingData, HttpHeaders httpHeaders) throws IOException {
		IndexRequest indexRequest = new IndexRequest().index("fs-forms-data").type(formsDocumentType)
				.source(new Gson().toJson(incomingData), XContentType.JSON);
		logger.info("Index Request Description: {} ", indexRequest.getDescription());
		IndexResponse response = null;
		response = client.index(indexRequest, RequestOptions.DEFAULT);
		return (response != null);
	}

	@Override
	public Boolean verifyFeedback(Map<String, Object> jsonMap, String id) throws IOException {
		UpdateRequest request = new UpdateRequest().index("fs-forms-data").type(formsDocumentType).id(id).doc(jsonMap);
		UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
		return (response != null);
	}

	@Override
	public Boolean addInteraction(FormData fData, HttpHeaders httpHeaders) throws IOException {
		IndexRequest indexRequest = new IndexRequest().index(interactionIndexName).type(formsDocumentType)
				.source(new Gson().toJson(fData), XContentType.JSON);
		logger.info("Index Request Description: {} ", indexRequest.getDescription());
		IndexResponse response = null;
		response = client.index(indexRequest, RequestOptions.DEFAULT);
		return (response != null);
	}

	@Override
	public UpdateRequest addBulkUpdateRequest(String id, Map<String, Object> jsonMap) throws IOException {
		return new UpdateRequest().index(interactionIndexName).type(formsDocumentType).id(id).doc(jsonMap);
	}

	@Override
	public Boolean updateInteraction(Map<String, Object> jsonMap, String id, HttpHeaders httpHeaders)
			throws IOException {
		UpdateRequest request = new UpdateRequest().index(interactionIndexName).type(formsDocumentType).id(id)
				.doc(jsonMap);
		UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
		return (response != null);
	}

	@Override
	public Boolean challengeFeedback(Map<String, Object> jsonMap, String id) throws IOException {
		UpdateRequest request = new UpdateRequest().index("fs-forms-data").type(formsDocumentType).id(id).doc(jsonMap);
		UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
		return (response != null);
	}

	@Override
	public Boolean voteFeedback(Map<String, Object> jsonMap, String id) throws IOException {
		UpdateRequest request = new UpdateRequest().index("fs-forms-data").type(formsDocumentType).id(id)
				.doc(new Gson().toJson(jsonMap), XContentType.JSON);
		UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
		return (response != null);
	}

	@Override
	public Boolean replyFeedback(Map<String, Object> jsonMap, String id) throws IOException {
		UpdateRequest request = new UpdateRequest().index("fs-forms-data").type(formsDocumentType).id(id)
				.doc(new Gson().toJson(jsonMap), XContentType.JSON);
		UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
		return (response != null);
	}

	@Override
	public Boolean updateInteractions(Map<String, Object> jsonMap, String id) throws IOException {
		UpdateRequest request = new UpdateRequest().index("fs-interactions").type(formsDocumentType).id(id)
				.doc(new Gson().toJson(jsonMap), XContentType.JSON);
		UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
		return (response != null);
	}

	@Override
	public Boolean updateBulkRequest(BulkRequest request) throws IOException {
		BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
		return !bulkResponse.hasFailures();
	}

}
