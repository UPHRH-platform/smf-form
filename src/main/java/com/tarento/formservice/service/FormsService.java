package com.tarento.formservice.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.tarento.formservice.model.AgentOverview;
import com.tarento.formservice.model.FormData;
import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.model.OverviewCount;
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

	public FormData addInteraction(FormData fData) throws IOException;

	public List<Form> getAllForms();

	public FormDetail getFormById(Long id);

	public List<FormData> getAllInteractions();

	public AgentOverview getAgentAggregations(Long agentId);

	public Boolean saveFormSubmit(IncomingData incomingData) throws IOException;

	public List<IncomingData> getFeedbacksByFormId(Long id, String approved, String challenged, Long agentId,
			Long customerId, UserInfo userInfo, Boolean challengeStatus);

	public List<IncomingData> getFeedbacksByFormId(Long id);

	public Boolean verifyFeedback(UserInfo userInfo, VerifyFeedbackDto verifyFeedbackDto) throws IOException;

	public Boolean voteFeedback(UserInfo userInfo, VoteFeedbackDto voteFeedbackDto) throws IOException;

	public Boolean replyFeedback(UserInfo userInfo, ReplyFeedbackDto voteFeedbackDto) throws IOException;

	public Boolean challengeFeedback(String id, String reason) throws IOException;

	boolean updateInteraction(Map<String, Object> jsonMap, String id) throws IOException;

	public List<IncomingData> getFeedbacks(String approved, String challenged, Boolean challengeStatus);

	public OverviewCount getOverviewCount(UserInfo userInfo);
	
	public Boolean requestFeedback(FormData formData) throws IOException;

	ResponseData fetchUserInfo(Long userId);

	List<FormData> getAllInteractionsForAutomatedRequestFeedback() throws Exception;

}
