/**
 * 
 */
package com.tarento.formservice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class FileConfigApplicationRunner implements ApplicationRunner {

	public static final Logger LOGGER = LoggerFactory.getLogger(FileConfigApplicationRunner.class);

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try {

		} catch (Exception e) {
			LOGGER.error("Exception while loading yaml files: ", e);
		}
	}

}
