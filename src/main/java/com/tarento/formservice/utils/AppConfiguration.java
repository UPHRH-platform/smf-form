package com.tarento.formservice.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

@Configuration
@SuppressWarnings("all")
@Getter
@PropertySource(value = { "/application.properties" })
public class AppConfiguration {

	@Value("${cloud_storage_type}")
	String provider;

	@Value("${azure_storage_container}")
	String containerName;

	@Value("${azure_storage_key}")
	String identity;

	@Value("${azure_storage_secret}")
	String credential;

}
