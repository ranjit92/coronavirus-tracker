package com.corona.virus.coronavirustracker.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
		for (CSVRecord record : records) {
			LocationStat locationStat = new LocationStat();
		    
			locationStat.setState(record.get("Province/State"));
		    locationStat.setCountry(record.get("Country/Region"));
		    locationStat.setLatestTotalCase(Integer.parseInt(record.get(record.size()-1)));
		    newListLocation.add(locationStat);
		}
		this.listLocation = newListLocation;
	
	}
}
