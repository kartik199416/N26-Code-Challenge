package com.n26.model;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.n26.util.BigDecimalDeserializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class Transaction {

	private @Getter @NonNull BigDecimal amount;

	private @Getter long timestamp;

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

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
