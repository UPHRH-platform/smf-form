package com.tarento.formservice.utils;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tarento.formservice.service.impl.FormsServiceImpl;

public class AnalyticsSchedulerManager {
	public static final Logger LOGGER = LoggerFactory.getLogger(FormsServiceImpl.class);

	/**
	 * SCHEDULER job is configured here
	 */
	public static void schedule() {
		JobDetail dailyJob = JobBuilder.newJob(InteractionRequestFeedbackJob.class)
				.withIdentity("DailyInteractionTime", "DailyInteractionData").build();
		Trigger dailyTrigger = TriggerBuilder.newTrigger().withIdentity("InteractionTimeDaily", "InteractionDataDaily")
				// this SCHEDULER will run every day at 5:30 PM in UTC .
				.withSchedule(CronScheduleBuilder.cronSchedule("0 0 3 ? * MON-SUN")).build();
		// 0 30 17 1/1 * ? * ->> 5:30 everyday
		// 0 0/2 * 1/1 * ? * ->> Every 2 mins
		// 0 30 17 ? * FRI,SUN * ->> Weekly Mail
		// 0 30 17 ? * MON-FRI ->> Monday to Friday at 5:30
		try {
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(dailyJob, dailyTrigger);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an error while running the scheduler:  %s", e.getMessage()));
		}
	}

	private AnalyticsSchedulerManager() {
		super();

	}
}
