package com.tarento.formservice.utils.NotificationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.htrace.fasterxml.jackson.core.type.TypeReference;
import org.apache.htrace.fasterxml.jackson.databind.ObjectMapper;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.tarento.formservice.model.AssignApplication;
import com.tarento.formservice.model.Assignee;
import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.model.UserInfo;
import com.tarento.formservice.model.UserProfile;
import com.tarento.formservice.models.SendMessagePrototype;
import com.tarento.formservice.models.UserDevice;
import com.tarento.formservice.repository.RestService;
import com.tarento.formservice.utils.AppConfiguration;
import com.tarento.formservice.utils.Constants;

@Service
public class NotificationUtil {

	private static ObjectMapper mapper = new ObjectMapper();

	private static final Logger logger = LoggerFactory.getLogger(NotificationUtil.class);

	private static AppConfiguration appConfig;

	@Autowired
	NotificationUtil(AppConfiguration appConfiguration) {
		appConfig = appConfiguration;
	}

	private static String templateName = "notification.vm";
	private static String body = "body";
	private static String formName = "{{formName}}";
	private static String date = "{{date}}";

	private static String returnSubject = "Application Returned";
	private static String returnBody = "The <b>" + formName
			+ "</b> application got returned from the regulator. please review your application and submit it back.";

	private static String approveSubject = "Application approved";
	private static String approveBody = "The <b>" + formName + "</b> application is approved.";

	private static String rejectSubject = "Application rejected";
	private static String rejectBody = "The <b>" + formName + "</b> application is rejected.";

	private static String sentInspectionSubject = "Application sent for inspection";
	private static String sentInspectionBody = "The <b>" + formName + "</b> application is sent for inspection.";

	private static String assignedInspectionSubject = "Application assigned for inspection";
	private static String assignedInspectionBody = "The <b>" + formName
			+ "</b> application is assigned to you for inspection which is scheduled on " + date;

	private static String inspectionCompletedSubject = "Application inspection completed";
	private static String inspectionCompletedBody = "The <b>" + formName + "</b> application inspection completed.";

	private static String leadInspCompletedSubject = "Application consent";
	private static String leadInspCompletedBody = "The lead inspection for <b>" + formName
			+ "</b> application is submitted. Please provide your consent";

	/**
	 * Handles every request workflow actions
	 * 
	 * @param applicationData
	 *            IncomingData
	 * @param action
	 *            String
	 * @param UserInfo
	 *            userInfo
	 */
	public static void SendNotification(IncomingData applicationData, String action, UserInfo userInfo) {
		try {
			VelocityContext context = new VelocityContext();
			List<String> recipient = new ArrayList<>();

			switch (action) {

			case Constants.WorkflowActions.RETURN_APPLICATION:
				recipient.add(applicationData.getCreatedBy());
				context.put(body, returnBody.replace(formName, applicationData.getTitle()));
				SendMail.sendMail(recipient.toArray(new String[recipient.size()]), returnSubject, context,
						templateName);
				break;

			case Constants.WorkflowActions.APPROVE_APPLICATION:
				recipient.add(applicationData.getCreatedBy());
				context.put(body, approveBody.replace(formName, applicationData.getTitle()));
				SendMail.sendMail(recipient.toArray(new String[recipient.size()]), approveSubject, context,
						templateName);
				break;

			case Constants.WorkflowActions.REJECT_APPLICATION:
				recipient.add(applicationData.getCreatedBy());
				context.put(body, rejectBody.replace(formName, applicationData.getTitle()));
				SendMail.sendMail(recipient.toArray(new String[recipient.size()]), rejectSubject, context,
						templateName);
				break;

			case Constants.WorkflowActions.ASSIGN_INSPECTOR:
				recipient.add(applicationData.getCreatedBy());
				context.put(body, sentInspectionBody.replace(formName, applicationData.getTitle()));
				SendMail.sendMail(recipient.toArray(new String[recipient.size()]), sentInspectionSubject, context,
						templateName);
				// Email notification for inspector
				List<String> inspectorEmail = getAssigneeEmail(applicationData.getInspection(), Boolean.TRUE,
						Boolean.TRUE);
				String messageContent = assignedInspectionBody.replace(formName, applicationData.getTitle())
						.replace("{{date}}", applicationData.getInspection().getAssignedDate());
				if (!inspectorEmail.isEmpty()) {
					String[] inspectorId = inspectorEmail.toArray(new String[inspectorEmail.size()]);
					context.put(body, messageContent);
					SendMail.sendMail(inspectorId, assignedInspectionSubject, context, templateName);
				}
				// push notification for inspector
				List<UserDevice> userDevices = getAssigneeDeviceToken(applicationData.getInspection(), userInfo,
						Boolean.TRUE, Boolean.TRUE);
				if (userDevices != null && userDevices.size() > 0) {
					SendMessagePrototype messagePrototype = new SendMessagePrototype();
					messagePrototype.setDevices(userDevices);
					messagePrototype.setMessageTitle(assignedInspectionSubject);
					messagePrototype.setMessageContent(messageContent.replace("<b>", "").replace("</b>", ""));
					PushBox.sendMessagesToDevices(messagePrototype);
				}
				break;

			case Constants.WorkflowActions.COMPLETED_INSPECTION:
				recipient.add(applicationData.getCreatedBy());
				context.put(body, inspectionCompletedBody.replace(formName, applicationData.getTitle()));
				SendMail.sendMail(recipient.toArray(new String[recipient.size()]), inspectionCompletedSubject, context,
						templateName);

				String regulatorEmail = getRegulatorEmail(applicationData.getInspection().getAssignedBy(), userInfo);
				if (StringUtils.isNotBlank(regulatorEmail)) {
					context.put(body, inspectionCompletedBody.replace(formName, applicationData.getTitle()));
					String[] regulatorId = { regulatorEmail };
					SendMail.sendMail(regulatorId, inspectionCompletedSubject, context, templateName);
				}
				break;

			case Constants.WorkflowActions.LEAD_INSPECTION_COMPLETED:
				// Email notification for inspector
				List<String> assistInspEmail = getAssigneeEmail(applicationData.getInspection(), Boolean.FALSE,
						Boolean.TRUE);
				String msgContent = leadInspCompletedBody.replace(formName, applicationData.getTitle());
				if (!assistInspEmail.isEmpty()) {
					String[] inspectorId = assistInspEmail.toArray(new String[assistInspEmail.size()]);
					context.put(body, msgContent);
					SendMail.sendMail(inspectorId, leadInspCompletedSubject, context, templateName);
				}
				// push notification for inspector
				List<UserDevice> assistInspDevices = getAssigneeDeviceToken(applicationData.getInspection(), userInfo,
						Boolean.FALSE, Boolean.TRUE);
				if (assistInspDevices != null && assistInspDevices.size() > 0) {
					SendMessagePrototype messagePrototype = new SendMessagePrototype();
					messagePrototype.setDevices(assistInspDevices);
					messagePrototype.setMessageTitle(leadInspCompletedSubject);
					messagePrototype.setMessageContent(msgContent.replace("<b>", "").replace("</b>", ""));
					PushBox.sendMessagesToDevices(messagePrototype);
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.error(String.format(Constants.EXCEPTION, "SendNotification", e.getMessage()));
		}
	}

	/**
	 * Returns the assignee's email id
	 * 
	 * @param inspection
	 *            AssignApplication
	 * @param isLead
	 *            Boolean
	 * @param isAssistant
	 *            Boolean
	 * @return List<String>
	 */
	private static List<String> getAssigneeEmail(AssignApplication inspection, Boolean isLead, Boolean isAssistant) {
		List<String> assignee = new ArrayList<>();
		if (inspection != null && inspection.getAssignedTo() != null && !inspection.getAssignedTo().isEmpty()) {
			for (Assignee user : inspection.getAssignedTo()) {
				if (user != null && StringUtils.isNotBlank(user.getEmailId())) {
					if (Boolean.TRUE.equals(isLead) && Boolean.TRUE.equals(user.getLeadInspector())) {
						assignee.add(user.getEmailId());
					}
					if (Boolean.TRUE.equals(isAssistant) && Boolean.TRUE.equals(!user.getLeadInspector())) {
						assignee.add(user.getEmailId());
					}
				}
			}
		}
		return assignee;
	}

	/**
	 * Returns the device token of the inspection assinged user
	 * 
	 * @param inspection
	 *            AssignApplication
	 * @param userinfo
	 *            UserInfo
	 * @param isLead
	 *            Boolean
	 * @param isAssistant
	 *            Boolean
	 * @return List<String>
	 */
	private static List<UserDevice> getAssigneeDeviceToken(AssignApplication inspection, UserInfo userinfo,
			Boolean isLead, Boolean isAssistant) {
		List<Long> inspectorUserId = new ArrayList<>();
		if (inspection != null) {
			if (Boolean.TRUE.equals(isLead)) {
				inspectorUserId.addAll(inspection.getLeadInspector());
			}
			if (Boolean.TRUE.equals(isAssistant)) {
				inspectorUserId.addAll(inspection.getAssistingInspector());
			}
			return getUserDeviceToken(inspectorUserId, userinfo);
		}
		return null;
	}

	/**
	 * User service call to get user info by their id
	 * 
	 * @param userId
	 *            Long
	 * @param userinfo
	 *            UserInfo
	 * @return String
	 */
	private static String getRegulatorEmail(Long userId, UserInfo userinfo) {
		try {
			if (userinfo != null) {
				String url = appConfig.getUserServiceHost() + appConfig.getGetUserByIdAPI() + "?id=" + userId;
				if (userinfo.getOrgId() != null) {
					url = url + "&orgId=" + userinfo.getOrgId();
				}
				HttpHeaders headers = new HttpHeaders();
				headers.add(Constants.Parameters.AUTHORIZATION, userinfo.getAuthToken());
				Object response = RestService.getRequestWithHeaders(headers, url);

				if (response != null) {
					Map<String, Object> userList = mapper.convertValue(response, Map.class);
					return (String) userList.get(Constants.Parameters.EMAIL_ID);
				}
			}
		} catch (Exception e) {
			logger.error(String.format(Constants.EXCEPTION, "getRegulatorEmail", e.getMessage()));
		}
		return null;
	}

	/**
	 * User service call to get the user's device token
	 * 
	 * @param userIds
	 *            List<Long>
	 * @param userinfo
	 *            UserInfo
	 * @return List<String>
	 */
	private static List<UserDevice> getUserDeviceToken(List<Long> userIds, UserInfo userinfo) {
		try {
			if (userIds != null && !userIds.isEmpty()) {
				String ids = userIds.stream().map(id -> String.valueOf(id)).collect(Collectors.joining(", "));
				String url = appConfig.getUserServiceHost() + appConfig.getGetUserDeviceTokenAPI() + "?userIds=" + ids;
				HttpHeaders headers = new HttpHeaders();
				headers.add(Constants.Parameters.AUTHORIZATION, userinfo.getAuthToken());
				Object response = RestService.getRequestWithHeaders(headers, url);
				if (response != null) {
					return mapper.convertValue(response, new TypeReference<List<UserDevice>>() {
					});
				}
			}
		} catch (Exception e) {
			logger.error(String.format(Constants.EXCEPTION, "getUserDeviceToken", e.getMessage()));
		}
		return null;
	}

}
