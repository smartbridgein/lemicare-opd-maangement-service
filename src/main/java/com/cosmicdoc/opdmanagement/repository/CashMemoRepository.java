package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.CashMemo;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CashMemo operations
 */
public interface CashMemoRepository {
    CashMemo save(CashMemo cashMemo);
    List<CashMemo> findAll();
    Optional<CashMemo> findById(String id);
    List<CashMemo> findByPatientId(String patientId);
    void delete(String id);
    String generateBillId();
}
