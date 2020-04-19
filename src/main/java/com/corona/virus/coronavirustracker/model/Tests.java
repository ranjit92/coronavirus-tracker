package com.corona.virus.coronavirustracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Tests {

	@JsonProperty("total")
	private Integer total;
}
