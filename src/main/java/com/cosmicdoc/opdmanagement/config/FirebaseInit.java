package com.cosmicdoc.opdmanagement.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.File;

/**
 * Utility class to initialize Firebase before Spring Boot context creation
 */
public class FirebaseInit {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseInit.class);
    private static boolean initialized = false;

    /**
     * Initialize Firebase using the service account key file
     */
    public static synchronized void initializeFirebase() {
        if (initialized) {
            return;
        }

        if (!FirebaseApp.getApps().isEmpty()) {
            logger.info("Firebase already initialized by another component");
            initialized = true;
            return;
        }

        try {
            // Extract the service account file to a temp file to avoid classpath loading issues
            File tempFile = File.createTempFile("firebase-service-account", ".json");
            tempFile.deleteOnExit();
            
            try (InputStream is = new ClassPathResource("google-services.json").getInputStream()) {
                Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            
            logger.info("Initializing Firebase from temp file: {}", tempFile.getAbsolutePath());
            
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(tempFile)))
                    .build();
            
            FirebaseApp.initializeApp(options);
            logger.info("Firebase initialized successfully");
            initialized = true;
        } catch (Exception e) {
            logger.error("Failed to initialize Firebase: {}", e.getMessage(), e);
            // Let the exception propagate - the application should fail to start if Firebase init fails
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
}
