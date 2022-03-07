package com.tarento.formservice.model;

import java.util.List;

/**
 * @author Darshan Nagesh
 *
 */
public class SearchRequestDto {
	private List<SearchObject> searchObjects;
	private List<SearchObject> excludeObject;
	private List<SearchObject> filterObjects;

	public List<SearchObject> getSearchObjects() {
		return searchObjects;
	}

	public void setSearchObjects(List<SearchObject> searchObjects) {
		this.searchObjects = searchObjects;
	}

	public List<SearchObject> getExcludeObject() {
		return excludeObject;
	}

	public void setExcludeObject(List<SearchObject> excludeObject) {
		this.excludeObject = excludeObject;
	}

	public List<SearchObject> getFilterObjects() {
		return filterObjects;
	}

	public void setFilterObjects(List<SearchObject> filterObjects) {
		this.filterObjects = filterObjects;
	}

}
