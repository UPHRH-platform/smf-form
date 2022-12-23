package com.tarento.formservice.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sunbird.cloud.storage.BaseStorageService;
import org.sunbird.cloud.storage.factory.StorageConfig;
import org.sunbird.cloud.storage.factory.StorageServiceFactory;

import scala.Option;

@Service
public class CloudStorage {

	public static final Logger LOGGER = LoggerFactory.getLogger(CloudStorage.class);
	private static final String EXCEPTION = "Exception in %s: %s";

	private static BaseStorageService storageService = null;

	private static String provider;
	private static String containerName;
	private static String identity;
	private static String credential;

	private static AppConfiguration appConfiguration;

	@Autowired
	private CloudStorage(AppConfiguration appConfig) {
		appConfiguration = appConfig;

		provider = appConfiguration.getProvider();
		containerName = appConfiguration.getContainerName();
		identity = appConfiguration.getIdentity();
		credential = appConfiguration.getCredential();

		storageService = StorageServiceFactory
				.getStorageService(new StorageConfig(provider, identity, credential, null));
	}

	public static Map<String, String> uploadFile(String folderName, File file) {
		try {
			String objectKey = DateUtils.getCurrentTimestamp() + "_" + file.getName();
			if (StringUtils.isNotBlank(folderName)) {
				objectKey = folderName + "/" + objectKey;
			}
			String url = storageService.upload(containerName, file.getAbsolutePath(), objectKey, Option.apply(false),
					Option.apply(1), Option.apply(5), Option.empty());
			LOGGER.info(file.getName() + " uploaded successfully");

			Map<String, String> uploadedFile = new HashMap<>();
			uploadedFile.put(Constants.NAME, objectKey);
			uploadedFile.put(Constants.URL, url);
			return uploadedFile;
		} catch (Exception e) {
			LOGGER.error(String.format(EXCEPTION, "uploadFile", e.getMessage()));
			return null;
		}

	}

	public static String getSignedURL(String fileName) {
		try {
			return storageService.getSignedURL(containerName, fileName, Option.empty(), Option.empty());

		} catch (Exception e) {
			LOGGER.error(String.format(EXCEPTION, "getSignedURL", e.getMessage()));
			return null;
		}
	}

	public static Boolean deleteFile(String fileName) {
		try {
			storageService.deleteObject(containerName, fileName, Option.apply(Boolean.FALSE));
			return Boolean.TRUE;
		} catch (Exception e) {
			LOGGER.error(String.format(EXCEPTION, "deleteFile", e.getMessage()));
			return Boolean.FALSE;
		}
	}

}
