package com.n26.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

	@GetMapping("/statistics")
	public Object getStatsSummary() {
		return null;
	}
}
