package com.tarento.formservice.repository;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tarento.formservice.utils.Constants;

@Service
public class RestService {

	public static final Logger logger = LoggerFactory.getLogger(RestService.class);

	private static RestTemplate restTemplate;

	@Autowired
	private void RestService(RestTemplate restTemp) {
		restTemplate = restTemp;
	}

	public static Object postRequest(HttpHeaders headers, String url, Object request) {
		try {
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			HttpEntity<Object> httpEntity = new HttpEntity<>(request, headers);
			logger.info("URI Being passed : " + url);
			logger.info("HTTP Entity Being passed : " + httpEntity);
			Map<String, Object> response = restTemplate.postForObject(url, httpEntity, Map.class);
			if (response.containsKey(Constants.Parameters.RESPONSE_DATA)) {
				return response.get(Constants.Parameters.RESPONSE_DATA);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(String.format(Constants.EXCEPTION, "getResponse", e.getMessage()));
		}
		return null;
	}

}
