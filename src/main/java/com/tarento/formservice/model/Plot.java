
package com.tarento.formservice.model;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.ToString;

@ToString(includeFieldNames = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "label",
    "name",
    "value",
    "valueLabel",
    "symbol",
    "parentName",
    "parentLabel",
    "isPercentage",
    "colorCode"
})
@Generated("jsonschema2pojo")
public class Plot {

    @JsonProperty("label")
    private String label;
    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private Integer value;
    @JsonProperty("valueLabel")
    private String valueLabel;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("parentName")
    private Object parentName;
    @JsonProperty("parentLabel")
    private Object parentLabel;
    @JsonProperty("isPercentage")
    private Boolean isPercentage;
    @JsonProperty("colorCode")
    private String colorCode;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("label")
    public String getLabel() {
        return label;
    }

    @JsonProperty("label")
    public void setLabel(String label) {
        this.label = label;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("value")
    public Integer getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(Integer value) {
        this.value = value;
    }

    @JsonProperty("valueLabel")
    public String getValueLabel() {
        return valueLabel;
    }

    @JsonProperty("valueLabel")
    public void setValueLabel(String valueLabel) {
        this.valueLabel = valueLabel;
    }

    @JsonProperty("symbol")
    public String getSymbol() {
        return symbol;
    }

    @JsonProperty("symbol")
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("parentName")
    public Object getParentName() {
        return parentName;
    }

    @JsonProperty("parentName")
    public void setParentName(Object parentName) {
        this.parentName = parentName;
    }

    @JsonProperty("parentLabel")
    public Object getParentLabel() {
        return parentLabel;
    }

    @JsonProperty("parentLabel")
    public void setParentLabel(Object parentLabel) {
        this.parentLabel = parentLabel;
    }

    @JsonProperty("isPercentage")
    public Boolean getIsPercentage() {
        return isPercentage;
    }

    @JsonProperty("isPercentage")
    public void setIsPercentage(Boolean isPercentage) {
        this.isPercentage = isPercentage;
    }

    @JsonProperty("colorCode")
    public String getColorCode() {
        return colorCode;
    }

    @JsonProperty("colorCode")
    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
