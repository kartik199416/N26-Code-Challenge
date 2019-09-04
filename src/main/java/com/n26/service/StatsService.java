package com.n26.service;

import com.n26.model.Statistics;
import com.n26.model.Transaction;

public interface StatsService {

	void register(Transaction tx);
	Statistics getStatistics();
	void clearStatistics();
	
}
