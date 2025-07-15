package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.CashMemo;
import com.cosmicdoc.opdmanagement.model.FirestoreCashMemo;
import com.cosmicdoc.opdmanagement.model.LineItem;
import com.cosmicdoc.opdmanagement.model.Service;
import com.cosmicdoc.opdmanagement.service.ServiceManagementService;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class CashMemoRepositoryImpl implements CashMemoRepository {

    private final Firestore firestore;
    private final ServiceManagementService serviceManagementService;
    
    private static final Logger logger = LoggerFactory.getLogger(CashMemoRepositoryImpl.class);
    private static final String COLLECTION_NAME = "cash_memos";
    private static final String BILL_ID_PREFIX = "CM-";

    @Autowired
    public CashMemoRepositoryImpl(Firestore firestore, ServiceManagementService serviceManagementService) {
        this.firestore = firestore;
        this.serviceManagementService = serviceManagementService;
    }

    @Override
    public CashMemo save(CashMemo cashMemo) {
        try {
            // Generate bill id if not present
            if (cashMemo.getBillId() == null || cashMemo.getBillId().isEmpty()) {
                cashMemo.setBillId(generateBillId());
            }

            // Generate document ID if not present
            if (cashMemo.getId() == null || cashMemo.getId().isEmpty()) {
                cashMemo.setId(UUID.randomUUID().toString());
            }

            // Set default values for nullable fields if not provided
            Double overallDiscount = cashMemo.getOverallDiscount();
            if (overallDiscount == null) {
                cashMemo.setOverallDiscount(0.0);
            }
            
            Double totalTax = cashMemo.getTotalTax();
            if (totalTax == null) {
                cashMemo.setTotalTax(0.0);
            }
            
            // Ensure lists aren't null
            if (cashMemo.getLineItems() == null) {
                cashMemo.setLineItems(new ArrayList<>());
            } else {
                // Populate service names for line items if available
                populateServiceNames(cashMemo.getLineItems());
            }
            
            if (cashMemo.getTaxBreakdown() == null) {
                cashMemo.setTaxBreakdown(new ArrayList<>());
            }

            FirestoreCashMemo firestoreCashMemo = new FirestoreCashMemo(cashMemo);
            
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(cashMemo.getId());
            ApiFuture<WriteResult> result = docRef.set(firestoreCashMemo);
            
            // Wait for the write to complete
            result.get();
            
            // Also save in patient's billing history
            updatePatientBillingHistory(cashMemo);
            
            return cashMemo;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save cash memo: " + e.getMessage(), e);
        }
    }

    private void updatePatientBillingHistory(CashMemo cashMemo) {
        try {
            DocumentReference patientRef = firestore.collection("patients").document(cashMemo.getPatientId());
            
            // Create billing history entry
            FirestoreCashMemo firestoreCashMemo = new FirestoreCashMemo(cashMemo);
            
            // Add to patient's billing history subcollection
            patientRef.collection("billing_history").document(cashMemo.getId()).set(firestoreCashMemo).get();
        } catch (InterruptedException | ExecutionException e) {
            // Log but don't fail the primary operation
            System.err.println("Warning: Failed to update patient billing history: " + e.getMessage());
        }
    }

    /**
     * Populates service names for line items based on their serviceId
     * 
     * @param lineItems List of line items to populate service names for
     */
    private void populateServiceNames(List<LineItem> lineItems) {
        if (lineItems == null || lineItems.isEmpty()) {
            return;
        }
        
        // Create a map to store service IDs and their associated services
        Map<String, Service> serviceMap = new HashMap<>();
        
        // Process each line item
        for (LineItem item : lineItems) {
            if (item.getServiceId() != null && !item.getServiceId().isEmpty()) {
                try {
                    // If we haven't fetched this service yet, get it from the service management service
                    if (!serviceMap.containsKey(item.getServiceId())) {
                        Service service = serviceManagementService.getServiceById(item.getServiceId());
                        if (service != null) {
                            serviceMap.put(item.getServiceId(), service);
                        }
                    }
                    
                    // Get the service from our map
                    Service service = serviceMap.get(item.getServiceId());
                    
                    // Set the service name in the line item
                    if (service != null) {
                        item.setServiceName(service.getName());
                    } else if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                        // Fall back to description if service is not found
                        item.setServiceName(item.getDescription());
                    }
                } catch (Exception e) {
                    logger.error("Error fetching service details for line item with service ID: " + item.getServiceId(), e);
                    // Fall back to description if there's an error
                    if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                        item.setServiceName(item.getDescription());
                    }
                }
            } else if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                // If there's no service ID but there is a description, use that as service name
                item.setServiceName(item.getDescription());
            }
        }
    }

    @Override
    public List<CashMemo> findAll() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            return documents.stream()
                    .map(doc -> doc.toObject(FirestoreCashMemo.class).toCashMemo())
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve cash memos: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<CashMemo> findById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                FirestoreCashMemo firestoreCashMemo = document.toObject(FirestoreCashMemo.class);
                CashMemo cashMemo = firestoreCashMemo.toCashMemo();
                
                // Populate service names for line items if available
                if (cashMemo.getLineItems() != null && !cashMemo.getLineItems().isEmpty()) {
                    populateServiceNames(cashMemo.getLineItems());
                }
                
                return Optional.of(cashMemo);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error retrieving cash memo with ID: " + id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<CashMemo> findByPatientId(String patientId) {
        try {
            Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("patientId", patientId);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            List<CashMemo> result = new ArrayList<>();
            for (DocumentSnapshot document : documents) {
                FirestoreCashMemo firestoreCashMemo = document.toObject(FirestoreCashMemo.class);
                if (firestoreCashMemo != null) {
                    result.add(firestoreCashMemo.toCashMemo());
                }
            }
            
            return result;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve cash memos by patient ID: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            // First retrieve the cash memo to get patient ID
            Optional<CashMemo> cashMemoOpt = findById(id);
            if (cashMemoOpt.isPresent()) {
                CashMemo cashMemo = cashMemoOpt.get();
                
                // Delete from main collection
                firestore.collection(COLLECTION_NAME).document(id).delete().get();
                
                // Also delete from patient's billing history
                firestore.collection("patients").document(cashMemo.getPatientId())
                        .collection("billing_history").document(id).delete().get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to delete cash memo: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateBillId() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePart = today.format(formatter);
        
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        return BILL_ID_PREFIX + datePart + randomPart;
    }
}
