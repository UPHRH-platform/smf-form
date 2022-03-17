package com.tarento.formservice.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Darshan Nagesh
 *
 */
@Getter
@Setter
public class SearchRequestDto {

	private List<SearchObject> searchObjects;
	private List<SearchObject> excludeObject;
	private List<SearchObject> filterObjects;

}
