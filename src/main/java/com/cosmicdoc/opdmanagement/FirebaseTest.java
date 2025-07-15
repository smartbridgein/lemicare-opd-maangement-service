package com.cosmicdoc.opdmanagement;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.ClassPathResource;

/**
 * Simple test class to verify Firebase initialization works.
 */
public class FirebaseTest {
    public static void main(String[] args) {
        try {
            System.out.println("Attempting to initialize Firebase...");
            
            InputStream serviceAccount = new ClassPathResource("google-services.json").getInputStream();
            System.out.println("Found credentials file, loading...");
            
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            
            FirebaseApp.initializeApp(options);
            System.out.println("Firebase initialized successfully!");
            
            Firestore firestore = FirestoreClient.getFirestore();
            System.out.println("Firestore instance created.");
            
            System.out.println("Test complete!");
        } catch (Exception e) {
            System.err.println("Error initializing Firebase:");
            e.printStackTrace();
        }
    }
}
