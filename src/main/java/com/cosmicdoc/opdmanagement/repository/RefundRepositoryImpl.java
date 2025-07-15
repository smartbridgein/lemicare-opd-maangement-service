package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.Refund;
import com.cosmicdoc.opdmanagement.model.FirestoreRefund;
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
public class RefundRepositoryImpl implements RefundRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "refunds";
    private static final String REFUND_ID_PREFIX = "REF-";

    @Autowired
    public RefundRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public Refund save(Refund refund) {
        try {
            // Generate refund id if not present
            if (refund.getRefundId() == null || refund.getRefundId().isEmpty()) {
                refund.setRefundId(generateRefundId());
            }

            // Generate document ID if not present
            if (refund.getId() == null || refund.getId().isEmpty()) {
                refund.setId(UUID.randomUUID().toString());
            }

            FirestoreRefund firestoreRefund = new FirestoreRefund(refund);
            
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(refund.getId());
            ApiFuture<WriteResult> result = docRef.set(firestoreRefund);
            
            // Wait for the write to complete
            result.get();
            
            // Also save in patient's billing history
            updatePatientBillingHistory(refund);
            
            return refund;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save refund: " + e.getMessage(), e);
        }
    }

    private void updatePatientBillingHistory(Refund refund) {
        try {
            DocumentReference patientRef = firestore.collection("patients").document(refund.getPatientId());
            
            // Create billing history entry
            FirestoreRefund firestoreRefund = new FirestoreRefund(refund);
            
            // Add to patient's billing history subcollection
            patientRef.collection("billing_history").document(refund.getId()).set(firestoreRefund).get();
        } catch (InterruptedException | ExecutionException e) {
            // Log but don't fail the primary operation
            System.err.println("Warning: Failed to update patient billing history: " + e.getMessage());
        }
    }

    @Override
    public List<Refund> findAll() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            return documents.stream()
                    .map(doc -> doc.toObject(FirestoreRefund.class).toRefund())
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve refunds: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Refund> findById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                FirestoreRefund firestoreRefund = document.toObject(FirestoreRefund.class);
                return Optional.ofNullable(firestoreRefund.toRefund());
            }
            
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve refund by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Refund> findByPatientId(String patientId) {
        try {
            Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("patientId", patientId);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            List<Refund> result = new ArrayList<>();
            for (DocumentSnapshot document : documents) {
                FirestoreRefund firestoreRefund = document.toObject(FirestoreRefund.class);
                if (firestoreRefund != null) {
                    result.add(firestoreRefund.toRefund());
                }
            }
            
            return result;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve refunds by patient ID: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            // First retrieve the refund to get patient ID
            Optional<Refund> refundOpt = findById(id);
            if (refundOpt.isPresent()) {
                Refund refund = refundOpt.get();
                
                // Delete from main collection
                firestore.collection(COLLECTION_NAME).document(id).delete().get();
                
                // Also delete from patient's billing history
                firestore.collection("patients").document(refund.getPatientId())
                        .collection("billing_history").document(id).delete().get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to delete refund: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateRefundId() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePart = today.format(formatter);
        
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        return REFUND_ID_PREFIX + datePart + randomPart;
    }
}
