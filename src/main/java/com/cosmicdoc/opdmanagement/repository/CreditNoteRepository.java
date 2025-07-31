package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.CreditNote;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CreditNote operations
 */
public interface CreditNoteRepository {
    CreditNote save(CreditNote creditNote);
    List<CreditNote> findAll();
    Optional<CreditNote> findById(String id);
    List<CreditNote> findByPatientId(String patientId);
    void delete(String id);
    String generateCreditNoteId();
}
