package com.tarento.formservice.utils.NotificationService;

import java.io.File;
import java.io.FileInputStream;

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

	private final Logger LOGGER = LoggerFactory.getLogger(PushBox.class);

	AppConfiguration appConfig;

	@Autowired
	private PushBox(AppConfiguration appConfiguration) {
		appConfig = appConfiguration;
		initialize();
	}

	private void initialize() {
		try {
			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials
							.fromStream(new ClassPathResource(appConfig.getFcmFileName()).getInputStream()))
					.setDatabaseUrl("https://up-smf-47fd3-default-rtdb.asia-southeast1.firebasedatabase.app").build();
			FirebaseApp.initializeApp(options);
			LOGGER.info("##PushBoxFox## : Firebase App Initialized");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(String.format(Constants.EXCEPTION, "initialize", e.getMessage()));
		}
	}

	public void sendMessagesToDevices(SendMessagePrototype messagePrototype) {
		for (UserDevice userDevice : messagePrototype.getDevices())
			try {
				Notification newNotification = Notification.builder().setTitle(messagePrototype.getMessageKey())
						.setBody(messagePrototype.getMessage().getMessageTitle()).build();
				Message message = Message.builder().putData("content", messagePrototype.getMessage().getMessageBody())
						.setToken(userDevice.getUserDeviceToken()).setNotification(newNotification).build();
				String response = FirebaseMessaging.getInstance().sendAsync(message).get();
				LOGGER.info("##PushBox## : Message Send Status : " + response);
			} catch (Exception ex) {
				LOGGER.error("##PushBox## : Error : Encountered an exception while sending the message : "
						+ ex.getMessage());
			}
	}

}
