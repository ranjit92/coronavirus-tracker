package com.corona.virus.coronavirustracker.model;

import lombok.Data;

@Data
public class LocationStat {

	private String state;
	
	private String country;
	
	private int latestTotalCase;
	
	private int diff;
	
}
