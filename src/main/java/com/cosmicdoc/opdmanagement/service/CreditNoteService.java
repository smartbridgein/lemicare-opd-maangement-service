package com.cosmicdoc.opdmanagement.service;

import com.cosmicdoc.opdmanagement.model.CreditNote;
import com.cosmicdoc.opdmanagement.repository.CreditNoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for CreditNote operations
 */
@Service
@Slf4j
public class CreditNoteService {

    private final CreditNoteRepository creditNoteRepository;

    @Autowired
    public CreditNoteService(CreditNoteRepository creditNoteRepository) {
        this.creditNoteRepository = creditNoteRepository;
    }

    /**
     * Create a new credit note
     *
     * @param creditNote The credit note to create
     * @return The created credit note
     */
    public CreditNote createCreditNote(CreditNote creditNote) {
        log.info("Creating credit note for patient: {}", creditNote.getPatientId());
        return creditNoteRepository.save(creditNote);
    }

    /**
     * Retrieve all credit notes
     *
     * @return List of all credit notes
     */
    public List<CreditNote> getAllCreditNotes() {
        log.info("Retrieving all credit notes");
        return creditNoteRepository.findAll();
    }

    /**
     * Retrieve a credit note by ID
     *
     * @param id The credit note ID
     * @return Optional containing the credit note if found
     */
    public Optional<CreditNote> getCreditNoteById(String id) {
        log.info("Retrieving credit note with ID: {}", id);
        return creditNoteRepository.findById(id);
    }

    /**
     * Retrieve credit notes for a specific patient
     *
     * @param patientId The patient ID
     * @return List of credit notes for the patient
     */
    public List<CreditNote> getCreditNotesByPatientId(String patientId) {
        log.info("Retrieving credit notes for patient: {}", patientId);
        return creditNoteRepository.findByPatientId(patientId);
    }

    /**
     * Update an existing credit note
     *
     * @param id The credit note ID
     * @param updatedCreditNote The updated credit note details
     * @return The updated credit note
     * @throws RuntimeException if the credit note is not found
     */
    public CreditNote updateCreditNote(String id, CreditNote updatedCreditNote) {
        log.info("Updating credit note with ID: {}", id);
        
        CreditNote existingCreditNote = creditNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Credit Note not found with ID: " + id));
        
        // Update fields while preserving ID and credit note ID
        updatedCreditNote.setId(existingCreditNote.getId());
        if (updatedCreditNote.getCreditNoteId() == null) {
            updatedCreditNote.setCreditNoteId(existingCreditNote.getCreditNoteId());
        }
        
        return creditNoteRepository.save(updatedCreditNote);
    }

    /**
     * Delete a credit note
     *
     * @param id The credit note ID
     */
    public void deleteCreditNote(String id) {
        log.info("Deleting credit note with ID: {}", id);
        creditNoteRepository.delete(id);
    }
}
