package com.tarento.formservice.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.springframework.web.multipart.MultipartFile;

import com.tarento.formservice.model.AssignApplication;
import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.model.KeyValueList;
import com.tarento.formservice.model.ReplyFeedbackDto;
import com.tarento.formservice.model.ResponseData;
import com.tarento.formservice.model.SearchRequestDto;
import com.tarento.formservice.model.State;
import com.tarento.formservice.model.StateMatrix;
import com.tarento.formservice.model.UserInfo;
import com.tarento.formservice.model.VerifyFeedbackDto;
import com.tarento.formservice.model.VoteFeedbackDto;
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

	public List<Form> getAllForms();

	public FormDetail getFormById(Long id);

	public Boolean saveFormSubmit(IncomingData incomingData) throws IOException;

	public List<Map<String, Object>> getFeedbacksByFormId(Long id, String approved, String challenged, Long agentId,
			Long customerId, UserInfo userInfo, Boolean challengeStatus);

	public List<Map<String, Object>> getFeedbacksByFormId(Long id);

	public Boolean verifyFeedback(UserInfo userInfo, VerifyFeedbackDto verifyFeedbackDto) throws IOException;

	public Boolean voteFeedback(UserInfo userInfo, VoteFeedbackDto voteFeedbackDto) throws IOException;

	public Boolean replyFeedback(UserInfo userInfo, ReplyFeedbackDto voteFeedbackDto) throws IOException;

	public List<Map<String, Object>> getFeedbacks(String approved, String challenged, Boolean challengeStatus);

	ResponseData fetchUserInfo(Long userId);

	List<Map<String, Object>> getApplications(UserInfo userInfo, SearchRequestDto searchRequestDto);

	KeyValueList getApplicationsStatusCount();

	public Boolean saveFormSubmitv1(IncomingData incomingData, UserInfo userInfo);

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

}
