package com.tarento.formservice.model;

import java.util.Date;
import java.util.List;

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
public class UserProfile extends User {
	private Long profileId;
	private String firstName;
	private String lastName;
	private int age;
	private String dob;
	private String gender;
	private String avatarUrl;
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
	private String countryCode;
	private Long countryId;
}
