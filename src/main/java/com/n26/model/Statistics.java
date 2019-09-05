package com.n26.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.n26.util.BigDecimalSerializer;
import com.n26.util.StatsCollector;

import lombok.Getter;
import lombok.ToString;

//@AllArgsConstructor
@ToString
public class Statistics {

	@JsonSerialize(using = BigDecimalSerializer.class)
	private @Getter BigDecimal sum;

	@JsonSerialize(using = BigDecimalSerializer.class)
	private @Getter BigDecimal avg;

	@JsonSerialize(using = BigDecimalSerializer.class)
	private @Getter BigDecimal max;

	@JsonSerialize(using = BigDecimalSerializer.class)
	private @Getter BigDecimal min;

	private @Getter long count;

	public Statistics(BigDecimal sum, BigDecimal avg, BigDecimal max, BigDecimal min, long count) {
		super();
		this.sum = sum;
		this.avg = avg;
		this.max = max;
		this.min = min;
		this.count = count;
	}

	public BigDecimal getSum() {
		return sum;
	}

	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}

	public BigDecimal getAvg() {
		return avg;
	}

	public void setAvg(BigDecimal avg) {
		this.avg = avg;
	}

	public BigDecimal getMax() {
		return max;
	}

	public void setMax(BigDecimal max) {
		this.max = max;
	}

	public BigDecimal getMin() {
		return min;
	}

	public void setMin(BigDecimal min) {
		this.min = min;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public static Statistics from(StatsCollector s) {
		return new Statistics(s.getSum(), s.getAvg(), s.getMax(), s.getMin(), s.getCount());
	}
}
