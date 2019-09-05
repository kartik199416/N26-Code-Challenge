package com.n26.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.n26.model.Statistics;
import com.n26.service.StatisticsService;

@RestController
public class StatsController {
	
	@Autowired
	private StatisticsService statsService;

	@GetMapping("/statistics")
	public Statistics getStatsSummary() {
		return statsService.getStatistics();
	}
}
