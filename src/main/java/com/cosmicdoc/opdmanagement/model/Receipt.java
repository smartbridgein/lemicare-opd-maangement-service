package com.cosmicdoc.opdmanagement.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a receipt in the OPD billing system
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Receipt extends BillingItem {
    private String receiptId;
    private String invoiceId; // Link to the invoice this receipt is for
    private String status; // Receipt status (e.g., ACTIVE, CANCELLED)
    
    // Constructor to create a new receipt with required fields
    public Receipt(String patientId, String patientName, double amount, String createdBy, String modeOfPayment) {
        this.setPatientId(patientId);
        this.setPatientName(patientName);
        this.setAmount(amount);
        this.setCreatedBy(createdBy);
        this.setModeOfPayment(modeOfPayment);
        this.setCreatedDate(java.time.LocalDate.now());
        this.setDate(java.time.LocalDate.now());
        this.status = "ACTIVE"; // Default status
    }
    
    // Constructor with invoiceId
    public Receipt(String patientId, String patientName, double amount, String createdBy, String modeOfPayment, String invoiceId) {
        this(patientId, patientName, amount, createdBy, modeOfPayment);
        this.invoiceId = invoiceId;
    }
}
