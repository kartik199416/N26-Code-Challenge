package com.n26.model;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.n26.util.BigDecimalDeserializer;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

//@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Transaction {

	private @NonNull BigDecimal amount;

	private long timestamp;

	public Transaction(@JsonDeserialize(using = BigDecimalDeserializer.class) @JsonProperty("amount") BigDecimal amount,
			@JsonProperty("timestamp") String date) {
		this.amount = amount;
		this.timestamp = Instant.parse(date).toEpochMilli();
	}

	public Boolean isValid(Long validityPeriod) {
		return Instant.now().toEpochMilli() - timestamp <= validityPeriod;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
}
