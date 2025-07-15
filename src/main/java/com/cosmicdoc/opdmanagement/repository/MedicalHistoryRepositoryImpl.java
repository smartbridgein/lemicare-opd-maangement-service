package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.FirestoreMedicalHistory;
import com.cosmicdoc.opdmanagement.model.MedicalHistory;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Repository
@Primary
public class MedicalHistoryRepositoryImpl implements MedicalHistoryRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(MedicalHistoryRepositoryImpl.class);
    private static final String COLLECTION_NAME = "medical_histories";

    private final Firestore firestore;

    @Autowired
    public MedicalHistoryRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
        logger.info("Initialized MedicalHistoryRepositoryImpl with direct Firestore access");
    }

    @Override
    public Optional<MedicalHistory> findById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                FirestoreMedicalHistory firestoreMedicalHistory = document.toObject(FirestoreMedicalHistory.class);
                return Optional.ofNullable(firestoreMedicalHistory).map(FirestoreMedicalHistory::toMedicalHistory);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error retrieving medical history with ID {}: {}", id, e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return Optional.empty();
    }

    @Override
    public Optional<MedicalHistory> findByPatientId(String patientId) {
        try {
            Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("patientId", patientId);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            if (!documents.isEmpty()) {
                FirestoreMedicalHistory firestoreMedicalHistory = documents.get(0).toObject(FirestoreMedicalHistory.class);
                return Optional.ofNullable(firestoreMedicalHistory).map(FirestoreMedicalHistory::toMedicalHistory);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error retrieving medical history for patient ID {}: {}", patientId, e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return Optional.empty();
    }

    @Override
    public MedicalHistory save(MedicalHistory medicalHistory) {
        try {
            FirestoreMedicalHistory firestoreMedicalHistory = FirestoreMedicalHistory.fromMedicalHistory(medicalHistory);
            
            // Generate ID if not present
            if (firestoreMedicalHistory.getId() == null || firestoreMedicalHistory.getId().isEmpty()) {
                firestoreMedicalHistory.setId(UUID.randomUUID().toString());
                medicalHistory.setId(firestoreMedicalHistory.getId());
            }
            
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(firestoreMedicalHistory.getId());
            ApiFuture<WriteResult> result = docRef.set(firestoreMedicalHistory);
            
            // Wait for the operation to complete
            result.get();
            logger.info("Medical history saved with ID: {}", firestoreMedicalHistory.getId());
            
            return medicalHistory;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error saving medical history: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to save medical history", e);
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<WriteResult> result = docRef.delete();
            result.get();
            logger.info("Medical history with ID {} deleted successfully", id);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error deleting medical history with ID {}: {}", id, e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to delete medical history", e);
        }
    }

    @Override
    public void deleteByPatientId(String patientId) {
        try {
            // First find the document with the given patientId
            Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("patientId", patientId);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            // Delete each matching document
            for (QueryDocumentSnapshot document : documents) {
                ApiFuture<WriteResult> result = document.getReference().delete();
                result.get();
                logger.info("Medical history for patient ID {} deleted successfully", patientId);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error deleting medical history for patient ID {}: {}", patientId, e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to delete medical history", e);
        }
    }
}
