package com.cosmicdoc.opdmanagement.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.cloud.Timestamp;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

/**
 * Custom deserializer for Google Cloud Timestamp to handle ISO-8601 formatted strings
 */
public class GoogleCloudTimestampDeserializer extends JsonDeserializer<Timestamp> {
    
    @Override
    public Timestamp deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateStr = p.getText();
        try {
            // Parse ISO-8601 formatted string to Instant
            TemporalAccessor temporal = DateTimeFormatter.ISO_INSTANT.parse(dateStr);
            Instant instant = Instant.from(temporal);
            
            // Convert to Google Cloud Timestamp
            return Timestamp.ofTimeSecondsAndNanos(
                instant.getEpochSecond(), 
                instant.getNano()
            );
        } catch (DateTimeParseException e) {
            throw new IOException("Failed to parse timestamp: " + dateStr, e);
        }
    }
}
