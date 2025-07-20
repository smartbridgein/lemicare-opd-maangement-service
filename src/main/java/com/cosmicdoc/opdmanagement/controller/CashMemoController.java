package com.cosmicdoc.opdmanagement.controller;

import com.cosmicdoc.opdmanagement.model.ApiResponse;
import com.cosmicdoc.opdmanagement.model.CashMemo;
import com.cosmicdoc.opdmanagement.service.CashMemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Cash Memo operations
 * This controller handles all cash memo related API endpoints
 * including complete data with tax details and line items
 */
@RestController
@RequestMapping("/api/billing/cash-memo")
@Slf4j
public class CashMemoController {

    private final CashMemoService cashMemoService;

    @Autowired
    public CashMemoController(CashMemoService cashMemoService) {
        this.cashMemoService = cashMemoService;
    }

    /**
     * Create a new cash memo
     *
     * @param cashMemo The cash memo to create
     * @return ResponseEntity with the created cash memo
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CashMemo>> createCashMemo(@RequestBody CashMemo cashMemo) {
        log.info("Received request to create cash memo for patient: {}", cashMemo.getPatientId());
        CashMemo createdCashMemo = cashMemoService.createCashMemo(cashMemo);
        return ResponseEntity.ok(ApiResponse.success("Cash memo created successfully", createdCashMemo));
    }

    /**
     * Get all cash memos
     *
     * @return ResponseEntity with list of all cash memos
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CashMemo>>> getAllCashMemos() {
        log.info("Received request to get all cash memos");
        List<CashMemo> cashMemos = cashMemoService.getAllCashMemos();
        return ResponseEntity.ok(ApiResponse.success("Cash memos retrieved successfully", cashMemos));
    }

    /**
     * Get a cash memo by ID
     *
     * @param id The cash memo ID
     * @return ResponseEntity with the cash memo if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CashMemo>> getCashMemoById(@PathVariable String id) {
        log.info("Received request to get cash memo with ID: {}", id);
        return cashMemoService.getCashMemoById(id)
                .map(cashMemo -> ResponseEntity.ok(ApiResponse.success("Cash memo retrieved successfully", cashMemo)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get cash memos by patient ID
     *
     * @param patientId The patient ID
     * @return ResponseEntity with list of cash memos for the patient
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<CashMemo>>> getCashMemosByPatientId(@PathVariable String patientId) {
        log.info("Received request to get cash memos for patient: {}", patientId);
        List<CashMemo> cashMemos = cashMemoService.getCashMemosByPatientId(patientId);
        return ResponseEntity.ok(ApiResponse.success("Cash memos retrieved successfully", cashMemos));
    }

    /**
     * Update an existing cash memo
     *
     * @param id The cash memo ID
     * @param cashMemo The updated cash memo details
     * @return ResponseEntity with the updated cash memo
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CashMemo>> updateCashMemo(@PathVariable String id, @RequestBody CashMemo cashMemo) {
        log.info("Received request to update cash memo with ID: {}", id);
        try {
            CashMemo updatedCashMemo = cashMemoService.updateCashMemo(id, cashMemo);
            return ResponseEntity.ok(ApiResponse.success("Cash memo updated successfully", updatedCashMemo));
        } catch (RuntimeException e) {
            log.error("Failed to update cash memo: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a cash memo
     *
     * @param id The cash memo ID
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCashMemo(@PathVariable String id) {
        log.info("Received request to delete cash memo with ID: {}", id);
        cashMemoService.deleteCashMemo(id);
        return ResponseEntity.ok(ApiResponse.success("Cash memo deleted successfully", null));
    }
}
