package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.Invoice;
import com.cosmicdoc.opdmanagement.model.FirestoreInvoice;
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
public class InvoiceRepositoryImpl implements InvoiceRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "invoices";
    private static final String INVOICE_ID_PREFIX = "INV-";

    @Autowired
    public InvoiceRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public Invoice save(Invoice invoice) {
        try {
            // Generate invoice id if not present
            if (invoice.getInvoiceId() == null || invoice.getInvoiceId().isEmpty()) {
                invoice.setInvoiceId(generateInvoiceId());
            }

            // Generate document ID if not present
            if (invoice.getId() == null || invoice.getId().isEmpty()) {
                invoice.setId(UUID.randomUUID().toString());
            }

            FirestoreInvoice firestoreInvoice = new FirestoreInvoice(invoice);
            
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(invoice.getId());
            ApiFuture<WriteResult> result = docRef.set(firestoreInvoice);
            
            // Wait for the write to complete
            result.get();
            
            // Also save in patient's billing history
            updatePatientBillingHistory(invoice);
            
            return invoice;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save invoice: " + e.getMessage(), e);
        }
    }

    private void updatePatientBillingHistory(Invoice invoice) {
        try {
            DocumentReference patientRef = firestore.collection("patients").document(invoice.getPatientId());
            
            // Create billing history entry
            FirestoreInvoice firestoreInvoice = new FirestoreInvoice(invoice);
            
            // Add to patient's billing history subcollection
            patientRef.collection("billing_history").document(invoice.getId()).set(firestoreInvoice).get();
        } catch (InterruptedException | ExecutionException e) {
            // Log but don't fail the primary operation
            System.err.println("Warning: Failed to update patient billing history: " + e.getMessage());
        }
    }

    @Override
    public List<Invoice> findAll() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            return documents.stream()
                    .map(doc -> doc.toObject(FirestoreInvoice.class).toInvoice())
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve invoices: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Invoice> findById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                FirestoreInvoice firestoreInvoice = document.toObject(FirestoreInvoice.class);
                return Optional.ofNullable(firestoreInvoice.toInvoice());
            }
            
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve invoice by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Invoice> findByPatientId(String patientId) {
        try {
            Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("patientId", patientId);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            List<Invoice> result = new ArrayList<>();
            for (DocumentSnapshot document : documents) {
                FirestoreInvoice firestoreInvoice = document.toObject(FirestoreInvoice.class);
                if (firestoreInvoice != null) {
                    result.add(firestoreInvoice.toInvoice());
                }
            }
            
            return result;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve invoices by patient ID: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            // First retrieve the invoice to get patient ID
            Optional<Invoice> invoiceOpt = findById(id);
            if (invoiceOpt.isPresent()) {
                Invoice invoice = invoiceOpt.get();
                
                // Delete from main collection
                firestore.collection(COLLECTION_NAME).document(id).delete().get();
                
                // Also delete from patient's billing history
                firestore.collection("patients").document(invoice.getPatientId())
                        .collection("billing_history").document(id).delete().get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to delete invoice: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateInvoiceId() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePart = today.format(formatter);
        
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        return INVOICE_ID_PREFIX + datePart + randomPart;
    }
}
