package com.tarento.formservice.model;

import java.util.List;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "username", "password", "emailId", "phoneNo", "orgId", "countryCode", "timeZone",
		"avatarUrl", "isActive", "isDeleted", "profileId", "firstName", "lastName", "age", "dob", "gender", "startDate",
		"endDate", "salary", "country", "registrationDate", "employmentType", "createdDate", "createdBy", "updatedDate",
		"updatedBy", "roles", "countryId", "authToken" })
@Generated("jsonschema2pojo")
public class ResponseData {

	@JsonProperty("id")
	private Long id;
	@JsonProperty("username")
	private String username;
	@JsonProperty("password")
	private Object password;
	@JsonProperty("emailId")
	private String emailId;
	@JsonProperty("phoneNo")
	private String phoneNo;
	@JsonProperty("orgId")
	private Object orgId;
	@JsonProperty("countryCode")
	private Object countryCode;
	@JsonProperty("timeZone")
	private String timeZone;
	@JsonProperty("avatarUrl")
	private Object avatarUrl;
	@JsonProperty("isActive")
	private Boolean isActive;
	@JsonProperty("isDeleted")
	private Boolean isDeleted;
	@JsonProperty("profileId")
	private Object profileId;
	@JsonProperty("firstName")
	private String firstName;
	@JsonProperty("lastName")
	private String lastName;
	@JsonProperty("age")
	private Long age;
	@JsonProperty("dob")
	private Object dob;
	@JsonProperty("gender")
	private Object gender;
	@JsonProperty("startDate")
	private Object startDate;
	@JsonProperty("endDate")
	private Object endDate;
	@JsonProperty("salary")
	private Object salary;
	@JsonProperty("country")
	private Object country;
	@JsonProperty("registrationDate")
	private Object registrationDate;
	@JsonProperty("employmentType")
	private Object employmentType;
	@JsonProperty("createdDate")
	private Object createdDate;
	@JsonProperty("createdBy")
	private Long createdBy;
	@JsonProperty("updatedDate")
	private Object updatedDate;
	@JsonProperty("updatedBy")
	private Long updatedBy;
	@JsonProperty("roles")
	private List<Role> roles = null;
	@JsonProperty("countryId")
	private Object countryId;
	@JsonProperty("authToken")
	private Object authToken;

	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(Long id) {
		this.id = id;
	}

	@JsonProperty("username")
	public String getUsername() {
		return username;
	}

	@JsonProperty("username")
	public void setUsername(String username) {
		this.username = username;
	}

	@JsonProperty("password")
	public Object getPassword() {
		return password;
	}

	@JsonProperty("password")
	public void setPassword(Object password) {
		this.password = password;
	}

	@JsonProperty("emailId")
	public String getEmailId() {
		return emailId;
	}

	@JsonProperty("emailId")
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	@JsonProperty("phoneNo")
	public String getPhoneNo() {
		return phoneNo;
	}

	@JsonProperty("phoneNo")
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	@JsonProperty("orgId")
	public Object getOrgId() {
		return orgId;
	}

	@JsonProperty("orgId")
	public void setOrgId(Object orgId) {
		this.orgId = orgId;
	}

	@JsonProperty("countryCode")
	public Object getCountryCode() {
		return countryCode;
	}

	@JsonProperty("countryCode")
	public void setCountryCode(Object countryCode) {
		this.countryCode = countryCode;
	}

	@JsonProperty("timeZone")
	public String getTimeZone() {
		return timeZone;
	}

	@JsonProperty("timeZone")
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	@JsonProperty("avatarUrl")
	public Object getAvatarUrl() {
		return avatarUrl;
	}

	@JsonProperty("avatarUrl")
	public void setAvatarUrl(Object avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	@JsonProperty("isActive")
	public Boolean getIsActive() {
		return isActive;
	}

	@JsonProperty("isActive")
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@JsonProperty("isDeleted")
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	@JsonProperty("isDeleted")
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@JsonProperty("profileId")
	public Object getProfileId() {
		return profileId;
	}

	@JsonProperty("profileId")
	public void setProfileId(Object profileId) {
		this.profileId = profileId;
	}

	@JsonProperty("firstName")
	public String getFirstName() {
		return firstName;
	}

	@JsonProperty("firstName")
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@JsonProperty("lastName")
	public String getLastName() {
		return lastName;
	}

	@JsonProperty("lastName")
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@JsonProperty("age")
	public Long getAge() {
		return age;
	}

	@JsonProperty("age")
	public void setAge(Long age) {
		this.age = age;
	}

	@JsonProperty("dob")
	public Object getDob() {
		return dob;
	}

	@JsonProperty("dob")
	public void setDob(Object dob) {
		this.dob = dob;
	}

	@JsonProperty("gender")
	public Object getGender() {
		return gender;
	}

	@JsonProperty("gender")
	public void setGender(Object gender) {
		this.gender = gender;
	}

	@JsonProperty("startDate")
	public Object getStartDate() {
		return startDate;
	}

	@JsonProperty("startDate")
	public void setStartDate(Object startDate) {
		this.startDate = startDate;
	}

	@JsonProperty("endDate")
	public Object getEndDate() {
		return endDate;
	}

	@JsonProperty("endDate")
	public void setEndDate(Object endDate) {
		this.endDate = endDate;
	}

	@JsonProperty("salary")
	public Object getSalary() {
		return salary;
	}

	@JsonProperty("salary")
	public void setSalary(Object salary) {
		this.salary = salary;
	}

	@JsonProperty("country")
	public Object getCountry() {
		return country;
	}

	@JsonProperty("country")
	public void setCountry(Object country) {
		this.country = country;
	}

	@JsonProperty("registrationDate")
	public Object getRegistrationDate() {
		return registrationDate;
	}

	@JsonProperty("registrationDate")
	public void setRegistrationDate(Object registrationDate) {
		this.registrationDate = registrationDate;
	}

	@JsonProperty("employmentType")
	public Object getEmploymentType() {
		return employmentType;
	}

	@JsonProperty("employmentType")
	public void setEmploymentType(Object employmentType) {
		this.employmentType = employmentType;
	}

	@JsonProperty("createdDate")
	public Object getCreatedDate() {
		return createdDate;
	}

	@JsonProperty("createdDate")
	public void setCreatedDate(Object createdDate) {
		this.createdDate = createdDate;
	}

	@JsonProperty("createdBy")
	public Long getCreatedBy() {
		return createdBy;
	}

	@JsonProperty("createdBy")
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	@JsonProperty("updatedDate")
	public Object getUpdatedDate() {
		return updatedDate;
	}

	@JsonProperty("updatedDate")
	public void setUpdatedDate(Object updatedDate) {
		this.updatedDate = updatedDate;
	}

	@JsonProperty("updatedBy")
	public Long getUpdatedBy() {
		return updatedBy;
	}

	@JsonProperty("updatedBy")
	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	@JsonProperty("roles")
	public List<Role> getRoles() {
		return roles;
	}

	@JsonProperty("roles")
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	@JsonProperty("countryId")
	public Object getCountryId() {
		return countryId;
	}

	@JsonProperty("countryId")
	public void setCountryId(Object countryId) {
		this.countryId = countryId;
	}

	@JsonProperty("authToken")
	public Object getAuthToken() {
		return authToken;
	}

	@JsonProperty("authToken")
	public void setAuthToken(Object authToken) {
		this.authToken = authToken;
	}

}