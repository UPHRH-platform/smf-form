package com.tarento.formservice.models;

import java.util.HashMap;
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
@JsonPropertyOrder({
"key",
"value",
"primary",
"order"
})
public class InformationMedia {

@JsonProperty("key")
private String key;
@JsonProperty("value")
private String value;
@JsonProperty("primary")
private Boolean primary;
@JsonProperty("order")
private Integer order;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("key")
public String getKey() {
return key;
}

@JsonProperty("key")
public void setKey(String key) {
this.key = key;
}

@JsonProperty("value")
public String getValue() {
return value;
}

@JsonProperty("value")
public void setValue(String value) {
this.value = value;
}

@JsonProperty("primary")
public Boolean getPrimary() {
return primary;
}

@JsonProperty("primary")
public void setPrimary(Boolean primary) {
this.primary = primary;
}

@JsonProperty("order")
public Integer getOrder() {
return order;
}

@JsonProperty("order")
public void setOrder(Integer order) {
this.order = order;
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
