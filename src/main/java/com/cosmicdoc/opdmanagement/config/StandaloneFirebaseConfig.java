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
import java.io.FileNotFoundException;

/**
 * Standalone Firebase configuration that directly initializes Firebase
 * without depending on the common module's configuration.
 */
@Configuration
public class StandaloneFirebaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(StandaloneFirebaseConfig.class);

    @Bean
    @Primary
    public Firestore firestore() throws IOException {
        // Initialize Firebase only if it hasn't been initialized yet
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                // Try loading service account from the classpath
                logger.info("Initializing Firebase from classpath resource");
                InputStream serviceAccount = new ClassPathResource("google-services.json").getInputStream();
                
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                
                FirebaseApp.initializeApp(options);
                logger.info("Firebase initialized successfully");
            } catch (FileNotFoundException e) {
                logger.error("Firebase credentials file not found: {}", e.getMessage());
                throw e;
            } catch (IOException e) {
                logger.error("Error reading Firebase credentials: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                logger.error("Error initializing Firebase: {}", e.getMessage());
                throw e;
            }
        } else {
            logger.info("Firebase already initialized");
        }
        
        return FirestoreClient.getFirestore();
    }
}
