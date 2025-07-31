package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.Refund;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Refund operations
 */
public interface RefundRepository {
    Refund save(Refund refund);
    List<Refund> findAll();
    Optional<Refund> findById(String id);
    List<Refund> findByPatientId(String patientId);
    void delete(String id);
    String generateRefundId();
}
