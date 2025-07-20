package com.cosmicdoc.opdmanagement.controller;

import com.cosmicdoc.opdmanagement.model.ApiResponse;
import com.cosmicdoc.opdmanagement.model.Invoice;
import com.cosmicdoc.opdmanagement.service.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Invoice operations
 */
@RestController
@RequestMapping("/api/billing/invoice")
@Slf4j
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /**
     * Create a new invoice
     *
     * @param invoice The invoice to create
     * @return ResponseEntity with the created invoice
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Invoice>> createInvoice(@RequestBody Invoice invoice) {
        log.info("Received request to create invoice for patient: {}", invoice.getPatientId());
        Invoice createdInvoice = invoiceService.createInvoice(invoice);
        return ResponseEntity.ok(ApiResponse.success("Invoice created successfully", createdInvoice));
    }

    /**
     * Get all invoices
     *
     * @return ResponseEntity with list of all invoices
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Invoice>>> getAllInvoices() {
        log.info("Received request to get all invoices");
        List<Invoice> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(ApiResponse.success("Invoices retrieved successfully", invoices));
    }

    /**
     * Get an invoice by ID
     *
     * @param id The invoice ID
     * @return ResponseEntity with the invoice if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Invoice>> getInvoiceById(@PathVariable String id) {
        log.info("Received request to get invoice with ID: {}", id);
        return invoiceService.getInvoiceById(id)
                .map(invoice -> ResponseEntity.ok(ApiResponse.success("Invoice retrieved successfully", invoice)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get invoices by patient ID
     *
     * @param patientId The patient ID
     * @return ResponseEntity with list of invoices for the patient
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<Invoice>>> getInvoicesByPatientId(@PathVariable String patientId) {
        log.info("Received request to get invoices for patient: {}", patientId);
        List<Invoice> invoices = invoiceService.getInvoicesByPatientId(patientId);
        return ResponseEntity.ok(ApiResponse.success("Invoices retrieved successfully", invoices));
    }

    /**
     * Update an existing invoice
     *
     * @param id The invoice ID
     * @param invoice The updated invoice details
     * @return ResponseEntity with the updated invoice
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Invoice>> updateInvoice(@PathVariable String id, @RequestBody Invoice invoice) {
        log.info("Received request to update invoice with ID: {}", id);
        try {
            Invoice updatedInvoice = invoiceService.updateInvoice(id, invoice);
            return ResponseEntity.ok(ApiResponse.success("Invoice updated successfully", updatedInvoice));
        } catch (RuntimeException e) {
            log.error("Failed to update invoice: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete an invoice
     *
     * @param id The invoice ID
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInvoice(@PathVariable String id) {
        log.info("Received request to delete invoice with ID: {}", id);
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok(ApiResponse.success("Invoice deleted successfully", null));
    }
}
