package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.Advance;
import com.cosmicdoc.opdmanagement.model.FirestoreAdvance;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class AdvanceRepositoryImpl implements AdvanceRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "advances";
    private static final String ADVANCE_ID_PREFIX = "ADV-";

    @Autowired
    public AdvanceRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public Advance save(Advance advance) {
        try {
            // Generate advance id if not present
            if (advance.getAdvanceId() == null || advance.getAdvanceId().isEmpty()) {
                advance.setAdvanceId(generateAdvanceId());
            }

            // Generate document ID if not present
            if (advance.getId() == null || advance.getId().isEmpty()) {
                advance.setId(UUID.randomUUID().toString());
            }

            FirestoreAdvance firestoreAdvance = new FirestoreAdvance(advance);
            
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(advance.getId());
            ApiFuture<WriteResult> result = docRef.set(firestoreAdvance);
            
            // Wait for the write to complete
            result.get();
            
            // Also save in patient's billing history
            updatePatientBillingHistory(advance);
            
            return advance;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save advance: " + e.getMessage(), e);
        }
    }

    private void updatePatientBillingHistory(Advance advance) {
        try {
            DocumentReference patientRef = firestore.collection("patients").document(advance.getPatientId());
            
            // Create billing history entry
            FirestoreAdvance firestoreAdvance = new FirestoreAdvance(advance);
            
            // Add to patient's billing history subcollection
            patientRef.collection("billing_history").document(advance.getId()).set(firestoreAdvance).get();
        } catch (InterruptedException | ExecutionException e) {
            // Log but don't fail the primary operation
            System.err.println("Warning: Failed to update patient billing history: " + e.getMessage());
        }
    }

    @Override
    public List<Advance> findAll() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            return documents.stream()
                    .map(doc -> doc.toObject(FirestoreAdvance.class).toAdvance())
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve advances: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Advance> findById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                FirestoreAdvance firestoreAdvance = document.toObject(FirestoreAdvance.class);
                return Optional.ofNullable(firestoreAdvance.toAdvance());
            }
            
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve advance by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Advance> findByPatientId(String patientId) {
        try {
            Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("patientId", patientId);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            List<Advance> result = new ArrayList<>();
            for (DocumentSnapshot document : documents) {
                FirestoreAdvance firestoreAdvance = document.toObject(FirestoreAdvance.class);
                if (firestoreAdvance != null) {
                    result.add(firestoreAdvance.toAdvance());
                }
            }
            
            return result;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve advances by patient ID: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            // First retrieve the advance to get patient ID
            Optional<Advance> advanceOpt = findById(id);
            if (advanceOpt.isPresent()) {
                Advance advance = advanceOpt.get();
                
                // Delete from main collection
                firestore.collection(COLLECTION_NAME).document(id).delete().get();
                
                // Also delete from patient's billing history
                firestore.collection("patients").document(advance.getPatientId())
                        .collection("billing_history").document(id).delete().get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to delete advance: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateAdvanceId() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePart = today.format(formatter);
        
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        return ADVANCE_ID_PREFIX + datePart + randomPart;
    }
}
