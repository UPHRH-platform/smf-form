package com.tarento.formservice.utils.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.tarento.formservice.models.SendMessagePrototype;
import com.tarento.formservice.models.UserDevice;
import com.tarento.formservice.utils.AppConfiguration;
import com.tarento.formservice.utils.Constants;

@Service
public class PushBox {

	private final static Logger LOGGER = LoggerFactory.getLogger(PushBox.class);

	AppConfiguration appConfig;

	@Autowired
	private PushBox(AppConfiguration appConfiguration) {
		appConfig = appConfiguration;
		initialize();
	}

	private void initialize() {
		try {
			FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(
					GoogleCredentials.fromStream(new ClassPathResource(appConfig.getFcmFileName()).getInputStream()))
					.build();
			FirebaseApp.initializeApp(options);
			LOGGER.info("##PushBoxFox## : Firebase App Initialized");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(String.format(Constants.EXCEPTION, "initialize", e.getMessage()));
		}
	}

	public static void sendMessagesToDevices(SendMessagePrototype messagePrototype) {
		for (UserDevice userDevice : messagePrototype.getDevices()) {
			try {
				Notification newNotification = new Notification(messagePrototype.getMessageTitle(),
						messagePrototype.getMessageContent());
				Message message = Message.builder().setToken(userDevice.getDeviceToken())
						.setNotification(newNotification).build();
				String response = FirebaseMessaging.getInstance().sendAsync(message).get();
				LOGGER.info("##PushBox## : Message Send Status : " + response);
			} catch (Exception ex) {
				LOGGER.error("##PushBox## : Error : Encountered an exception while sending the message : "
						+ ex.getMessage());
			}
		}
	}

}
