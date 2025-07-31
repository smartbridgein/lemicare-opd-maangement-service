package com.cosmicdoc.opdmanagement;

import com.cosmicdoc.opdmanagement.config.FirebaseInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
    basePackages = {"com.cosmicdoc.opdmanagement"},
    excludeFilters = {}
)
public class OpdManagementServiceApplication {
    private static final Logger logger = LoggerFactory.getLogger(OpdManagementServiceApplication.class);

    public static void main(String[] args) {
        // Initialize Firebase before starting the Spring application
        try {
            // Use our custom Firebase initialization utility
            FirebaseInit.initializeFirebase();
            
            // Then start Spring application after Firebase is initialized
            SpringApplication.run(OpdManagementServiceApplication.class, args);
        } catch (Exception e) {
            logger.error("Failed to initialize Firebase: {}", e.getMessage(), e);
            // Exit early if Firebase initialization fails - it's required for the application to function
            System.exit(1);
        }
    }
}
