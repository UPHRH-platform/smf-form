package com.tarento.formservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(includeFieldNames = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverviewCount {

	private int pendingApproval;
	private int approvalSum;
	private int pendingChallenge;
	private int challengeSum;
	
	private int reviewsReceived; 
	private double averageRating;
	private int reviewsChallenged;
	private int customersInteracted; 
}
