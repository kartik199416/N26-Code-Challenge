package com.n26.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.n26.util.BigDecimalSerializer;
import com.n26.util.StatsCollector;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
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

	public static Statistics from(StatsCollector s) {
		return new Statistics(s.getSum(), s.getAvg(), s.getMax(), s.getMin(), s.getCount());
	}
}
