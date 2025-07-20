package com.cosmicdoc.opdmanagement.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents an invoice in the OPD billing system
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Invoice extends BillingItem {
    private String invoiceId;
    
    // Constructor to create a new invoice with required fields
    public Invoice(String patientId, String patientName, double amount, String createdBy) {
        this.setPatientId(patientId);
        this.setPatientName(patientName);
        this.setAmount(amount);
        this.setCreatedBy(createdBy);
        this.setCreatedDate(java.time.LocalDate.now());
        this.setDate(java.time.LocalDate.now());
    }
}
