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

	@Value("${services.esindexer.host}")
	private String elasticHost;

	@Value("${services.esindexer.host.port}")
	private int elasticPort;

	@Value("${services.esindexer.username}")
	private String elasticUsername;

	@Value("${services.esindexer.password}")
	private String elasticPassword;

	@Value("${es.fs.forms.index.name}")
	private String formIndex;

	@Value("${es.fs.forms.document.type}")
	private String formIndexType;

	@Value("${es.fs.formsdata.index.name}")
	private String formDataIndex;

	@Value("${es.fs.formsdata.document.type}")
	private String formDataIndexType;

	@Value("(${es.fs.interactions.index.name}")
	private String formInteractionIndex;

	@Value("${cloud_storage_type}")
	private String provider;

	@Value("${azure_storage_container}")
	private String containerName;

	@Value("${azure_storage_key}")
	private String identity;

	@Value("${azure_storage_secret}")
	private String credential;

	@Value("${mail.smtp.host}")
	private String smtpHost;

	@Value("${mail.smtp.auth}")
	private String smtpAuth;

	@Value("${mail.smtp.port}")
	private String smtpPort;

	@Value("${mail.smtp.user}")
	private String smtpUser;

	@Value("${mail.smtp.password}")
	private String smtpPassword;

	@Value("${mail.smtp.email}")
	private String smtpEmail;
	
	@Value("${es.fs.state.index.name}")
	private String formStateIndex; 
	
	@Value("${es.fs.statematrix.index.name}")
	private String formStateMatrixIndex; 

}
