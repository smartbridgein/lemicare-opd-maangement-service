package com.cosmicdoc.opdmanagement.controller;

import com.cosmicdoc.opdmanagement.model.ApiResponse;
import com.cosmicdoc.opdmanagement.model.CreditNote;
import com.cosmicdoc.opdmanagement.service.CreditNoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Credit Note operations
 */
@RestController
@RequestMapping("/api/billing/credit-note")
@Slf4j
public class CreditNoteController {

    private final CreditNoteService creditNoteService;

    @Autowired
    public CreditNoteController(CreditNoteService creditNoteService) {
        this.creditNoteService = creditNoteService;
    }

    /**
     * Create a new credit note
     *
     * @param creditNote The credit note to create
     * @return ResponseEntity with the created credit note
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreditNote>> createCreditNote(@RequestBody CreditNote creditNote) {
        log.info("Received request to create credit note for patient: {}", creditNote.getPatientId());
        CreditNote createdCreditNote = creditNoteService.createCreditNote(creditNote);
        return ResponseEntity.ok(ApiResponse.success("Credit note created successfully", createdCreditNote));
    }

    /**
     * Get all credit notes
     *
     * @return ResponseEntity with list of all credit notes
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CreditNote>>> getAllCreditNotes() {
        log.info("Received request to get all credit notes");
        List<CreditNote> creditNotes = creditNoteService.getAllCreditNotes();
        return ResponseEntity.ok(ApiResponse.success("Credit notes retrieved successfully", creditNotes));
    }

    /**
     * Get a credit note by ID
     *
     * @param id The credit note ID
     * @return ResponseEntity with the credit note if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CreditNote>> getCreditNoteById(@PathVariable String id) {
        log.info("Received request to get credit note with ID: {}", id);
        return creditNoteService.getCreditNoteById(id)
                .map(creditNote -> ResponseEntity.ok(ApiResponse.success("Credit note retrieved successfully", creditNote)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get credit notes by patient ID
     *
     * @param patientId The patient ID
     * @return ResponseEntity with list of credit notes for the patient
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<CreditNote>>> getCreditNotesByPatientId(@PathVariable String patientId) {
        log.info("Received request to get credit notes for patient: {}", patientId);
        List<CreditNote> creditNotes = creditNoteService.getCreditNotesByPatientId(patientId);
        return ResponseEntity.ok(ApiResponse.success("Credit notes retrieved successfully", creditNotes));
    }

    /**
     * Update an existing credit note
     *
     * @param id The credit note ID
     * @param creditNote The updated credit note details
     * @return ResponseEntity with the updated credit note
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CreditNote>> updateCreditNote(@PathVariable String id, @RequestBody CreditNote creditNote) {
        log.info("Received request to update credit note with ID: {}", id);
        try {
            CreditNote updatedCreditNote = creditNoteService.updateCreditNote(id, creditNote);
            return ResponseEntity.ok(ApiResponse.success("Credit note updated successfully", updatedCreditNote));
        } catch (RuntimeException e) {
            log.error("Failed to update credit note: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a credit note
     *
     * @param id The credit note ID
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCreditNote(@PathVariable String id) {
        log.info("Received request to delete credit note with ID: {}", id);
        creditNoteService.deleteCreditNote(id);
        return ResponseEntity.ok(ApiResponse.success("Credit note deleted successfully", null));
    }
}
