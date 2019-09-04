package com.n26.util;

import java.math.BigDecimal;
import java.util.function.BinaryOperator;

import com.n26.model.Transaction;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class StatsCollector {

	public static final long SPAN_MS = 10;
	public static final int COUNT = 6000;
	public static final StatsCollector EMPTY_STATS = new StatsCollector();

	private @Getter BigDecimal sum = BigDecimal.ZERO;
	// private @Getter BigDecimal avg = BigDecimal.ZERO;
	private @Getter BigDecimal max = BigDecimal.ZERO;
	private @Getter BigDecimal min = BigDecimal.ZERO;
	private @Getter long count = 0L;
	private @Getter long timestamp = 0L;

	public static final BinaryOperator<StatsCollector> reducer = (prev, next) -> {
		if (prev == null)
			prev = StatsCollector.EMPTY_STATS;
		if (next == null)
			return prev;

		return new StatsCollector(prev.getSum().add(next.getSum()), prev.getCount() + next.getCount(),
				prev.getMax().compareTo(next.getMax()) == 1 ? prev.getMin() : next.getMax(),
				prev.getMin().compareTo(next.getMin()) == 1 ? next.getMin() : prev.getMin(),
				prev.getTimestamp() < next.getTimestamp() ? prev.getTimestamp() : next.getTimestamp());
	};

	public static StatsCollector from(StatsCollector prev, Transaction tx){
		StatsCollector statsCollector = new StatsCollector();
		
		if(prev == null || prev == StatsCollector.EMPTY_STATS ||(tx.getTimestamp() - prev.getTimestamp() >= SPAN_MS)) {
			statsCollector.timestamp = rangeOf(tx.getTimestamp())[0];
			statsCollector.sum = tx.getAmount();
			statsCollector.count = 1;
			statsCollector.max = tx.getAmount();
			statsCollector.min = tx.getAmount();
			
		} else {
			statsCollector.timestamp = prev.timestamp;
			statsCollector.sum = prev.sum.add(tx.getAmount());
			statsCollector.count = prev.count + 1;
			statsCollector.max = tx.getAmount().compareTo(prev.max) == 1 ? tx.getAmount() : prev.max ;
			statsCollector.min = (prev.getCount() ==0 || prev.getMin().compareTo(tx.getAmount())) == 1 ? tx.getAmount() : prev.getMin();
		}
		
		return statsCollector;
	}

	public static long[] rangeOf(long millis) {
		long offset = millis % SPAN_MS;
		return new long[] { millis - offset, millis + (SPAN_MS - offset - 1) };
	}

	public BigDecimal getAvg() {
		return (count == 0) ? BigDecimal.ZERO : sum.divide(new BigDecimal(count), 2, BigDecimal.ROUND_HALF_UP);
	}

	public static int indexOf(long millis) {
		return Math.toIntExact((millis / SPAN_MS) % COUNT);
	}
}
