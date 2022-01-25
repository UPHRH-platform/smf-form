package com.tarento.formservice.utils;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tarento.formservice.service.FormsService;
import com.tarento.formservice.service.impl.FormsServiceImpl;

public class InteractionRequestFeedbackJob implements Job {
	public static final Logger LOGGER = LoggerFactory.getLogger(FormsServiceImpl.class);
	@Autowired
	private FormsService formsService;

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			formsService.getAllInteractionsForAutomatedRequestFeedback();
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an error while running the job:  %s", e.getMessage()));
		}
	}

}
