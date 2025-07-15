package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.Patient;
import com.cosmicdoc.opdmanagement.repository.PatientRepository;
import com.cosmicdoc.opdmanagement.model.FirestorePatient;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
@Primary
public class OpdPatientRepositoryImpl implements PatientRepository {
    private static final Logger logger = LoggerFactory.getLogger(OpdPatientRepositoryImpl.class);
    private static final String COLLECTION_NAME = "patients";

    private final Firestore firestore;

    @Autowired
    public OpdPatientRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
        logger.info("Initialized OpdPatientRepositoryImpl with direct Firestore access");
    }

    @Override
    public List<Patient> findAll() {
        List<Patient> patients = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            for (QueryDocumentSnapshot document : documents) {
                FirestorePatient firestorePatient = document.toObject(FirestorePatient.class);
                patients.add(firestorePatient.toPatient());
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error retrieving all patients: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return patients;
    }

    @Override
    public Optional<Patient> findById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                FirestorePatient firestorePatient = document.toObject(FirestorePatient.class);
                return Optional.ofNullable(firestorePatient).map(FirestorePatient::toPatient);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error retrieving patient with ID {}: {}", id, e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Patient> findByPhoneNumber(String phoneNumber) {
        try {
            Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("phoneNumber", phoneNumber);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            if (!documents.isEmpty()) {
                FirestorePatient firestorePatient = documents.get(0).toObject(FirestorePatient.class);
                return Optional.ofNullable(firestorePatient).map(FirestorePatient::toPatient);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error retrieving patient with phone number {}: {}", phoneNumber, e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<Patient> findByEmail(String email) {
        try {
            Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("email", email);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            if (!documents.isEmpty()) {
                FirestorePatient firestorePatient = documents.get(0).toObject(FirestorePatient.class);
                return Optional.ofNullable(firestorePatient).map(FirestorePatient::toPatient);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error retrieving patient with email {}: {}", email, e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return null;
    }

    @Override
    public Patient save(Patient patient) {
        try {
            // Convert Patient to FirestorePatient
            FirestorePatient firestorePatient = FirestorePatient.fromPatient(patient);
            
            DocumentReference docRef;
            if (firestorePatient.getId() != null && !firestorePatient.getId().isEmpty()) {
                // Use existing ID (for updates)
                docRef = firestore.collection(COLLECTION_NAME).document(firestorePatient.getId());
                logger.info("Updating existing patient with ID: {}", firestorePatient.getId());
            } else {
                // For a new patient, we'll use the patient ID as the document ID
                // The actual ID generation happens in PatientService
                // If somehow we get here without an ID, use a default pattern
                String patientId = patient.getId();
                if (patientId == null || patientId.isEmpty()) {
                    patientId = generateFallbackPatientId();
                    patient.setId(patientId);
                    firestorePatient.setId(patientId);
                    logger.warn("Patient had no ID, generated fallback ID: {}", patientId);
                }
                
                docRef = firestore.collection(COLLECTION_NAME).document(patientId);
                logger.info("Creating new patient with ID: {}", patientId);
            }
            
            ApiFuture<WriteResult> result = docRef.set(firestorePatient);
            result.get(); // Wait for the write to complete
            
            return patient;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error saving patient: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to save patient", e);
        }
    }
    
    /**
     * Generate a fallback patient ID if none was provided by the service layer
     * This is just a safety measure - normally IDs should be generated by PatientService
     */
    private String generateFallbackPatientId() {
        String year = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy"));
        
        try {
            // Find highest existing ID
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("id", "PAT-" + year)
                .whereLessThan("id", "PAT-" + (Integer.parseInt(year) + 1))
                .orderBy("id", Query.Direction.DESCENDING)
                .limit(1)
                .get();
                
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            int sequence = 1; // Default start
            
            if (!documents.isEmpty()) {
                String lastId = documents.get(0).getString("id");
                if (lastId != null && lastId.matches("PAT-\\d{4}-\\d{4}")) {
                    String seqStr = lastId.substring(lastId.lastIndexOf('-') + 1);
                    sequence = Integer.parseInt(seqStr) + 1;
                }
            }
            
            return String.format("PAT-%s-%04d", year, sequence);
            
        } catch (Exception e) {
            logger.error("Error generating fallback patient ID: {}", e.getMessage());
            // Last resort fallback with timestamp to ensure uniqueness
            return String.format("PAT-%s-%04d", year, (int) (System.currentTimeMillis() % 10000));
        }
    }


    @Override
    public void deleteById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<WriteResult> writeResult = docRef.delete();
            writeResult.get(); // Wait for deletion to complete
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error deleting patient with ID {}: {}", id, e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to delete patient", e);
        }
    }

    @Override
    public boolean existsById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            return document.exists();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error checking if patient exists with ID {}: {}", id, e.getMessage(), e);
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
