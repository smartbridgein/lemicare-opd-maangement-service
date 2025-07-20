package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.Receipt;
import com.cosmicdoc.opdmanagement.model.FirestoreReceipt;
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
public class ReceiptRepositoryImpl implements ReceiptRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "receipts";
    private static final String RECEIPT_ID_PREFIX = "REC-";

    @Autowired
    public ReceiptRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public Receipt save(Receipt receipt) {
        try {
            // Generate receipt id if not present
            if (receipt.getReceiptId() == null || receipt.getReceiptId().isEmpty()) {
                receipt.setReceiptId(generateReceiptId());
            }

            // Generate document ID if not present
            if (receipt.getId() == null || receipt.getId().isEmpty()) {
                receipt.setId(UUID.randomUUID().toString());
            }

            FirestoreReceipt firestoreReceipt = new FirestoreReceipt(receipt);
            
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(receipt.getId());
            ApiFuture<WriteResult> result = docRef.set(firestoreReceipt);
            
            // Wait for the write to complete
            result.get();
            
            // Also save in patient's billing history
            updatePatientBillingHistory(receipt);
            
            return receipt;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save receipt: " + e.getMessage(), e);
        }
    }

    private void updatePatientBillingHistory(Receipt receipt) {
        try {
            DocumentReference patientRef = firestore.collection("patients").document(receipt.getPatientId());
            
            // Create billing history entry
            FirestoreReceipt firestoreReceipt = new FirestoreReceipt(receipt);
            
            // Add to patient's billing history subcollection
            patientRef.collection("billing_history").document(receipt.getId()).set(firestoreReceipt).get();
        } catch (InterruptedException | ExecutionException e) {
            // Log but don't fail the primary operation
            System.err.println("Warning: Failed to update patient billing history: " + e.getMessage());
        }
    }

    @Override
    public List<Receipt> findAll() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            return documents.stream()
                    .map(doc -> doc.toObject(FirestoreReceipt.class).toReceipt())
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve receipts: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Receipt> findById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                FirestoreReceipt firestoreReceipt = document.toObject(FirestoreReceipt.class);
                return Optional.ofNullable(firestoreReceipt.toReceipt());
            }
            
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve receipt by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Receipt> findByPatientId(String patientId) {
        try {
            Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("patientId", patientId);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            List<Receipt> result = new ArrayList<>();
            for (DocumentSnapshot document : documents) {
                FirestoreReceipt firestoreReceipt = document.toObject(FirestoreReceipt.class);
                if (firestoreReceipt != null) {
                    result.add(firestoreReceipt.toReceipt());
                }
            }
            
            return result;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve receipts by patient ID: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            // First retrieve the receipt to get patient ID
            Optional<Receipt> receiptOpt = findById(id);
            if (receiptOpt.isPresent()) {
                Receipt receipt = receiptOpt.get();
                
                // Delete from main collection
                firestore.collection(COLLECTION_NAME).document(id).delete().get();
                
                // Also delete from patient's billing history
                firestore.collection("patients").document(receipt.getPatientId())
                        .collection("billing_history").document(id).delete().get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to delete receipt: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateReceiptId() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePart = today.format(formatter);
        
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        return RECEIPT_ID_PREFIX + datePart + randomPart;
    }
}
