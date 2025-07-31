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
        
        // Ensure timestamp fields are set if not already present
        if (invoice.getTimestamp() == null) {
            invoice.setTimestamp(java.time.LocalDateTime.now());
        }
        if (invoice.getCreatedTimestamp() == null) {
            invoice.setCreatedTimestamp(java.time.LocalDateTime.now());
        }
        if (invoice.getDate() == null) {
            invoice.setDate(java.time.LocalDate.now());
        }
        if (invoice.getCreatedDate() == null) {
            invoice.setCreatedDate(java.time.LocalDate.now());
        }
        
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
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(id);
        
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            // Fix balance tracking for existing invoices that don't have proper balance values
            invoice = ensureBalanceFieldsAreCorrect(invoice);
            return Optional.of(invoice);
        }
        
        return invoiceOpt;
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

    /**
     * Ensure balance tracking fields are correct for existing invoices
     * This fixes invoices created before balance tracking was implemented
     *
     * @param invoice The invoice to check and fix
     * @return The invoice with corrected balance fields
     */
    private Invoice ensureBalanceFieldsAreCorrect(Invoice invoice) {
        boolean needsUpdate = false;
        
        // Check if balance fields need initialization
        if (invoice.getStatus() == null || invoice.getStatus().isEmpty()) {
            invoice.setStatus("PENDING");
            needsUpdate = true;
        }
        
        // If balanceAmount is 0 but status is not PAID, it likely needs fixing
        if (invoice.getBalanceAmount() == 0.0 && !"PAID".equals(invoice.getStatus())) {
            double totalAmount = invoice.getAmount();
            double paidAmount = invoice.getPaidAmount();
            
            // Calculate correct balance
            double correctBalance = totalAmount - paidAmount;
            
            if (correctBalance > 0) {
                invoice.setBalanceAmount(correctBalance);
                needsUpdate = true;
                log.info("Fixed balance for invoice {}: set balanceAmount to {} (total: {}, paid: {})", 
                        invoice.getInvoiceId(), correctBalance, totalAmount, paidAmount);
            }
        }
        
        // Update status based on balance
        if (invoice.getBalanceAmount() <= 0 && invoice.getPaidAmount() > 0) {
            if (!"PAID".equals(invoice.getStatus())) {
                invoice.setStatus("PAID");
                needsUpdate = true;
            }
        } else if (invoice.getPaidAmount() > 0 && invoice.getBalanceAmount() > 0) {
            if (!"PARTIAL".equals(invoice.getStatus())) {
                invoice.setStatus("PARTIAL");
                needsUpdate = true;
            }
        } else if (invoice.getPaidAmount() == 0) {
            if (!"PENDING".equals(invoice.getStatus()) && !"UNPAID".equals(invoice.getStatus())) {
                invoice.setStatus("PENDING");
                needsUpdate = true;
            }
        }
        
        // Save the updated invoice if changes were made
        if (needsUpdate) {
            log.info("Updating invoice {} with corrected balance fields", invoice.getInvoiceId());
            invoice = invoiceRepository.save(invoice);
        }
        
        return invoice;
    }
}
