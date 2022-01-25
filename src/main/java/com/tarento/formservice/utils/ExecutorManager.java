package com.tarento.formservice.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import lombok.NoArgsConstructor;

/**
 * This class will manage execute service thread.
 *
 * @author Manzarul.Haque
 *
 */
@NoArgsConstructor
public class ExecutorManager {
	/*
	 * service ScheduledExecutorService object
	 */
	private static ScheduledExecutorService service = null;
	static {
		service = Executors.newScheduledThreadPool(Constants.MAX_EXECUTOR_THREAD);
	}

	/**
	 * This method will send executor service object.
	 *
	 * @return
	 */
	public static ScheduledExecutorService getExecutorService() {
		return service;
	}
}
