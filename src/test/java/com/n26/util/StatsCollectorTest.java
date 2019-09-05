package com.n26.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Arrays;

import org.junit.Test;

import com.n26.model.Transaction;

public class StatsCollectorTest {

	@Test
	public void testIndexOf() {
		Instant instant = Instant.now();
		long now = instant.toEpochMilli();
		
		int indexNow = Math.toIntExact((now / StatsCollector.SPAN_MS) % StatsCollector.COUNT);
		assertEquals(indexNow, StatsCollector.indexOf(now));
		
	}
	
	@Test
	public void testIndexOfPeriod() {
		Instant instant = Instant.now();
		long now = instant.toEpochMilli();
		long previous = instant.minusMillis(StatsCollector.SPAN_MS*StatsCollector.COUNT).toEpochMilli();
		
		assertEquals(StatsCollector.indexOf(now), StatsCollector.indexOf(previous));
	}
	
	@Test
	public void testIndexOfEdgeValues() {
		Instant instant = Instant.now();
		long now = instant.toEpochMilli();
		long spanOffset = now % StatsCollector.SPAN_MS;
		
		// minimum long that belongs to particular index
		long lowEdge = now - spanOffset;
		assertEquals(StatsCollector.indexOf(now), StatsCollector.indexOf(lowEdge));
		assertNotEquals(StatsCollector.indexOf(now), StatsCollector.indexOf(lowEdge - 1));
		
		// max long that belongs to same index
		long highEdge = now + (StatsCollector.SPAN_MS - spanOffset - 1);
		assertEquals(StatsCollector.indexOf(now), StatsCollector.indexOf(highEdge));
		assertNotEquals(StatsCollector.indexOf(now), StatsCollector.indexOf(highEdge + 1));
	}
	
	@Test
	public void testRangeOf() {
		Instant instant = Instant.now();
		long now = instant.toEpochMilli();
		long offset = now % StatsCollector.SPAN_MS;
		
		long[] edges = StatsCollector.rangeOf(now);
		assertEquals(now - offset, edges[0]);
		assertEquals(now + (StatsCollector.SPAN_MS - offset - 1), edges[1]);
		assertEquals(edges[1] - edges[0], StatsCollector.SPAN_MS -1);
	}
	
	@Test
	public void testRangeOfNoOverlapping() {
		Instant instant = Instant.now();
		long now = instant.toEpochMilli();
		long lastInterval[] = StatsCollector.rangeOf(now);
		
		long previous = instant.minusMillis(StatsCollector.SPAN_MS).toEpochMilli();
		long prevInterval[] = StatsCollector.rangeOf(previous); 
		
		assertNotEquals(Arrays.equals(lastInterval, prevInterval), true);
	}
	
	@Test
	public void testRangeOfTightEdges() {
		Instant instant = Instant.now();
		long now = instant.toEpochMilli();
		long lastInterval[] = StatsCollector.rangeOf(now);
		
		long previous = instant.minusMillis(StatsCollector.SPAN_MS).toEpochMilli();
		long prevInterval[] = StatsCollector.rangeOf(previous); 
		
		assertNotEquals(lastInterval[0], prevInterval[1]);
		assertEquals(lastInterval[0]-1, prevInterval[1]);
		
		now = previous;
		lastInterval = prevInterval;
		previous = instant.minusMillis(StatsCollector.SPAN_MS * 2).toEpochMilli();
		prevInterval = StatsCollector.rangeOf(previous);
		
		assertNotEquals(lastInterval[0], prevInterval[1]);
		assertEquals(lastInterval[0]-1, prevInterval[1]);
	}
	
	@Test
	public void testStatsCollectorImmutability() {
		StatsCollector prev = StatsCollector.EMPTY_STATS;
		Transaction tx = new Transaction(BigDecimal.ZERO, 0L);
		
		// from should always return a new value;
		StatsCollector next = StatsCollector.from(prev, tx);
		assertNotEquals(prev,next);
		
		// since reducer scope has access to fields, you never know ;)
		StatsCollector q = StatsCollector.reducer.apply(prev, next);
		assertNotEquals(prev, q);
		assertNotEquals(next, q);
		
	}
	
	@Test
	public void testStatsCollectorFromNullPrevious() {
		long now = Instant.now().toEpochMilli();
		long rangeMin = StatsCollector.rangeOf(now)[0];
		Transaction tx = new Transaction(BigDecimal.ONE, now);
		
		StatsCollector next = StatsCollector.from(null, tx);
		assertNotNull(next);
		assertEquals(tx.getAmount(), next.getSum());
		assertEquals(1, next.getCount());
		assertEquals(tx.getAmount(), next.getMax());
		assertEquals(tx.getAmount(), next.getMin());
		assertEquals(rangeMin, next.getTimestamp());
	}
	
	@Test
	public void testStatsCollectorFrom() {
		StatsCollector prev = StatsCollector.EMPTY_STATS;
		long now = Instant.now().toEpochMilli();
		long rangeMin = StatsCollector.rangeOf(now)[0];
		long rangeMax = StatsCollector.rangeOf(now)[1];
		
		Transaction tx = new Transaction(BigDecimal.ONE, rangeMax - 1);
		Transaction tx2 = new Transaction(new BigDecimal("3"), rangeMax);
		
		prev = StatsCollector.from(prev, tx);
		StatsCollector next = StatsCollector.from(prev, tx2);
		
		assertNotEquals(prev, next);
		assertEquals(new BigDecimal("4"), next.getSum());
		assertEquals(2, next.getCount());
		assertEquals(new BigDecimal("3"), next.getMax());
		assertEquals(BigDecimal.ONE, next.getMin());
		assertEquals(rangeMin, next.getTimestamp());
	}
	
	@Test
	public void testStatsCollectorFromStale() {
		StatsCollector prev = StatsCollector.EMPTY_STATS;
		long now = Instant.now().toEpochMilli();
		long rangeMin = StatsCollector.rangeOf(now)[0];
		long rangeMax = StatsCollector.rangeOf(now)[1];
		
		Transaction tx = new Transaction(BigDecimal.ONE, rangeMax - (StatsCollector.SPAN_MS * StatsCollector.COUNT));
		Transaction tx2 = new Transaction(new BigDecimal("3"), rangeMax);
		
		prev = StatsCollector.from(prev, tx);
		StatsCollector next = StatsCollector.from(prev, tx2);
		
		assertNotEquals(prev, next);
		assertEquals(new BigDecimal("3"), next.getSum());
		assertEquals(1, next.getCount());
		assertEquals(new BigDecimal("3"), next.getMax());
		assertEquals(new BigDecimal("3"), next.getMin());
		assertEquals(rangeMin, next.getTimestamp());
	}
	
	@Test
	public void testStatsCollectorReducer() {
		long now = Instant.now().toEpochMilli();
		StatsCollector p = StatsCollector.from(StatsCollector.EMPTY_STATS, new Transaction(BigDecimal.ONE, now-1));
		StatsCollector q = StatsCollector.from(StatsCollector.EMPTY_STATS, new Transaction(new BigDecimal("2"), now));
		
		StatsCollector r = StatsCollector.reducer.apply(p, q);
		
		assertEquals(new BigDecimal("3"), r.getSum());
		assertEquals(2, r.getCount());
		assertEquals(new BigDecimal("2"), r.getMax());
		assertEquals(BigDecimal.ONE, r.getMin());
		assertEquals(StatsCollector.rangeOf(now-1)[0], r.getTimestamp());
		
	}
	
	@Test
	public void testStatsCollectorReducerNullArguments() {
		assertEquals(StatsCollector.EMPTY_STATS, StatsCollector.reducer.apply(null, null));
		
		StatsCollector p = StatsCollector.from(StatsCollector.EMPTY_STATS, new Transaction(BigDecimal.ONE, Instant.now().toEpochMilli()));
		
		StatsCollector r = StatsCollector.reducer.apply(p, null);
		assertNotEquals(StatsCollector.EMPTY_STATS, r);
		
		StatsCollector q = StatsCollector.reducer.apply(null, p);
		assertNotEquals(StatsCollector.EMPTY_STATS, q);
	}
	
	@Test
	public void testStatsCollectorGetAvg() {
		long now = Instant.now().toEpochMilli();
		Transaction tx = new Transaction(new BigDecimal("4"), now);
		StatsCollector next = StatsCollector.from(null, tx);
		assertEquals(new BigDecimal("4").setScale(2, RoundingMode.HALF_UP), next.getAvg());
		
		tx = new Transaction(new BigDecimal("8"), now);
		next = StatsCollector.from(next, tx);
		assertEquals(new BigDecimal("6").setScale(2, RoundingMode.HALF_UP), next.getAvg());
		
		tx = new Transaction(new BigDecimal("6"), now);
		next = StatsCollector.from(next, tx);
		assertEquals(new BigDecimal("6").setScale(2, RoundingMode.HALF_UP), next.getAvg());
	}
	
	@Test
	public void testStatsCollectorGetAvgDivisionByZero() {
		assertEquals(BigDecimal.ZERO, StatsCollector.EMPTY_STATS.getAvg());
	}
}
