package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.FirestoreVisitHistory;
import com.cosmicdoc.opdmanagement.model.VisitHistory;
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
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Repository
@Primary
public class VisitHistoryRepositoryImpl implements VisitHistoryRepository {

    private static final Logger logger = LoggerFactory.getLogger(VisitHistoryRepositoryImpl.class);
    private static final String COLLECTION_NAME = "visit_histories";

    private final Firestore firestore;

    @Autowired
    public VisitHistoryRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
        logger.info("Initialized VisitHistoryRepositoryImpl with direct Firestore access");
    }

    @Override
    public Optional<VisitHistory> findById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                FirestoreVisitHistory firestoreVisitHistory = document.toObject(FirestoreVisitHistory.class);
                return Optional.ofNullable(firestoreVisitHistory).map(FirestoreVisitHistory::toVisitHistory);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error retrieving visit history with ID {}: {}", id, e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return Optional.empty();
    }

    @Override
    public List<VisitHistory> findByPatientId(String patientId) {
        List<VisitHistory> visitHistories = new ArrayList<>();
        try {
            Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("patientId", patientId);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            for (QueryDocumentSnapshot document : documents) {
                FirestoreVisitHistory firestoreVisitHistory = document.toObject(FirestoreVisitHistory.class);
                visitHistories.add(firestoreVisitHistory.toVisitHistory());
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error retrieving visit histories for patient ID {}: {}", patientId, e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return visitHistories;
    }

    @Override
    public VisitHistory save(VisitHistory visitHistory) {
        try {
            FirestoreVisitHistory firestoreVisitHistory = FirestoreVisitHistory.fromVisitHistory(visitHistory);
            
            // Generate ID if not present
            if (firestoreVisitHistory.getId() == null || firestoreVisitHistory.getId().isEmpty()) {
                firestoreVisitHistory.setId(UUID.randomUUID().toString());
                visitHistory.setId(firestoreVisitHistory.getId());
            }
            
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(firestoreVisitHistory.getId());
            ApiFuture<WriteResult> result = docRef.set(firestoreVisitHistory);
            
            // Wait for the operation to complete
            result.get();
            logger.info("Visit history saved with ID: {}", firestoreVisitHistory.getId());
            
            return visitHistory;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error saving visit history: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to save visit history", e);
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<WriteResult> result = docRef.delete();
            result.get();
            logger.info("Visit history with ID {} deleted successfully", id);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error deleting visit history with ID {}: {}", id, e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to delete visit history", e);
        }
    }

    @Override
    public void deleteByPatientId(String patientId) {
        try {
            // First find all documents with the given patientId
            Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("patientId", patientId);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            // Delete each matching document
            for (QueryDocumentSnapshot document : documents) {
                ApiFuture<WriteResult> result = document.getReference().delete();
                result.get();
                logger.info("Visit history for patient ID {} deleted successfully", patientId);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error deleting visit histories for patient ID {}: {}", patientId, e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to delete visit histories", e);
        }
    }
}
