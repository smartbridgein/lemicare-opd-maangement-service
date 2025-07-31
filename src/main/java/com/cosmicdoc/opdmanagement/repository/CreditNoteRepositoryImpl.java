package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.CreditNote;
import com.cosmicdoc.opdmanagement.model.FirestoreCreditNote;
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
public class CreditNoteRepositoryImpl implements CreditNoteRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "credit_notes";
    private static final String CREDIT_NOTE_ID_PREFIX = "CN-";

    @Autowired
    public CreditNoteRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public CreditNote save(CreditNote creditNote) {
        try {
            // Generate credit note id if not present
            if (creditNote.getCreditNoteId() == null || creditNote.getCreditNoteId().isEmpty()) {
                creditNote.setCreditNoteId(generateCreditNoteId());
            }

            // Generate document ID if not present
            if (creditNote.getId() == null || creditNote.getId().isEmpty()) {
                creditNote.setId(UUID.randomUUID().toString());
            }

            FirestoreCreditNote firestoreCreditNote = new FirestoreCreditNote(creditNote);
            
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(creditNote.getId());
            ApiFuture<WriteResult> result = docRef.set(firestoreCreditNote);
            
            // Wait for the write to complete
            result.get();
            
            // Also save in patient's billing history
            updatePatientBillingHistory(creditNote);
            
            return creditNote;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save credit note: " + e.getMessage(), e);
        }
    }

    private void updatePatientBillingHistory(CreditNote creditNote) {
        try {
            DocumentReference patientRef = firestore.collection("patients").document(creditNote.getPatientId());
            
            // Create billing history entry
            FirestoreCreditNote firestoreCreditNote = new FirestoreCreditNote(creditNote);
            
            // Add to patient's billing history subcollection
            patientRef.collection("billing_history").document(creditNote.getId()).set(firestoreCreditNote).get();
        } catch (InterruptedException | ExecutionException e) {
            // Log but don't fail the primary operation
            System.err.println("Warning: Failed to update patient billing history: " + e.getMessage());
        }
    }

    @Override
    public List<CreditNote> findAll() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            return documents.stream()
                    .map(doc -> doc.toObject(FirestoreCreditNote.class).toCreditNote())
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve credit notes: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<CreditNote> findById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                FirestoreCreditNote firestoreCreditNote = document.toObject(FirestoreCreditNote.class);
                return Optional.ofNullable(firestoreCreditNote.toCreditNote());
            }
            
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve credit note by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CreditNote> findByPatientId(String patientId) {
        try {
            Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("patientId", patientId);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            List<CreditNote> result = new ArrayList<>();
            for (DocumentSnapshot document : documents) {
                FirestoreCreditNote firestoreCreditNote = document.toObject(FirestoreCreditNote.class);
                if (firestoreCreditNote != null) {
                    result.add(firestoreCreditNote.toCreditNote());
                }
            }
            
            return result;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve credit notes by patient ID: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            // First retrieve the credit note to get patient ID
            Optional<CreditNote> creditNoteOpt = findById(id);
            if (creditNoteOpt.isPresent()) {
                CreditNote creditNote = creditNoteOpt.get();
                
                // Delete from main collection
                firestore.collection(COLLECTION_NAME).document(id).delete().get();
                
                // Also delete from patient's billing history
                firestore.collection("patients").document(creditNote.getPatientId())
                        .collection("billing_history").document(id).delete().get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to delete credit note: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateCreditNoteId() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePart = today.format(formatter);
        
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        return CREDIT_NOTE_ID_PREFIX + datePart + randomPart;
    }
}
