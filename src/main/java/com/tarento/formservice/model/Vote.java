package com.tarento.formservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class Vote {
	private Long customerId; 
	private Long voteDate;
	private String vote;
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public Long getVoteDate() {
		return voteDate;
	}
	public void setVoteDate(Long voteDate) {
		this.voteDate = voteDate;
	}
	public String getVote() {
		return vote;
	}
	public void setVote(String vote) {
		this.vote = vote;
	} 
	
	
}
