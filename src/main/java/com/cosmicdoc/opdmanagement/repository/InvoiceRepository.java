package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.Invoice;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Invoice operations
 */
public interface InvoiceRepository {
    Invoice save(Invoice invoice);
    List<Invoice> findAll();
    Optional<Invoice> findById(String id);
    List<Invoice> findByPatientId(String patientId);
    void delete(String id);
    String generateInvoiceId();
}
