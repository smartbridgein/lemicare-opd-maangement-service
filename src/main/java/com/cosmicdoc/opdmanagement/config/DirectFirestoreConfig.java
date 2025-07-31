package com.cosmicdoc.opdmanagement.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.File;

/**
 * Direct Firestore configuration that uses the service account key file directly,
 * bypassing the common module's JSON parsing mechanisms.
 */
@Configuration
public class DirectFirestoreConfig {
    private static final Logger logger = LoggerFactory.getLogger(DirectFirestoreConfig.class);

    @Bean
    @Primary
    public Firestore firestore() throws IOException {
        // Initialize Firebase only if it hasn't been initialized yet
        if (FirebaseApp.getApps().isEmpty()) {
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
                logger.info("Firebase initialized successfully using direct approach");
            } catch (Exception e) {
                logger.error("Error initializing Firebase: {}", e.getMessage(), e);
                throw e;
            }
        } else {
            logger.info("Firebase already initialized");
        }
        
        return FirestoreClient.getFirestore();
    }
}
