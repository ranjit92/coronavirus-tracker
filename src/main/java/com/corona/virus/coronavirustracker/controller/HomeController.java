package com.corona.virus.coronavirustracker.controller;

import java.text.NumberFormat;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.corona.virus.coronavirustracker.model.CovidResponseFlat;
import com.corona.virus.coronavirustracker.model.LocationStat;
import com.corona.virus.coronavirustracker.service.CoronaVirusDataService;
import com.corona.virus.coronavirustracker.service.CoronaVirusDataServiceImpl;

@Controller
public class HomeController {

	@Autowired
	CoronaVirusDataService trackerService;

	NumberFormat myFormat = NumberFormat.getInstance();

	@GetMapping("/basic")
	public String home(Model model) {

		List<LocationStat> listLocation = trackerService.getListLocation();
		int totalCase = listLocation.stream().mapToInt(stat -> stat.getLatestTotalCase()).sum();

		myFormat.setGroupingUsed(true);
		model.addAttribute("listLocation", listLocation);
		model.addAttribute("totalcase", myFormat.format(totalCase));
		return "home";
	}

	@GetMapping("/")
	public String displayDetailedTable(Model model) {
		myFormat.setGroupingUsed(true);
		List<CovidResponseFlat> covidFlatList = trackerService.getCovidAPIResponses();
		int totalCase = covidFlatList.stream().filter(tr -> tr.getActiveCases() != null).mapToInt(stat -> stat.getActiveCases()).sum();

		model.addAttribute("covidFlatList", covidFlatList);
		model.addAttribute("totalcase", myFormat.format(totalCase));
		return "covidtemplate";
	}
}
