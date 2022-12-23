package com.tarento.formservice.models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This model contains the User Profile Information for a User
 * 
 * @author Darshan Nagesh
 *
 */

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_profile")
public class UserProfile  {
	
	
	
	@Id @GeneratedValue
	@Column(name = "id")
	private Long profileId;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "age")
	private int age;
	
	@Column(name = "phone_number")
	private String phoneNo;
	
	@Column(name = "dob")
	private String dob;
	
	@Column(name = "gender")
	private String gender;
	
	@Column(name = "avatar_url")
	private String avatarUrl;
	
	@Column(name = "user_profilecol")
	private String userProfilecol;
	
	@Column(name = "work_start_date")
	private Date startDate;
	
	@Column(name = "work_end_date")
	private Date endDate;
	
	@Column(name = "salary")
	private Long salary;
	
	@Column(name = "country")
	private String country;
	
	@Column(name = "registration_date")
	private Date registrationDate;
	
	@Column(name = "employment_type")
	private String employmentType;
	
	@Column(name = "created_date")
	private Date createdDate;
	@Column(name = "created_by")
	private Long createdBy;
	@Column(name = "updated_date")
	private Date updatedDate;
	@Column(name = "updated_by")
	private Long updatedBy;
	
	

}
