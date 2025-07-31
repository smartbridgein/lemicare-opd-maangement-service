package com.cosmicdoc.opdmanagement.controller;

import com.cosmicdoc.opdmanagement.model.ApiResponse;
import com.cosmicdoc.opdmanagement.model.Advance;
import com.cosmicdoc.opdmanagement.service.AdvanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Advance payment operations
 */
@RestController
@RequestMapping("/api/billing/advance")
@Slf4j
public class AdvanceController {

    private final AdvanceService advanceService;

    @Autowired
    public AdvanceController(AdvanceService advanceService) {
        this.advanceService = advanceService;
    }

    /**
     * Create a new advance payment
     *
     * @param advance The advance to create
     * @return ResponseEntity with the created advance
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Advance>> createAdvance(@RequestBody Advance advance) {
        log.info("Received request to create advance payment for patient: {}", advance.getPatientId());
        Advance createdAdvance = advanceService.createAdvance(advance);
        return ResponseEntity.ok(ApiResponse.success("Advance payment created successfully", createdAdvance));
    }

    /**
     * Get all advances
     *
     * @return ResponseEntity with list of all advances
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Advance>>> getAllAdvances() {
        log.info("Received request to get all advance payments");
        List<Advance> advances = advanceService.getAllAdvances();
        return ResponseEntity.ok(ApiResponse.success("Advance payments retrieved successfully", advances));
    }

    /**
     * Get an advance by ID
     *
     * @param id The advance ID
     * @return ResponseEntity with the advance if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Advance>> getAdvanceById(@PathVariable String id) {
        log.info("Received request to get advance payment with ID: {}", id);
        return advanceService.getAdvanceById(id)
                .map(advance -> ResponseEntity.ok(ApiResponse.success("Advance payment retrieved successfully", advance)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get advances by patient ID
     *
     * @param patientId The patient ID
     * @return ResponseEntity with list of advances for the patient
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<Advance>>> getAdvancesByPatientId(@PathVariable String patientId) {
        log.info("Received request to get advance payments for patient: {}", patientId);
        List<Advance> advances = advanceService.getAdvancesByPatientId(patientId);
        return ResponseEntity.ok(ApiResponse.success("Advance payments retrieved successfully", advances));
    }

    /**
     * Update an existing advance
     *
     * @param id The advance ID
     * @param advance The updated advance details
     * @return ResponseEntity with the updated advance
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Advance>> updateAdvance(@PathVariable String id, @RequestBody Advance advance) {
        log.info("Received request to update advance payment with ID: {}", id);
        try {
            Advance updatedAdvance = advanceService.updateAdvance(id, advance);
            return ResponseEntity.ok(ApiResponse.success("Advance payment updated successfully", updatedAdvance));
        } catch (RuntimeException e) {
            log.error("Failed to update advance payment: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete an advance
     *
     * @param id The advance ID
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAdvance(@PathVariable String id) {
        log.info("Received request to delete advance payment with ID: {}", id);
        advanceService.deleteAdvance(id);
        return ResponseEntity.ok(ApiResponse.success("Advance payment deleted successfully", null));
    }
}
