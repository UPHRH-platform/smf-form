package com.tarento.formservice.utils;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tarento.formservice.executor.StateMatrixManager;
import com.tarento.formservice.model.StateMatrix;
import com.tarento.formservice.model.WorkflowDto;
import com.tarento.formservice.repository.ElasticSearchRepository;

@Component
public class WorkflowUtil {

	public static final Logger LOGGER = LoggerFactory.getLogger(WorkflowUtil.class);
	
	static AppConfiguration appConfig;

	static ElasticSearchRepository elasticsearchRepo;
	
	@Autowired
	public void setFormService(ElasticSearchRepository elasticsearchRepo) {
		WorkflowUtil.elasticsearchRepo = elasticsearchRepo;
	}
	
	@Autowired
	public void setAppConfig(AppConfiguration appConfig) {
		WorkflowUtil.appConfig = appConfig;
	}
	
	public static void getNextStateForMyRequest(WorkflowDto workflowDto) {
		StateMatrix matrix = StateMatrixManager.getStateMatrixMap().get(workflowDto.getActionStatement());
		String nextState = ""; 
		if(matrix.getRole().equals(workflowDto.getRole()) && matrix.getCurrentState().equals(workflowDto.getCurrentState())) { 
			nextState = matrix.getNextState();
			workflowDto.setNextState(nextState);
		}
		
		Runnable task1 = new Runnable() {
			@Override
			public void run() {
				try {
					updateWorkflow(workflowDto);
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
				}
			}
		};
		Thread thread1 = new Thread(task1);
		thread1.start();

	}
	
	public static Boolean updateWorkflow(WorkflowDto workflowDto) {
		return elasticsearchRepo.writeDatatoElastic(workflowDto, String.valueOf(new Date().getTime()), appConfig.getWorkflowLogIndex(),
				appConfig.getFormIndexType());
	}
}
