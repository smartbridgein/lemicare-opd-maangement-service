package com.cosmicdoc.opdmanagement.controller;

import com.cosmicdoc.opdmanagement.model.ApiResponse;
import com.cosmicdoc.opdmanagement.model.Receipt;
import com.cosmicdoc.opdmanagement.service.ReceiptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Receipt operations
 */
@RestController
@RequestMapping("/api/billing/receipt")
@Slf4j
public class ReceiptController {

    private final ReceiptService receiptService;

    @Autowired
    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    /**
     * Create a new receipt
     *
     * @param receipt The receipt to create
     * @return ResponseEntity with the created receipt
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Receipt>> createReceipt(@RequestBody Receipt receipt) {
        log.info("Received request to create receipt for patient: {}", receipt.getPatientId());
        Receipt createdReceipt = receiptService.createReceipt(receipt);
        return ResponseEntity.ok(ApiResponse.success("Receipt created successfully", createdReceipt));
    }

    /**
     * Get all receipts
     *
     * @return ResponseEntity with list of all receipts
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Receipt>>> getAllReceipts() {
        log.info("Received request to get all receipts");
        List<Receipt> receipts = receiptService.getAllReceipts();
        return ResponseEntity.ok(ApiResponse.success("Receipts retrieved successfully", receipts));
    }

    /**
     * Get a receipt by ID
     *
     * @param id The receipt ID
     * @return ResponseEntity with the receipt if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Receipt>> getReceiptById(@PathVariable String id) {
        log.info("Received request to get receipt with ID: {}", id);
        return receiptService.getReceiptById(id)
                .map(receipt -> ResponseEntity.ok(ApiResponse.success("Receipt retrieved successfully", receipt)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get receipts by patient ID
     *
     * @param patientId The patient ID
     * @return ResponseEntity with list of receipts for the patient
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<Receipt>>> getReceiptsByPatientId(@PathVariable String patientId) {
        log.info("Received request to get receipts for patient: {}", patientId);
        List<Receipt> receipts = receiptService.getReceiptsByPatientId(patientId);
        return ResponseEntity.ok(ApiResponse.success("Receipts retrieved successfully", receipts));
    }

    /**
     * Update an existing receipt
     *
     * @param id The receipt ID
     * @param receipt The updated receipt details
     * @return ResponseEntity with the updated receipt
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Receipt>> updateReceipt(@PathVariable String id, @RequestBody Receipt receipt) {
        log.info("Received request to update receipt with ID: {}", id);
        try {
            Receipt updatedReceipt = receiptService.updateReceipt(id, receipt);
            return ResponseEntity.ok(ApiResponse.success("Receipt updated successfully", updatedReceipt));
        } catch (RuntimeException e) {
            log.error("Failed to update receipt: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a receipt
     *
     * @param id The receipt ID
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReceipt(@PathVariable String id) {
        log.info("Received request to delete receipt with ID: {}", id);
        receiptService.deleteReceipt(id);
        return ResponseEntity.ok(ApiResponse.success("Receipt deleted successfully", null));
    }
}
