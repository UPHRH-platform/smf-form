package com.tarento.formservice.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
@Entity
@Table(name = "institute_courses")
public class InstituteCourses {
	

	@Id @GeneratedValue
	@Column(name = "id")
	private Long id;
	@Column(name = "district_name")
	private String districtName;
	@Column(name = "center_code")
	private String centerCode;
	@Column(name = "degree")
	private String degree;
	@Column(name = "course")
	private String course;
	@Column(name = "applied_year")
	private String appliedYear;
	@Column(name = "sector")
	private String sector;
	@Column(name = "profile_id")
	private Long profileId; //Fk UserProfile
	@Column(name = "created_date")
	private Date createdDate;
	@Column(name = "created_by")
	private Long createdBy;
	@Column(name = "updated_date")
	private Date updatedDate;
	@Column(name = "updated_by")
	private Long updatedBy;
	
}
