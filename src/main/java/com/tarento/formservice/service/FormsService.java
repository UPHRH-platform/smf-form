package com.tarento.formservice.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.tarento.formservice.model.*;
import org.springframework.web.multipart.MultipartFile;

import com.tarento.formservice.models.Form;
import com.tarento.formservice.models.FormDetail;

/**
 * Interface for all the Form Service APIs
 * 
 * @author Darshan Nagesh
 *
 */
public interface FormsService {

	public Form createForm(FormDetail newForm) throws IOException;

	public List<Form> getAllForms(UserInfo userInfo, Boolean isDetail);

	public FormDetail getFormById(Long id);

	public Boolean saveFormSubmit(IncomingData incomingData) throws IOException;

	ResponseData fetchUserInfo(Long userId);

	List<Map<String, Object>> getApplications(UserInfo userInfo, SearchRequestDto searchRequestDto);
	
	List<Map<String, Object>> getPlainFormsById(String id);
	
	List<Map<String, Object>> getAllPlainForms();

	String getInstitutesData(UserInfo userInfo, InstituteDownloadRequestDto instituteDownloadRequestDto);

	KeyValueList getApplicationsStatusCount(UserInfo userInfo);

	public Boolean saveFormSubmitv1(IncomingData incomingData, UserInfo userInfo, String action);
	
	public Boolean savePlainForm(IncomingData incomingData);

	public String fileUpload(MultipartFile multipartFile, String folderName);

	public Boolean deleteCloudFile(List<String> files);

	public Boolean reviewApplication(IncomingData incomingData, UserInfo userInfo);

	public ConcurrentMap<Long, State> fetchAllStates();

	public ConcurrentMap<String, List<StateMatrix>> fetchAllStateMatrix();

	Boolean assignApplication(UserInfo userinfo, AssignApplication assign);

	Map<String, Object> getApplicationById(String applicationId, UserInfo userInfo);

	public Boolean updateApplicationStatus(IncomingData incomingData, UserInfo userInfo, String operation);

	public Boolean submitInspection(IncomingData incomingData, UserInfo userInfo);

	public List<Map<String, Object>> getActivityLogs(String applicationId);

	public Boolean consentApplication(Consent consent, UserInfo userInfo);

	public void submitBulkInspection(List<IncomingData> inspectionDataList, UserInfo userInfo);

	public void consentBulkApplication(List<Consent> consentList, UserInfo userInfo);

}
