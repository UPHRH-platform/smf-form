package com.tarento.formservice.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.htrace.fasterxml.jackson.databind.ObjectMapper;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tarento.formservice.model.AssignApplication;
import com.tarento.formservice.model.IncomingData;
import com.tarento.formservice.model.UserProfile;
import com.tarento.formservice.repository.RestService;

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

	/**
	 * Handles every request workflow actions
	 * 
	 * @param applicationMap
	 *            Map<String, Object>
	 * @param action
	 *            String
	 */
	public static void SendNotification(Map<String, Object> applicationMap, String action) {
		new Thread(() -> {
			try {
				IncomingData applicationData = mapper.convertValue(applicationMap, IncomingData.class);

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

					List<String> inspectorEmail = getAssigneeEmail(applicationData.getInspection());
					if (!inspectorEmail.isEmpty()) {
						String[] inspectorId = inspectorEmail.toArray(new String[inspectorEmail.size()]);
						context.put(body, assignedInspectionBody.replace(formName, applicationData.getTitle())
								.replace("{{date}}", applicationData.getInspection().getAssignedDate()));
						SendMail.sendMail(inspectorId, assignedInspectionSubject, context, templateName);
					}
					break;

				case Constants.WorkflowActions.COMPLETED_INSPECTION:
					recipient.add(applicationData.getCreatedBy());
					context.put(body, inspectionCompletedBody.replace(formName, applicationData.getTitle()));
					SendMail.sendMail(recipient.toArray(new String[recipient.size()]), inspectionCompletedSubject,
							context, templateName);

					String regulatorEmail = getRegulatorEmail(applicationData.getInspection().getAssignedBy());
					if (StringUtils.isNotBlank(regulatorEmail)) {
						context.put(body, inspectionCompletedBody.replace(formName, applicationData.getTitle()));
						String[] regulatorId = { regulatorEmail };
						SendMail.sendMail(regulatorId, inspectionCompletedSubject, context, templateName);

					}
					break;

				default:
					break;
				}
			} catch (Exception e) {
				logger.error(String.format(Constants.EXCEPTION, "SendNotification", e.getMessage()));
			}
		}).start();

	}

	/**
	 * Returns the assignee's email id
	 * 
	 * @param inspection
	 *            AssignApplication
	 * @return List<String>
	 */
	private static List<String> getAssigneeEmail(AssignApplication inspection) {
		List<String> assignee = new ArrayList<>();
		if (inspection != null && inspection.getAssignedTo() != null && !inspection.getAssignedTo().isEmpty()) {
			for (UserProfile user : inspection.getAssignedTo()) {
				if (user != null && StringUtils.isNotBlank(user.getEmailId())) {
					assignee.add(user.getEmailId());
				}
			}
		}
		return assignee;
	}

	private static String getRegulatorEmail(Long userId) {
		try {
			String url = appConfig.getUserServiceHost() + appConfig.getGetUserByIdAPI();
			Object response = RestService.getRequest(url);

			if (response != null) {
				Map<String, Object> userList = mapper.convertValue(response, Map.class);
				return (String) userList.get(Constants.Parameters.EMAIL_ID);
			}
		} catch (Exception e) {
			logger.error(String.format(Constants.EXCEPTION, "getRegulatorEmail", e.getMessage()));
		}
		return null;
	}

}
