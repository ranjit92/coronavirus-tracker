package com.corona.virus.coronavirustracker.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.corona.virus.coronavirustracker.model.Cases;
import com.corona.virus.coronavirustracker.model.CovidData;
import com.corona.virus.coronavirustracker.model.CovidResponseFlat;
import com.corona.virus.coronavirustracker.model.Deaths;
import com.corona.virus.coronavirustracker.model.LocationStat;
import com.corona.virus.coronavirustracker.model.Response;

@Service
public class CoronaVirusDataServiceImpl implements CoronaVirusDataService{

	private static final String RAPIDAPI_URL = "https://covid-193.p.rapidapi.com/statistics";

	private static final String X_RAPIDAPI_VALUE = "9pRobULMZUmsh9B2xKyVSVWvwntAp1YuAgNjsnUvxi00wr8SBr";

	private static final String X_RAPIDAPI_KEY = "x-rapidapi-key";

	private static final String HOST = "covid-193.p.rapidapi.com";

	private static final String X_RAPIDAPI_HOST = "x-rapidapi-host";

	private static final String COUNTRY_REGION = "Country/Region";

	private static final String COVIDURL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	private static List<LocationStat> listLocation = new ArrayList<>();
	
	private static List<CovidResponseFlat> covidAPIResponses = new ArrayList<>();
	
	@Override
	public List<LocationStat> getListLocation() {
		return listLocation;
	}

	@Override
	public List<CovidResponseFlat> getCovidAPIResponses() {
			return covidAPIResponses;
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
//		System.out.println(newListLocation);
		
		this.listLocation = newListLocation;
		
	
	}

	/**
	 * @throws IOException
	 *  Runs every 20 minute(Server time UTC)
	 *  API dependency Rapidapi which updates on every 15 minute 
	 *  
	 */
	@PostConstruct
	@Scheduled(cron = "0 */20 * * * *") 
	private void fetchCOVIDDataAPI() throws IOException {
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add(X_RAPIDAPI_HOST, HOST);
		headers.add(X_RAPIDAPI_KEY, X_RAPIDAPI_VALUE);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		 ResponseEntity<CovidData> apiResponse = restTemplate.exchange(RAPIDAPI_URL, HttpMethod.GET, entity, CovidData.class);
		 CovidData covidData = apiResponse.getBody();
		 List<Response> responses = covidData.getResponse();
		 System.out.println(responses.toString());
		 
		 List<CovidResponseFlat> flatCovidResponse = responses.stream().map(response -> getFlatResponse(response)).collect(Collectors.toCollection(ArrayList::new));
		 this.covidAPIResponses = flatCovidResponse;
	}


	private CovidResponseFlat getFlatResponse(Response response) {
		
		CovidResponseFlat covidResponseFlat = new CovidResponseFlat();
		covidResponseFlat.setCountry(response.getCountry());
		
		Cases cases = response.getCases();
		
		if(null !=cases.get_new() && !cases.get_new().isEmpty())
			covidResponseFlat.setNewCases(cases.get_new());
		
		if(null != cases.getTotal() && cases.getTotal() > 0)
			covidResponseFlat.setTotalCases(cases.getTotal());
		
		if(null != cases.getActive() && cases.getActive() > 0)
			covidResponseFlat.setActiveCases(cases.getActive());
		
		if(null != cases.getRecovered() && cases.getRecovered() > 0)
			covidResponseFlat.setTotalRecovered(cases.getRecovered());
		
		if(null != cases.getCritical() && cases.getCritical() > 0)
			covidResponseFlat.setCriticalCases(cases.getCritical());
		
		Deaths deaths = response.getDeaths();
		
		if(null !=deaths.get_new() && !deaths.get_new().isEmpty())
			covidResponseFlat.setNewDeaths(deaths.get_new());
		

		if(null != deaths.getTotal() && deaths.getTotal() > 0)
			covidResponseFlat.setTotalDeaths(deaths.getTotal());
		
		if(null != response.getTests().getTotal() && response.getTests().getTotal() > 0)
			covidResponseFlat.setTotalTests(response.getTests().getTotal());
		
		return covidResponseFlat;
	}


}

