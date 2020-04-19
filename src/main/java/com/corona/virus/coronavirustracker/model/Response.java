package com.corona.virus.coronavirustracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Response {

	@JsonProperty("country")
	private String country;
	@JsonProperty("cases")
	private Cases cases;
	@JsonProperty("deaths")
	private Deaths deaths;
	@JsonProperty("tests")
	private Tests tests;
	@JsonProperty("day")
	private String day;
	@JsonProperty("time")
	private String time;
}
