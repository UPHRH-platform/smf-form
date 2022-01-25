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
@JsonPropertyOrder({
    "id",
    "vizType",
    "headerName",
    "headerValue",
    "headerSymbol",
    "colorPaletteCode",
    "colorPaletteId",
    "plots"
})
public class Data {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("vizType")
    private String vizType;
    @JsonProperty("headerName")
    private String headerName;
    @JsonProperty("headerValue")
    private String headerValue;
    @JsonProperty("headerSymbol")
    private String headerSymbol;
    @JsonProperty("colorPaletteCode")
    private String colorPaletteCode;
    @JsonProperty("colorPaletteId")
    private Object colorPaletteId;
    @JsonProperty("plots")
    private List<Plot> plots = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("vizType")
    public String getVizType() {
        return vizType;
    }

    @JsonProperty("vizType")
    public void setVizType(String vizType) {
        this.vizType = vizType;
    }

    @JsonProperty("headerName")
    public String getHeaderName() {
        return headerName;
    }

    @JsonProperty("headerName")
    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    @JsonProperty("headerValue")
    public String getHeaderValue() {
        return headerValue;
    }

    @JsonProperty("headerValue")
    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    @JsonProperty("headerSymbol")
    public String getHeaderSymbol() {
        return headerSymbol;
    }

    @JsonProperty("headerSymbol")
    public void setHeaderSymbol(String headerSymbol) {
        this.headerSymbol = headerSymbol;
    }

    @JsonProperty("colorPaletteCode")
    public String getColorPaletteCode() {
        return colorPaletteCode;
    }

    @JsonProperty("colorPaletteCode")
    public void setColorPaletteCode(String colorPaletteCode) {
        this.colorPaletteCode = colorPaletteCode;
    }

    @JsonProperty("colorPaletteId")
    public Object getColorPaletteId() {
        return colorPaletteId;
    }

    @JsonProperty("colorPaletteId")
    public void setColorPaletteId(Object colorPaletteId) {
        this.colorPaletteId = colorPaletteId;
    }

    @JsonProperty("plots")
    public List<Plot> getPlots() {
        return plots;
    }

    @JsonProperty("plots")
    public void setPlots(List<Plot> plots) {
        this.plots = plots;
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
