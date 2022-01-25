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
@JsonInclude(JsonInclude.Include.ALWAYS)
public class FormData {
	private String eid;
	private Long id;
	private Long customer;
	private String customerName;
	private String customerEmail;
	private Long agent;
	private String agentName; 
	private Long interactionDate;
	private Long linkSentDate;
	private String urlCode;
	private String formData;
}
