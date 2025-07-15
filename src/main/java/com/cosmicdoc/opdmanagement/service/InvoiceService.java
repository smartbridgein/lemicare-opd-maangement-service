package com.cosmicdoc.opdmanagement.service;

import com.cosmicdoc.opdmanagement.model.Invoice;
import com.cosmicdoc.opdmanagement.repository.InvoiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Invoice operations
 */
@Service
@Slf4j
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Create a new invoice
     *
     * @param invoice The invoice to create
     * @return The created invoice
     */
    public Invoice createInvoice(Invoice invoice) {
        log.info("Creating invoice for patient: {}", invoice.getPatientId());
        return invoiceRepository.save(invoice);
    }

    /**
     * Retrieve all invoices
     *
     * @return List of all invoices
     */
    public List<Invoice> getAllInvoices() {
        log.info("Retrieving all invoices");
        return invoiceRepository.findAll();
    }

    /**
     * Retrieve an invoice by ID
     *
     * @param id The invoice ID
     * @return Optional containing the invoice if found
     */
    public Optional<Invoice> getInvoiceById(String id) {
        log.info("Retrieving invoice with ID: {}", id);
        return invoiceRepository.findById(id);
    }

    /**
     * Retrieve invoices for a specific patient
     *
     * @param patientId The patient ID
     * @return List of invoices for the patient
     */
    public List<Invoice> getInvoicesByPatientId(String patientId) {
        log.info("Retrieving invoices for patient: {}", patientId);
        return invoiceRepository.findByPatientId(patientId);
    }

    /**
     * Update an existing invoice
     *
     * @param id The invoice ID
     * @param updatedInvoice The updated invoice details
     * @return The updated invoice
     * @throws RuntimeException if the invoice is not found
     */
    public Invoice updateInvoice(String id, Invoice updatedInvoice) {
        log.info("Updating invoice with ID: {}", id);
        
        Invoice existingInvoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + id));
        
        // Update fields while preserving ID and invoice ID
        updatedInvoice.setId(existingInvoice.getId());
        if (updatedInvoice.getInvoiceId() == null) {
            updatedInvoice.setInvoiceId(existingInvoice.getInvoiceId());
        }
        
        return invoiceRepository.save(updatedInvoice);
    }

    /**
     * Delete an invoice
     *
     * @param id The invoice ID
     */
    public void deleteInvoice(String id) {
        log.info("Deleting invoice with ID: {}", id);
        invoiceRepository.delete(id);
    }
}
