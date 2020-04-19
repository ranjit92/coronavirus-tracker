package com.corona.virus.coronavirustracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Deaths {

	@JsonProperty("new")
	private String _new;
	@JsonProperty("total")
	private Integer total;
}
