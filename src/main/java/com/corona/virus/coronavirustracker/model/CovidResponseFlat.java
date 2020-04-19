package com.corona.virus.coronavirustracker.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CovidResponseFlat {

	private String country;
	private Integer totalCases;
	private String newCases;
	private Integer totalDeaths;
	private String newDeaths;
	private Integer totalRecovered;
	private Integer activeCases;
	private Integer criticalCases;
	private Integer totalTests;
}
