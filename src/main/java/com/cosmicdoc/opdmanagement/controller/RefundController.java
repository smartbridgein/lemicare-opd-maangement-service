package com.cosmicdoc.opdmanagement.controller;

import com.cosmicdoc.opdmanagement.model.ApiResponse;
import com.cosmicdoc.opdmanagement.model.Refund;
import com.cosmicdoc.opdmanagement.service.RefundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Refund operations
 */
@RestController
@RequestMapping("/api/billing/refund")
@Slf4j
public class RefundController {

    private final RefundService refundService;

    @Autowired
    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    /**
     * Create a new refund
     *
     * @param refund The refund to create
     * @return ResponseEntity with the created refund
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Refund>> createRefund(@RequestBody Refund refund) {
        log.info("Received request to create refund for patient: {}", refund.getPatientId());
        Refund createdRefund = refundService.createRefund(refund);
        return ResponseEntity.ok(ApiResponse.success("Refund created successfully", createdRefund));
    }

    /**
     * Get all refunds
     *
     * @return ResponseEntity with list of all refunds
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Refund>>> getAllRefunds() {
        log.info("Received request to get all refunds");
        List<Refund> refunds = refundService.getAllRefunds();
        return ResponseEntity.ok(ApiResponse.success("Refunds retrieved successfully", refunds));
    }

    /**
     * Get a refund by ID
     *
     * @param id The refund ID
     * @return ResponseEntity with the refund if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Refund>> getRefundById(@PathVariable String id) {
        log.info("Received request to get refund with ID: {}", id);
        return refundService.getRefundById(id)
                .map(refund -> ResponseEntity.ok(ApiResponse.success("Refund retrieved successfully", refund)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get refunds by patient ID
     *
     * @param patientId The patient ID
     * @return ResponseEntity with list of refunds for the patient
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<Refund>>> getRefundsByPatientId(@PathVariable String patientId) {
        log.info("Received request to get refunds for patient: {}", patientId);
        List<Refund> refunds = refundService.getRefundsByPatientId(patientId);
        return ResponseEntity.ok(ApiResponse.success("Refunds retrieved successfully", refunds));
    }

    /**
     * Update an existing refund
     *
     * @param id The refund ID
     * @param refund The updated refund details
     * @return ResponseEntity with the updated refund
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Refund>> updateRefund(@PathVariable String id, @RequestBody Refund refund) {
        log.info("Received request to update refund with ID: {}", id);
        try {
            Refund updatedRefund = refundService.updateRefund(id, refund);
            return ResponseEntity.ok(ApiResponse.success("Refund updated successfully", updatedRefund));
        } catch (RuntimeException e) {
            log.error("Failed to update refund: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a refund
     *
     * @param id The refund ID
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRefund(@PathVariable String id) {
        log.info("Received request to delete refund with ID: {}", id);
        refundService.deleteRefund(id);
        return ResponseEntity.ok(ApiResponse.success("Refund deleted successfully", null));
    }
}
