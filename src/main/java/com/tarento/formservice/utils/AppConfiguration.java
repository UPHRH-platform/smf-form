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

	@Value("${es.fs.formsdata.index.name}")
	private String formDataIndex;

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

	@Value("${es.fs.workflowlog.index.name}")
	private String workflowLogIndex;

	@Value("${user.routes.host.url}")
	private String userServiceHost;

	@Value("${user.routes.getalluser.url}")
	private String getAllUserApiPath;

	@Value("${es.fs.activitylog.index.name}")
	private String activityLogIndex;

	@Value("${user.routes.getuserbyid.url}")
	private String getUserByIdAPI;

	@Value("${fcm.service.account.filename}")
	private String fcmFileName;

	@Value("${user.routes.getuserdevicetoken}")
	private String getUserDeviceTokenAPI;

	public String getFcmFileName() {
		return fcmFileName;
	}

	public void setFcmFileName(String fcmFileName) {
		this.fcmFileName = fcmFileName;
	}

	public String getGetUserDeviceTokenAPI() {
		return getUserDeviceTokenAPI;
	}

	public void setGetUserDeviceTokenAPI(String getUserDeviceTokenAPI) {
		this.getUserDeviceTokenAPI = getUserDeviceTokenAPI;
	}

	public String getGetUserByIdAPI() {
		return getUserByIdAPI;
	}

	public void setGetUserByIdAPI(String getUserByIdAPI) {
		this.getUserByIdAPI = getUserByIdAPI;
	}

	public String getElasticHost() {
		return elasticHost;
	}

	public void setElasticHost(String elasticHost) {
		this.elasticHost = elasticHost;
	}

	public int getElasticPort() {
		return elasticPort;
	}

	public void setElasticPort(int elasticPort) {
		this.elasticPort = elasticPort;
	}

	public String getElasticUsername() {
		return elasticUsername;
	}

	public void setElasticUsername(String elasticUsername) {
		this.elasticUsername = elasticUsername;
	}

	public String getElasticPassword() {
		return elasticPassword;
	}

	public void setElasticPassword(String elasticPassword) {
		this.elasticPassword = elasticPassword;
	}

	public String getFormIndex() {
		return formIndex;
	}

	public void setFormIndex(String formIndex) {
		this.formIndex = formIndex;
	}

	public String getFormDataIndex() {
		return formDataIndex;
	}

	public void setFormDataIndex(String formDataIndex) {
		this.formDataIndex = formDataIndex;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public String getSmtpAuth() {
		return smtpAuth;
	}

	public void setSmtpAuth(String smtpAuth) {
		this.smtpAuth = smtpAuth;
	}

	public String getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getSmtpUser() {
		return smtpUser;
	}

	public void setSmtpUser(String smtpUser) {
		this.smtpUser = smtpUser;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	public String getSmtpEmail() {
		return smtpEmail;
	}

	public void setSmtpEmail(String smtpEmail) {
		this.smtpEmail = smtpEmail;
	}

	public String getFormStateIndex() {
		return formStateIndex;
	}

	public void setFormStateIndex(String formStateIndex) {
		this.formStateIndex = formStateIndex;
	}

	public String getFormStateMatrixIndex() {
		return formStateMatrixIndex;
	}

	public void setFormStateMatrixIndex(String formStateMatrixIndex) {
		this.formStateMatrixIndex = formStateMatrixIndex;
	}

	public String getWorkflowLogIndex() {
		return workflowLogIndex;
	}

	public void setWorkflowLogIndex(String workflowLogIndex) {
		this.workflowLogIndex = workflowLogIndex;
	}

	public String getUserServiceHost() {
		return userServiceHost;
	}

	public void setUserServiceHost(String userServiceHost) {
		this.userServiceHost = userServiceHost;
	}

	public String getGetAllUserApiPath() {
		return getAllUserApiPath;
	}

	public void setGetAllUserApiPath(String getAllUserApiPath) {
		this.getAllUserApiPath = getAllUserApiPath;
	}

	public String getActivityLogIndex() {
		return activityLogIndex;
	}

	public void setActivityLogIndex(String activityLogIndex) {
		this.activityLogIndex = activityLogIndex;
	}

}
