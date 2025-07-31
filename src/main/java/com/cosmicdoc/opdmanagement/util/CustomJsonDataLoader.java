package com.cosmicdoc.opdmanagement.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom JSON data loader that uses our configured ObjectMapper with
 * proper Google Cloud Timestamp deserializer
 */
@Component
public class CustomJsonDataLoader {
    private static final Logger logger = LoggerFactory.getLogger(CustomJsonDataLoader.class);
    
    private final ObjectMapper objectMapper;
    
    @Autowired
    public CustomJsonDataLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        logger.info("CustomJsonDataLoader initialized with configured ObjectMapper");
    }
    
    /**
     * Loads JSON data from a file in the classpath and returns it as a Map
     *
     * @param filePath the path to the JSON file in the classpath
     * @param <T> the type of objects in the list
     * @return a Map with entity type as key and list of entities as value
     */
    public <T> Map<String, List<T>> loadDataAsMap(String filePath) {
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            try (InputStream inputStream = resource.getInputStream()) {
                // Create JavaType for String key
                var keyType = objectMapper.getTypeFactory().constructType(String.class);
                // Create JavaType for List<Object> value
                var valueType = objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class);
                // Create MapType with the key and value types
                var mapType = objectMapper.getTypeFactory().constructMapType(HashMap.class, keyType, valueType);
                // Read value with the constructed map type
                return objectMapper.readValue(inputStream, mapType);
            }
        } catch (IOException e) {
            logger.error("Failed to load data from file: {}", filePath, e);
            return Collections.emptyMap();
        }
    }
    
    /**
     * Loads JSON data from a file in the classpath and returns it as a List
     *
     * @param filePath the path to the JSON file in the classpath
     * @param valueType the class of objects in the list
     * @param <T> the type of objects in the list
     * @return a List of entities
     */
    public <T> List<T> loadDataAsList(String filePath, Class<T> valueType) {
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            try (InputStream inputStream = resource.getInputStream()) {
                return objectMapper.readValue(inputStream, 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, valueType));
            }
        } catch (IOException e) {
            logger.error("Failed to load data from file: {}", filePath, e);
            return Collections.emptyList();
        }
    }
}
