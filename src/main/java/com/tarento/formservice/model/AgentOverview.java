package com.tarento.formservice.model;

import java.util.Map;

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
@JsonInclude(JsonInclude.Include.ALWAYS)
public class AgentOverview {
	private Double averageRating;
	private Long totalRating; 
	private Map<Integer, Double> ratingSplit; 
	private Map<String, Double> featureListing; 
}
