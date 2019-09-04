package com.n26.util;

import java.io.IOException;
import java.util.Date;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DateDeserailizer extends JsonDeserializer<Date> {
	
	private static final DateTimeFormatter df = DateTimeFormat.forPattern("dd/mm/yyyy hh:mm:ss");

	@Override
	public Date deserialize(JsonParser arg0, DeserializationContext arg1) throws IOException, JsonProcessingException {
		// TODO Auto-generated method stub
		System.out.println(arg0.getText().toString());
		System.out.println(arg0.getCurrentToken());
		if(arg0.getCurrentToken().equals(JsonToken.VALUE_STRING)){
			return df.parseDateTime(arg0.getText().toString()).toDate();
		}
		return null;
	}

}
