package com.corona.virus.coronavirustracker.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.corona.virus.coronavirustracker.model.LocationStat;

@Service
public class CoronaVirusDataService {

	private static final String COVIDURL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	private static List<LocationStat> listLocation = new ArrayList<>();
	
	public List<LocationStat> getListLocation() {
		return listLocation;
	}


	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchCOVIDData() throws IOException {
		
		List<LocationStat> newListLocation = new ArrayList<>();
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(COVIDURL, String.class);
		StringReader csvReader = new StringReader(response.getBody());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvReader);
		
		Map<String, LocationStat> countryTotal = new HashMap<>();
		for (CSVRecord record : records) {
			LocationStat locationStat = new LocationStat();
		    
		    int latestTotal = Integer.parseInt(record.get(record.size()-1));
		    int oneDayPrevious = Integer.parseInt(record.get(record.size()-2)); 
		    
		    if(countryTotal.containsKey(record.get("Country/Region"))) {
		    	locationStat = countryTotal.get(record.get("Country/Region"));
		    	int newTotal = locationStat.getLatestTotalCase() + latestTotal;
		    	locationStat.setLatestTotalCase(newTotal);
		    	locationStat.setDiff(locationStat.getDiff()+(latestTotal-oneDayPrevious));
		    	countryTotal.put(record.get("Country/Region"), locationStat);
		    }
		    else {
		    	locationStat.setCountry(record.get("Country/Region"));
		    	locationStat.setLatestTotalCase(latestTotal);
		    	locationStat.setDiff(latestTotal-oneDayPrevious);
		    	
		    	countryTotal.put(record.get("Country/Region"), locationStat);
		    }
		    
		}
		
		newListLocation = new ArrayList<>(countryTotal.values());
		Collections.sort(newListLocation, (object1,object2) -> object2.getLatestTotalCase()-object1.getLatestTotalCase());
		System.out.println(newListLocation);
		
		this.listLocation = newListLocation;
		
	
	}
}
