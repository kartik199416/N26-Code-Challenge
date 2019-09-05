package com.n26.service;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.n26.model.Statistics;
import com.n26.model.Transaction;
import com.n26.util.StatsCollector;

@Service
public class StatisticsServiceImpl implements StatisticsService {
	
	private AtomicReferenceArray<StatsCollector> statsCollector;
	
	@PostConstruct
	private void postConstruct() {
		initialize();
	}

	private void initialize() {
		statsCollector = new AtomicReferenceArray<>(StatsCollector.COUNT);
	}

	@Override
	public void register(Transaction tx) {
		// TODO Auto-generated method stub
		int index = StatsCollector.indexOf(tx.getTimestamp());
		statsCollector.getAndUpdate(index, prev -> StatsCollector.from(prev, tx));
	}

	@Override
	public Statistics getStatistics() {
		// TODO Auto-generated method stub
		return getStatistics(Instant.now().toEpochMilli());
	}
	

	Statistics getStatistics(long epochMilli) {
		// TODO Auto-generated method stub
		StatsCollector sc = IntStream.range(0, StatsCollector.COUNT)
				.mapToObj(statsCollector::get)
				.filter(Objects::nonNull)
				.filter(s -> epochMilli - s.getTimestamp() < StatsCollector.SPAN_MS * StatsCollector.COUNT)
				.reduce(StatsCollector.EMPTY_STATS, StatsCollector.reducer);
				
		return Statistics.from(sc);
	}

	@Override
	public void clearStatistics() {
		// TODO Auto-generated method stub
		initialize();
		
	}
	

}
