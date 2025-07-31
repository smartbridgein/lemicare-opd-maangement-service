package com.cosmicdoc.opdmanagement.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Firestore compatible wrapper for Invoice
 */
@Data
@NoArgsConstructor
public class FirestoreInvoice {
    @DocumentId
    private String id;
    private String patientId;
    private String patientName;
    private Timestamp date;
    private Timestamp timestamp; // Full timestamp for sorting and detailed tracking
    private Double amount;
    private String createdBy;
    private String invoiceId;
    private Timestamp createdDate;
    private Timestamp createdTimestamp; // Full timestamp for creation tracking
    private String modeOfPayment;
    
    // Additional fields for complete invoice data
    private List<FirestoreLineItem> items;
    private String commonTaxationType;
    private List<FirestoreTaxComponent> taxBreakdown;
    private Double totalTax;
    private Double subtotal;
    private Double discount;
    private Double grandTotal;
    private String notes;
    private String status;
    private List<FirestorePaymentRecord> paymentHistory;
    
    public FirestoreInvoice(Invoice invoice) {
        this.id = invoice.getId();
        this.patientId = invoice.getPatientId();
        this.patientName = invoice.getPatientName();
        this.amount = invoice.getAmount();
        this.createdBy = invoice.getCreatedBy();
        this.invoiceId = invoice.getInvoiceId();
        this.modeOfPayment = invoice.getModeOfPayment();
        
        // Convert additional fields
        this.commonTaxationType = invoice.getCommonTaxationType();
        this.totalTax = invoice.getTotalTax();
        this.subtotal = invoice.getSubtotal();
        this.discount = invoice.getDiscount();
        this.grandTotal = invoice.getGrandTotal();
        this.notes = invoice.getNotes();
        this.status = invoice.getStatus();
        
        // Convert line items
        if (invoice.getItems() != null) {
            this.items = invoice.getItems().stream()
                .map(FirestoreLineItem::new)
                .collect(Collectors.toList());
        }
        
        // Convert tax breakdown
        if (invoice.getTaxBreakdown() != null) {
            this.taxBreakdown = invoice.getTaxBreakdown().stream()
                .map(FirestoreTaxComponent::new)
                .collect(Collectors.toList());
        }
        
        // Convert payment history
        if (invoice.getPaymentHistory() != null) {
            this.paymentHistory = invoice.getPaymentHistory().stream()
                .map(FirestorePaymentRecord::new)
                .collect(Collectors.toList());
        }
        
        if (invoice.getDate() != null) {
            this.date = Timestamp.of(Date.from(invoice.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        
        // Map timestamp field (full datetime)
        if (invoice.getTimestamp() != null) {
            this.timestamp = Timestamp.of(Date.from(invoice.getTimestamp().atZone(ZoneId.systemDefault()).toInstant()));
        } else {
            this.timestamp = Timestamp.now(); // Set current timestamp if not provided
        }
        
        if (invoice.getCreatedDate() != null) {
            this.createdDate = Timestamp.of(Date.from(invoice.getCreatedDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            this.createdDate = Timestamp.now();
        }
        
        // Map createdTimestamp field (full datetime)
        if (invoice.getCreatedTimestamp() != null) {
            this.createdTimestamp = Timestamp.of(Date.from(invoice.getCreatedTimestamp().atZone(ZoneId.systemDefault()).toInstant()));
        } else {
            this.createdTimestamp = Timestamp.now(); // Set current timestamp if not provided
        }
    }
    
    public Invoice toInvoice() {
        Invoice invoice = new Invoice();
        invoice.setId(this.id);
        invoice.setPatientId(this.patientId);
        invoice.setPatientName(this.patientName);
        invoice.setAmount(this.amount != null ? this.amount : 0.0);
        invoice.setCreatedBy(this.createdBy);
        invoice.setInvoiceId(this.invoiceId);
        invoice.setModeOfPayment(this.modeOfPayment);
        
        // Convert additional fields
        invoice.setCommonTaxationType(this.commonTaxationType);
        invoice.setTotalTax(this.totalTax != null ? this.totalTax : 0.0);
        invoice.setSubtotal(this.subtotal != null ? this.subtotal : 0.0);
        invoice.setDiscount(this.discount != null ? this.discount : 0.0);
        invoice.setGrandTotal(this.grandTotal != null ? this.grandTotal : 0.0);
        invoice.setNotes(this.notes);
        invoice.setStatus(this.status);
        
        // Convert line items
        if (this.items != null) {
            invoice.setItems(this.items.stream()
                .map(FirestoreLineItem::toLineItem)
                .collect(Collectors.toList()));
        }
        
        // Convert tax breakdown
        if (this.taxBreakdown != null) {
            invoice.setTaxBreakdown(this.taxBreakdown.stream()
                .map(FirestoreTaxComponent::toTaxComponent)
                .collect(Collectors.toList()));
        }
        
        // Convert payment history
        if (this.paymentHistory != null) {
            invoice.setPaymentHistory(this.paymentHistory.stream()
                .map(FirestorePaymentRecord::toPaymentRecord)
                .collect(Collectors.toList()));
        }
        
        if (this.date != null) {
            invoice.setDate(this.date.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        // Map timestamp field back to LocalDateTime
        if (this.timestamp != null) {
            invoice.setTimestamp(this.timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        
        if (this.createdDate != null) {
            invoice.setCreatedDate(this.createdDate.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        // Map createdTimestamp field back to LocalDateTime
        if (this.createdTimestamp != null) {
            invoice.setCreatedTimestamp(this.createdTimestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        
        return invoice;
    }
}
