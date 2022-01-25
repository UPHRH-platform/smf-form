
package com.tarento.formservice.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.ToString;

@ToString(includeFieldNames = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "chartType", "subChartType", "visualizationCode", "chartFormat", "drillDownChartId", "filterKeys",
		"customData", "dates", "filter", "data", "title", "description", "image" })
public class Chart {
	@JsonProperty("id")
	private Long id;
	@JsonProperty("chartType")
	private String chartType;
	@JsonProperty("subChartType")
	private String subChartType;
	@JsonProperty("visualizationCode")
	private String visualizationCode;
	@JsonProperty("chartFormat")
	private String chartFormat;
	@JsonProperty("drillDownChartId")
	private String drillDownChartId;
	@JsonProperty("filterKeys")
	private String filterKeys;
	@JsonProperty("customData")
	private String customData;
	@JsonProperty("dates")
	private String dates;
	@JsonProperty("filter")
	private String filter;
	@JsonProperty("data")
	private List<Data> data = null;
	@JsonProperty("title")
	private String title;
	@JsonProperty("description")
	private String description;
	@JsonProperty("image")
	private String image;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("chartType")
	public String getChartType() {
		return chartType;
	}

	@JsonProperty("chartType")
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	@JsonProperty("subChartType")
	public String getSubChartType() {
		return subChartType;
	}

	@JsonProperty("subChartType")
	public void setSubChartType(String subChartType) {
		this.subChartType = subChartType;
	}

	@JsonProperty("visualizationCode")
	public String getVisualizationCode() {
		return visualizationCode;
	}

	@JsonProperty("visualizationCode")
	public void setVisualizationCode(String visualizationCode) {
		this.visualizationCode = visualizationCode;
	}

	@JsonProperty("chartFormat")
	public String getChartFormat() {
		return chartFormat;
	}

	@JsonProperty("chartFormat")
	public void setChartFormat(String chartFormat) {
		this.chartFormat = chartFormat;
	}

	@JsonProperty("drillDownChartId")
	public String getDrillDownChartId() {
		return drillDownChartId;
	}

	@JsonProperty("drillDownChartId")
	public void setDrillDownChartId(String drillDownChartId) {
		this.drillDownChartId = drillDownChartId;
	}

	@JsonProperty("filterKeys")
	public String getFilterKeys() {
		return filterKeys;
	}

	@JsonProperty("filterKeys")
	public void setFilterKeys(String filterKeys) {
		this.filterKeys = filterKeys;
	}

	@JsonProperty("customData")
	public String getCustomData() {
		return customData;
	}

	@JsonProperty("customData")
	public void setCustomData(String customData) {
		this.customData = customData;
	}

	@JsonProperty("dates")
	public String getDates() {
		return dates;
	}

	@JsonProperty("dates")
	public void setDates(String dates) {
		this.dates = dates;
	}

	@JsonProperty("filter")
	public String getFilter() {
		return filter;
	}

	@JsonProperty("filter")
	public void setFilter(String filter) {
		this.filter = filter;
	}

	@JsonProperty("data")
	public List<Data> getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(List<Data> data) {
		this.data = data;
	}

	@JsonProperty("title")
	public String getTitle() {
		return title;
	}

	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = title;
	}

	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonProperty("image")
	public String getImage() {
		return image;
	}

	@JsonProperty("image")
	public void setImage(String image) {
		this.image = image;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
