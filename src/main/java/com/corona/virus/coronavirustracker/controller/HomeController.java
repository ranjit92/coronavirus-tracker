package com.corona.virus.coronavirustracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.corona.virus.coronavirustracker.model.LocationStat;
import com.corona.virus.coronavirustracker.service.CoronaVirusDataService;

@Controller
public class HomeController {

	@Autowired
	CoronaVirusDataService trackerService;

	@GetMapping("/")
	public String home(Model model) {
		
		List<LocationStat> listLocation = trackerService.getListLocation();
		int totalCase = listLocation.stream().mapToInt(stat -> stat.getLatestTotalCase()).sum();

		model.addAttribute("listLocation", listLocation);
		model.addAttribute("totalcase", totalCase);
		return "home";
	}
}