package com.corona.virus.coronavirustracker.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CovidData {

	@JsonProperty("get")
	private String get;
	@JsonProperty("parameters")
	private List<Object> parameters = null;
	@JsonProperty("errors")
	private List<Object> errors = null;
	@JsonProperty("results")
	private Integer results;
	@JsonProperty("response")
	private List<Response> response = null;
}
