package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.Advance;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Advance operations
 */
public interface AdvanceRepository {
    Advance save(Advance advance);
    List<Advance> findAll();
    Optional<Advance> findById(String id);
    List<Advance> findByPatientId(String patientId);
    void delete(String id);
    String generateAdvanceId();
}
