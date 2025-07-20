package com.cosmicdoc.opdmanagement.service;

import com.cosmicdoc.opdmanagement.model.CashMemo;
import com.cosmicdoc.opdmanagement.repository.CashMemoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Cash Memo operations
 */
@Service
@Slf4j
public class CashMemoService {

    private final CashMemoRepository cashMemoRepository;

    @Autowired
    public CashMemoService(CashMemoRepository cashMemoRepository) {
        this.cashMemoRepository = cashMemoRepository;
    }

    /**
     * Create a new cash memo
     *
     * @param cashMemo The cash memo to create
     * @return The created cash memo
     */
    public CashMemo createCashMemo(CashMemo cashMemo) {
        log.info("Creating cash memo for patient: {}", cashMemo.getPatientId());
        return cashMemoRepository.save(cashMemo);
    }

    /**
     * Retrieve all cash memos
     *
     * @return List of all cash memos
     */
    public List<CashMemo> getAllCashMemos() {
        log.info("Retrieving all cash memos");
        return cashMemoRepository.findAll();
    }

    /**
     * Retrieve a cash memo by ID
     *
     * @param id The cash memo ID
     * @return Optional containing the cash memo if found
     */
    public Optional<CashMemo> getCashMemoById(String id) {
        log.info("Retrieving cash memo with ID: {}", id);
        return cashMemoRepository.findById(id);
    }

    /**
     * Retrieve cash memos for a specific patient
     *
     * @param patientId The patient ID
     * @return List of cash memos for the patient
     */
    public List<CashMemo> getCashMemosByPatientId(String patientId) {
        log.info("Retrieving cash memos for patient: {}", patientId);
        return cashMemoRepository.findByPatientId(patientId);
    }

    /**
     * Update an existing cash memo
     *
     * @param id The cash memo ID
     * @param updatedCashMemo The updated cash memo details
     * @return The updated cash memo
     * @throws RuntimeException if the cash memo is not found
     */
    public CashMemo updateCashMemo(String id, CashMemo updatedCashMemo) {
        log.info("Updating cash memo with ID: {}", id);
        
        CashMemo existingCashMemo = cashMemoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cash Memo not found with ID: " + id));
        
        // Update fields while preserving ID and bill ID
        updatedCashMemo.setId(existingCashMemo.getId());
        if (updatedCashMemo.getBillId() == null) {
            updatedCashMemo.setBillId(existingCashMemo.getBillId());
        }
        
        return cashMemoRepository.save(updatedCashMemo);
    }

    /**
     * Delete a cash memo
     *
     * @param id The cash memo ID
     */
    public void deleteCashMemo(String id) {
        log.info("Deleting cash memo with ID: {}", id);
        cashMemoRepository.delete(id);
    }
}
