package com.corona.virus.coronavirustracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Cases {
	@JsonProperty("new")
	private String _new;
	@JsonProperty("active")
	private Integer active;
	@JsonProperty("critical")
	private Integer critical;
	@JsonProperty("recovered")
	private Integer recovered;
	@JsonProperty("total")
	private Integer total;
	
	
}
