package com.tarento.formservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class InstituteFormDataDto {

	private String districtCode;
	private String centerCode;
	private String instituteName;
	private String degree;
	private String course;
	//private String nursingCouseOffered;
	//private String paramedicalCourseOffered;
	private String formsSavedAsDraft;
	private String formsSubmitted;
	private String formsSubmittedTimestamp;
}
