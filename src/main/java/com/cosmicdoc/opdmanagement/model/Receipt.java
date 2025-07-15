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
    
    // Constructor to create a new receipt with required fields
    public Receipt(String patientId, String patientName, double amount, String createdBy, String modeOfPayment) {
        this.setPatientId(patientId);
        this.setPatientName(patientName);
        this.setAmount(amount);
        this.setCreatedBy(createdBy);
        this.setModeOfPayment(modeOfPayment);
        this.setCreatedDate(java.time.LocalDate.now());
        this.setDate(java.time.LocalDate.now());
    }
}
