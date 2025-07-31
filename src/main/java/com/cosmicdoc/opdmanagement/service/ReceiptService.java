package com.cosmicdoc.opdmanagement.service;

import com.cosmicdoc.opdmanagement.model.Invoice;
import com.cosmicdoc.opdmanagement.model.Receipt;
import com.cosmicdoc.opdmanagement.repository.ReceiptRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Receipt operations
 */
@Service
@Slf4j
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final InvoiceService invoiceService;

    @Autowired
    public ReceiptService(ReceiptRepository receiptRepository, InvoiceService invoiceService) {
        this.receiptRepository = receiptRepository;
        this.invoiceService = invoiceService;
    }

    /**
     * Create a new receipt
     *
     * @param receipt The receipt to create
     * @return The created receipt
     */
    public Receipt createReceipt(Receipt receipt) {
        log.info("Creating receipt for patient: {}", receipt.getPatientId());
        
        // CRITICAL: Check if receipt already exists for this invoice
        if (receipt.getInvoiceId() != null && !receipt.getInvoiceId().isEmpty()) {
            List<Receipt> existingReceipts = receiptRepository.findByInvoiceId(receipt.getInvoiceId());
            if (!existingReceipts.isEmpty()) {
                log.error("DUPLICATE RECEIPT PREVENTION: Receipt already exists for invoice: {}", receipt.getInvoiceId());
                log.error("Existing receipts: {}", existingReceipts.stream()
                    .map(r -> "ReceiptId: " + r.getReceiptId() + ", Amount: " + r.getAmount())
                    .toArray());
                throw new RuntimeException("Cannot create receipt: Invoice " + receipt.getInvoiceId() + 
                    " already has an existing receipt. Use the Print function to reprint the existing receipt.");
            }
            log.info("No existing receipts found for invoice: {}, proceeding with creation", receipt.getInvoiceId());
        }
        
        // Save the receipt first
        Receipt savedReceipt = receiptRepository.save(receipt);
        
        // If this receipt is linked to an invoice, update the invoice status to PAID
        if (receipt.getInvoiceId() != null && !receipt.getInvoiceId().isEmpty()) {
            try {
                log.info("Updating invoice status to PAID for invoice: {}", receipt.getInvoiceId());
                Optional<Invoice> invoiceOpt = invoiceService.getInvoiceById(receipt.getInvoiceId());
                
                if (invoiceOpt.isPresent()) {
                    Invoice invoice = invoiceOpt.get();
                    
                    // Update payment tracking fields
                    double receiptAmount = receipt.getAmount();
                    double currentPaidAmount = invoice.getPaidAmount();
                    double newPaidAmount = currentPaidAmount + receiptAmount;
                    double totalAmount = invoice.getAmount();
                    double newBalanceAmount = totalAmount - newPaidAmount;
                    
                    invoice.setPaidAmount(newPaidAmount);
                    invoice.setBalanceAmount(Math.max(0, newBalanceAmount)); // Ensure balance doesn't go negative
                    
                    // Update status based on payment completion
                    if (newBalanceAmount <= 0) {
                        invoice.setStatus("PAID");
                        invoice.setBalanceAmount(0); // Ensure balance is exactly 0 for paid invoices
                    } else if (newPaidAmount > 0) {
                        invoice.setStatus("PARTIAL");
                    }
                    
                    invoiceService.updateInvoice(invoice.getId(), invoice);
                    log.info("Successfully updated invoice {} - Status: {}, Paid: {}, Balance: {}", 
                            receipt.getInvoiceId(), invoice.getStatus(), invoice.getPaidAmount(), invoice.getBalanceAmount());
                } else {
                    log.warn("Invoice not found with ID: {}", receipt.getInvoiceId());
                }
            } catch (Exception e) {
                log.error("Failed to update invoice status for invoice: {}", receipt.getInvoiceId(), e);
                // Don't fail the receipt creation if invoice update fails
            }
        }
        
        return savedReceipt;
    }

    /**
     * Retrieve all receipts
     *
     * @return List of all receipts
     */
    public List<Receipt> getAllReceipts() {
        log.info("Retrieving all receipts");
        return receiptRepository.findAll();
    }

    /**
     * Retrieve a receipt by ID
     *
     * @param id The receipt ID
     * @return Optional containing the receipt if found
     */
    public Optional<Receipt> getReceiptById(String id) {
        log.info("Retrieving receipt with ID: {}", id);
        return receiptRepository.findById(id);
    }

    /**
     * Retrieve receipts for a specific patient
     *
     * @param patientId The patient ID
     * @return List of receipts for the patient
     */
    public List<Receipt> getReceiptsByPatientId(String patientId) {
        log.info("Retrieving receipts for patient: {}", patientId);
        return receiptRepository.findByPatientId(patientId);
    }

    /**
     * Retrieve receipts for a specific invoice
     *
     * @param invoiceId The invoice ID
     * @return List of receipts for the invoice
     */
    public List<Receipt> getReceiptsByInvoiceId(String invoiceId) {
        log.info("Retrieving receipts for invoice: {}", invoiceId);
        return receiptRepository.findByInvoiceId(invoiceId);
    }

    /**
     * Update an existing receipt
     *
     * @param id The receipt ID
     * @param receipt The updated receipt details
     * @return The updated receipt
     * @throws RuntimeException if the receipt is not found
     */
    public Receipt updateReceipt(String id, Receipt receipt) {
        log.info("Updating receipt with ID: {}", id);
        
        // CRITICAL: Check if this receipt is linked to a paid invoice - prevent editing if so
        Optional<Receipt> existingReceiptOpt = receiptRepository.findById(id);
        if (existingReceiptOpt.isPresent()) {
            Receipt existingReceipt = existingReceiptOpt.get();
            
            if (existingReceipt.getInvoiceId() != null && !existingReceipt.getInvoiceId().isEmpty()) {
                try {
                    Optional<Invoice> invoiceOpt = invoiceService.getInvoiceById(existingReceipt.getInvoiceId());
                    
                    if (invoiceOpt.isPresent()) {
                        Invoice invoice = invoiceOpt.get();
                        
                        // Check if invoice is paid - prevent editing if so
                        if ("PAID".equals(invoice.getStatus()) || invoice.getBalanceAmount() <= 0) {
                            log.error("RECEIPT EDIT PREVENTION: Cannot edit receipt {} - linked invoice {} is already paid", 
                                    id, existingReceipt.getInvoiceId());
                            throw new RuntimeException("Cannot edit receipt: This receipt is linked to a paid invoice (" + 
                                    existingReceipt.getInvoiceId() + "). Editing paid receipts is not allowed for audit compliance.");
                        }
                    }
                } catch (Exception e) {
                    if (e.getMessage().contains("Cannot edit receipt")) {
                        throw e; // Re-throw our custom validation error
                    }
                    log.warn("Could not verify invoice status for receipt {}: {}", id, e.getMessage());
                    // Continue with update if we can't verify invoice status
                }
            }
        }
        
        receipt.setReceiptId(id);
        return receiptRepository.save(receipt);
    }

    /**
     * Delete a receipt
     *
     * @param id The receipt ID
     */
    public void deleteReceipt(String id) {
        log.info("Deleting receipt with ID: {}", id);
        receiptRepository.delete(id);
    }

    /**
     * Fix invoice statuses for existing receipts that should have updated their invoices
     * This method can be called to retroactively sync invoice statuses
     */
    public void fixInvoiceStatusesForExistingReceipts() {
        log.info("Starting to fix invoice statuses for existing receipts");
        
        try {
            List<Receipt> allReceipts = receiptRepository.findAll();
            log.info("Found {} receipts to process", allReceipts.size());
            
            for (Receipt receipt : allReceipts) {
                if (receipt.getInvoiceId() != null && !receipt.getInvoiceId().isEmpty()) {
                    log.info("Processing receipt {} for invoice {}", receipt.getReceiptId(), receipt.getInvoiceId());
                    
                    try {
                        Optional<Invoice> invoiceOpt = invoiceService.getInvoiceById(receipt.getInvoiceId());
                        
                        if (invoiceOpt.isPresent()) {
                            Invoice invoice = invoiceOpt.get();
                            
                            // Check if invoice needs status update
                            if (!"PAID".equals(invoice.getStatus())) {
                                log.info("Updating invoice {} status from {} to PAID", 
                                        receipt.getInvoiceId(), invoice.getStatus());
                                
                                // Update payment tracking fields
                                double receiptAmount = receipt.getAmount();
                                double totalAmount = invoice.getAmount();
                                
                                invoice.setPaidAmount(receiptAmount);
                                invoice.setBalanceAmount(0); // Fully paid
                                invoice.setStatus("PAID");
                                
                                invoiceService.updateInvoice(invoice.getId(), invoice);
                                log.info("Successfully updated invoice {} to PAID status", receipt.getInvoiceId());
                            } else {
                                log.info("Invoice {} is already marked as PAID", receipt.getInvoiceId());
                            }
                        } else {
                            log.warn("Invoice not found with ID: {}", receipt.getInvoiceId());
                        }
                    } catch (Exception e) {
                        log.error("Failed to update invoice status for receipt {}: {}", 
                                receipt.getReceiptId(), e.getMessage(), e);
                    }
                } else {
                    log.debug("Receipt {} has no invoiceId, skipping", receipt.getReceiptId());
                }
            }
            
            log.info("Completed fixing invoice statuses for existing receipts");
        } catch (Exception e) {
            log.error("Error while fixing invoice statuses: {}", e.getMessage(), e);
        }
    }
}
