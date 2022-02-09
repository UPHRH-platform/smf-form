package com.tarento.formservice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tarento.formservice.executor.StateMatrixManager;
import com.tarento.formservice.model.StateMatrix;
import com.tarento.formservice.model.WorkflowDto;
import com.tarento.formservice.repository.ElasticSearchRepository;

public class WorkflowUtil {

	public static final Logger LOGGER = LoggerFactory.getLogger(WorkflowUtil.class);
	
	static AppConfiguration appConfig;

	static ElasticSearchRepository elasticsearchRepo;
	
	@Autowired
	public void setElasticSearchRepo(ElasticSearchRepository elasticsearchRepo) {
		this.elasticsearchRepo = elasticsearchRepo;
	}

	@Autowired
	public void setAppConfig(AppConfiguration appConfig) {
		this.appConfig = appConfig;
	}

	
	public static Boolean updateWorkflow(WorkflowDto workflowDto) {
		return elasticsearchRepo.writeDatatoElastic(workflowDto, workflowDto.getChangedDate().toString(), appConfig.getWorkflowLogIndex(),
				appConfig.getFormIndexType());
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
}
