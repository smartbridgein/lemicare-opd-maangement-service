package com.cosmicdoc.opdmanagement.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents an invoice in the OPD billing system
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Invoice extends BillingItem {
    private String invoiceId;
    private List<LineItem> items; // Line items for the invoice
    private String commonTaxationType; // Tax type: Non-Gst, Inclusive, Exclusive
    private List<TaxComponent> taxBreakdown; // Tax breakdown details
    private double totalTax; // Total tax amount
    private double subtotal; // Subtotal before tax
    private double discount; // Total discount
    private double grandTotal; // Final total amount
    private String notes; // Invoice notes
    private String status; // Invoice status (PENDING, PAID, PARTIAL, CANCELLED)
    private double balanceAmount; // Remaining balance to be paid
    private double paidAmount; // Amount already paid
    private List<PaymentRecord> paymentHistory; // Payment history
    
    // Constructor to create a new invoice with required fields
    public Invoice(String patientId, String patientName, double amount, String createdBy) {
        this.setPatientId(patientId);
        this.setPatientName(patientName);
        this.setAmount(amount);
        this.setCreatedBy(createdBy);
        this.setCreatedDate(java.time.LocalDate.now());
        this.setCreatedTimestamp(java.time.LocalDateTime.now());
        this.setDate(java.time.LocalDate.now());
        this.setTimestamp(java.time.LocalDateTime.now());
        
        // Initialize balance tracking fields
        this.status = "PENDING";
        this.balanceAmount = amount; // Initially, full amount is pending
        this.paidAmount = 0.0; // No payment made initially
        this.grandTotal = amount; // Set grand total to amount initially
    }
}
