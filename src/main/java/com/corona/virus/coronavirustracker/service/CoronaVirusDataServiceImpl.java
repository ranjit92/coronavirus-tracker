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
public class CoronaVirusDataServiceImpl implements CoronaVirusDataService{

	private static final String COUNTRY_REGION = "Country/Region";

	private static final String COVIDURL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	private static List<LocationStat> listLocation = new ArrayList<>();
	
	@Override
	public List<LocationStat> getListLocation() {
		return listLocation;
	}


	/**
	 * @throws IOException
	 * Runs every 1st hour 10th minute and 0's second of everyday(Server time UTC)
	 */
	@PostConstruct
	@Scheduled(cron = "0 10 1 * * *") 
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
		    
		    if(countryTotal.containsKey(record.get(COUNTRY_REGION))) {
		    	locationStat = countryTotal.get(record.get(COUNTRY_REGION));
		    	int newTotal = locationStat.getLatestTotalCase() + latestTotal;
		    	locationStat.setLatestTotalCase(newTotal);
		    	locationStat.setDiff(locationStat.getDiff()+(latestTotal-oneDayPrevious));
		    	countryTotal.put(record.get(COUNTRY_REGION), locationStat);
		    }
		    else {
		    	locationStat.setCountry(record.get(COUNTRY_REGION));
		    	locationStat.setLatestTotalCase(latestTotal);
		    	locationStat.setDiff(latestTotal-oneDayPrevious);
		    	
		    	countryTotal.put(record.get(COUNTRY_REGION), locationStat);
		    }
		    
		}
		
		newListLocation = new ArrayList<>(countryTotal.values());
		Collections.sort(newListLocation, (object1,object2) -> object2.getLatestTotalCase()-object1.getLatestTotalCase());
		System.out.println(newListLocation);
		
		this.listLocation = newListLocation;
		
	
	}
}
