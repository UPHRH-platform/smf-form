package com.tarento.formservice.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.model.ReplyFeedbackDto;
import com.tarento.formservice.model.ResponseData;
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

	public List<IncomingData> getFeedbacksByFormId(Long id, String approved, String challenged, Long agentId,
			Long customerId, UserInfo userInfo, Boolean challengeStatus);

	public List<IncomingData> getFeedbacksByFormId(Long id);

	public Boolean verifyFeedback(UserInfo userInfo, VerifyFeedbackDto verifyFeedbackDto) throws IOException;

	public Boolean voteFeedback(UserInfo userInfo, VoteFeedbackDto voteFeedbackDto) throws IOException;

	public Boolean replyFeedback(UserInfo userInfo, ReplyFeedbackDto voteFeedbackDto) throws IOException;

	public List<IncomingData> getFeedbacks(String approved, String challenged, Boolean challengeStatus);

	ResponseData fetchUserInfo(Long userId);

	public Boolean saveFormSubmit(IncomingData incomingData, Map<String, MultipartFile> multipartFiles);

	List<IncomingData> getApplications(String formId, String applicationId);

}
