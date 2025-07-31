package com.cosmicdoc.opdmanagement.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents an advance payment in the OPD billing system
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Advance extends BillingItem {
    private String advanceId;
    
    // Constructor to create a new advance with required fields
    public Advance(String patientId, String patientName, double amount, String createdBy, String modeOfPayment) {
        this.setPatientId(patientId);
        this.setPatientName(patientName);
        this.setAmount(amount);
        this.setCreatedBy(createdBy);
        this.setModeOfPayment(modeOfPayment);
        this.setCreatedDate(java.time.LocalDate.now());
        this.setDate(java.time.LocalDate.now());
    }
}
