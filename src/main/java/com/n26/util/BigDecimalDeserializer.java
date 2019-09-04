package com.n26.util;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class BigDecimalDeserializer extends JsonDeserializer<BigDecimal> {

	@Override
	public BigDecimal deserialize(JsonParser arg0, DeserializationContext arg1)
			throws IOException, JsonProcessingException {
		if (arg0.getCurrentToken().equals(JsonToken.VALUE_STRING)) {
			return (new BigDecimal(arg0.getText().trim())).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		return arg0.getDecimalValue().setScale(2, BigDecimal.ROUND_HALF_UP);
	}

}
