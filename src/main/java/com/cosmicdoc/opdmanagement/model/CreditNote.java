package com.cosmicdoc.opdmanagement.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a credit note in the OPD billing system
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreditNote extends BillingItem {
    private String creditNoteId;
    private String reason;
    
    // Constructor to create a new credit note with required fields
    public CreditNote(String patientId, String patientName, double amount, String createdBy, String reason) {
        this.setPatientId(patientId);
        this.setPatientName(patientName);
        this.setAmount(amount);
        this.setCreatedBy(createdBy);
        this.setReason(reason);
        this.setCreatedDate(java.time.LocalDate.now());
        this.setDate(java.time.LocalDate.now());
    }
}
