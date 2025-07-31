package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.Receipt;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Receipt operations
 */
public interface ReceiptRepository {
    Receipt save(Receipt receipt);
    List<Receipt> findAll();
    Optional<Receipt> findById(String id);
    List<Receipt> findByPatientId(String patientId);
    List<Receipt> findByInvoiceId(String invoiceId);
    void delete(String id);
    String generateReceiptId();
}
