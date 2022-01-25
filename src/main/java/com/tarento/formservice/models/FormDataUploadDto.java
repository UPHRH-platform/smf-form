package com.tarento.formservice.models;

import org.springframework.web.multipart.MultipartFile;

public class FormDataUploadDto {
	
	private Long id; 
	private int version; 
	private MultipartFile file;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	} 
	
	

}
