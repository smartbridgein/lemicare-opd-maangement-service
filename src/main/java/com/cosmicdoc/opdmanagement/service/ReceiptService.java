package com.cosmicdoc.opdmanagement.service;

import com.cosmicdoc.opdmanagement.model.Receipt;
import com.cosmicdoc.opdmanagement.repository.ReceiptRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Receipt operations
 */
@Service
@Slf4j
public class ReceiptService {

    private final ReceiptRepository receiptRepository;

    @Autowired
    public ReceiptService(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    /**
     * Create a new receipt
     *
     * @param receipt The receipt to create
     * @return The created receipt
     */
    public Receipt createReceipt(Receipt receipt) {
        log.info("Creating receipt for patient: {}", receipt.getPatientId());
        return receiptRepository.save(receipt);
    }

    /**
     * Retrieve all receipts
     *
     * @return List of all receipts
     */
    public List<Receipt> getAllReceipts() {
        log.info("Retrieving all receipts");
        return receiptRepository.findAll();
    }

    /**
     * Retrieve a receipt by ID
     *
     * @param id The receipt ID
     * @return Optional containing the receipt if found
     */
    public Optional<Receipt> getReceiptById(String id) {
        log.info("Retrieving receipt with ID: {}", id);
        return receiptRepository.findById(id);
    }

    /**
     * Retrieve receipts for a specific patient
     *
     * @param patientId The patient ID
     * @return List of receipts for the patient
     */
    public List<Receipt> getReceiptsByPatientId(String patientId) {
        log.info("Retrieving receipts for patient: {}", patientId);
        return receiptRepository.findByPatientId(patientId);
    }

    /**
     * Update an existing receipt
     *
     * @param id The receipt ID
     * @param updatedReceipt The updated receipt details
     * @return The updated receipt
     * @throws RuntimeException if the receipt is not found
     */
    public Receipt updateReceipt(String id, Receipt updatedReceipt) {
        log.info("Updating receipt with ID: {}", id);
        
        Receipt existingReceipt = receiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found with ID: " + id));
        
        // Update fields while preserving ID and receipt ID
        updatedReceipt.setId(existingReceipt.getId());
        if (updatedReceipt.getReceiptId() == null) {
            updatedReceipt.setReceiptId(existingReceipt.getReceiptId());
        }
        
        return receiptRepository.save(updatedReceipt);
    }

    /**
     * Delete a receipt
     *
     * @param id The receipt ID
     */
    public void deleteReceipt(String id) {
        log.info("Deleting receipt with ID: {}", id);
        receiptRepository.delete(id);
    }
}
