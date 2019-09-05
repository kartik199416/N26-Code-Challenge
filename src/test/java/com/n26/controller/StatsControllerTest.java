package com.n26.controller;

import java.math.BigDecimal;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.n26.model.Statistics;
import com.n26.service.StatisticsService;

@RunWith(SpringRunner.class)
@WebMvcTest(StatsController.class)
public class StatsControllerTest {

	@Autowired
	MockMvc mvc;
	@MockBean
	StatisticsService service;

	@Test
	public void contextLoads() throws Exception {
	}

	@Test
	public void testStatistics() throws Exception {
		Statistics mockStatistics = new Statistics(new BigDecimal("14"), new BigDecimal("22"), new BigDecimal("12"),
				new BigDecimal("1"), 2L);

		doReturn(mockStatistics).when(service).getStatistics();

		mvc.perform(get("/statistics").contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isOk())
				.andExpect(jsonPath("$.sum", is("14.00"))).andExpect(jsonPath("$.avg", is("22.00")))
				.andExpect(jsonPath("$.max", is("12.00"))).andExpect(jsonPath("$.min", is("1.00")))
				.andExpect(jsonPath("$.count", is(2)));

		verify(service, times(1)).getStatistics();

	}
}
