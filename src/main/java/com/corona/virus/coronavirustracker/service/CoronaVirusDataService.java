package com.corona.virus.coronavirustracker.service;

import java.util.List;

import com.corona.virus.coronavirustracker.model.CovidResponseFlat;
import com.corona.virus.coronavirustracker.model.LocationStat;

public interface CoronaVirusDataService {
	
	public List<LocationStat> getListLocation();
	
	public List<CovidResponseFlat> getCovidAPIResponses();

}
