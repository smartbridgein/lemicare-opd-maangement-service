package com.cosmicdoc.opdmanagement.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.cloud.Timestamp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson configuration to register custom deserializers
 * including handling of Google Cloud Timestamp fields
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();
        
        // Create and register a module for the Google Cloud Timestamp deserializer
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Timestamp.class, new GoogleCloudTimestampDeserializer());
        objectMapper.registerModule(module);
        
        return objectMapper;
    }
}
