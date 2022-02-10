package com.tarento.formservice.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class UserProfile extends User {
	private Long profileId;
	private String firstName;
	private String lastName;
	private int age;
	private String dob;
	private String gender;
	private Date startDate;
	private Date endDate;
	private Long salary;
	private String country;
	private Date registrationDate;
	private String employmentType;
	private Date createdDate;
	private Long createdBy;
	private Date updatedDate;
	private Long updatedBy;
	private List<Role> roles;
	private Long countryId;
}
