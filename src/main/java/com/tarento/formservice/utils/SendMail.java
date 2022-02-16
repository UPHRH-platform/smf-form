package com.tarento.formservice.utils;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * this api is used to sending mail.
 *
 * @author Manzarul.Haque
 *
 */
@Service
public class SendMail {
	private static final String TEXT_HTML = "text/html";
	private static final String EMAILS = "emails/";
	public static final Logger LOGGER = LoggerFactory.getLogger(SendMail.class);
	private static final String CLASSNAME = SendMail.class.getName();
	private static Properties props = null;

	private static AppConfiguration appConfig;

	@Autowired
	private SendMail(AppConfiguration appConfiguration) {
		appConfig = appConfiguration;

		props = System.getProperties();
		props.put("mail.smtp.host", appConfig.getSmtpHost());
		props.put("mail.smtp.socketFactory.port", appConfig.getSmtpPort());
		props.put("mail.smtp.auth", appConfig.getSmtpAuth());
		props.put("mail.smtp.port", appConfig.getSmtpPort());
	}

	/**
	 * this method is used to send email.
	 *
	 * @param receipent
	 *            email to whom we send mail
	 * @param context
	 *            VelocityContext
	 * @param templateName
	 *            String
	 * @param subject
	 *            subject
	 */
	@Async
	public static void sendMail(String[] recipient, String subject, VelocityContext context, String templateName) {
		try {
			Session session = Session.getInstance(props,
					new GMailAuthenticator(appConfig.getSmtpUser(), appConfig.getSmtpPassword()));
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(appConfig.getSmtpEmail()));
			int size = recipient.length;
			int i = 0;
			while (size > 0) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient[i]));
				i++;
				size--;
			}
			message.setSubject(subject);
			VelocityEngine engine = new VelocityEngine();
			engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			engine.init();
			Template template = engine.getTemplate(templateName);
			StringWriter writer = new StringWriter();
			template.merge(context, writer);
			message.setContent(writer.toString(), TEXT_HTML);
			Transport transport = session.getTransport("smtp");
			transport.connect(appConfig.getSmtpHost(), appConfig.getSmtpUser(), appConfig.getSmtpPassword());
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			LOGGER.error(e.toString(), CLASSNAME);
		}
	}

	/**
	 * this method is used to send email along with CC Recipients list.
	 *
	 * @param receipent
	 *            email to whom we send mail
	 * @param context
	 *            VelocityContext
	 * @param templateName
	 *            String
	 * @param subject
	 *            subject
	 * @param ccList
	 *            String
	 */
	@Async
	public static void sendMail(String[] receipent, String subject, VelocityContext context, String templateName,
			String[] ccList) {
		try {
			Session session = Session.getInstance(props,
					new GMailAuthenticator(appConfig.getSmtpUser(), appConfig.getSmtpPassword()));
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(appConfig.getSmtpEmail()));
			int size = receipent.length;
			int i = 0;
			while (size > 0) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(receipent[i]));
				i++;
				size--;
			}
			size = ccList.length;
			i = 0;
			while (size > 0) {
				message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccList[i]));
				i++;
				size--;
			}
			message.setSubject(subject);
			VelocityEngine engine = new VelocityEngine();
			engine.init();
			String templatePath = EMAILS;
			Template template = engine.getTemplate(templateName);
			StringWriter writer = new StringWriter();
			template.merge(context, writer);
			message.setContent(writer.toString(), TEXT_HTML);
			Transport transport = session.getTransport("smtp");
			transport.connect(appConfig.getSmtpHost(), appConfig.getSmtpUser(), appConfig.getSmtpPassword());
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "sendMail", e.getMessage()));
		}
	}

	/**
	 * this method is used to send email as an attachment.
	 *
	 * @param receipent
	 *            email to whom we send mail
	 * @param mail
	 *            mail body.
	 * @param subject
	 *            subject
	 * @param filePath
	 *            String
	 */
	@Async
	public static void sendAttachment(String[] receipent, String mail, String subject, String filePath) {
		try {
			Session session = Session.getInstance(props,
					new GMailAuthenticator(appConfig.getSmtpUser(), appConfig.getSmtpPassword()));
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(appConfig.getSmtpEmail()));
			int size = receipent.length;
			int i = 0;
			while (size > 0) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(receipent[i]));
				i++;
				size--;
			}
			message.setSubject(subject);
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(mail, TEXT_HTML);

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			DataSource source = new FileDataSource(filePath);
			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(filePath);
			multipart.addBodyPart(messageBodyPart);
			message.setSubject(subject);
			message.setContent(multipart);
			Transport transport = session.getTransport("smtp");
			transport.connect(appConfig.getSmtpHost(), appConfig.getSmtpUser(), appConfig.getSmtpPassword());
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.EXCEPTION, "sendAttachment", e.getMessage()));
		}
	}

	public static void sendMail(final Map<String, String> keyValue, final String[] emails, final String subject,
			final String vmFileName) {
		ExecutorManager.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				VelocityContext context = new VelocityContext();
				for (Map.Entry<String, String> entry : keyValue.entrySet()) {
					context.put(entry.getKey(), entry.getValue());
				}

				context.put(JsonKey.LOGO_URL, Constants.LOGO_URL);
				sendMail(emails, subject, context, vmFileName);
			}

		});
	}
}
