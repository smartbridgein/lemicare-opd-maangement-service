package com.cosmicdoc.opdmanagement.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a refund in the OPD billing system
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Refund extends BillingItem {
    private String refundId;
    private String reason;
    private String originalBillId; // Reference to the original invoice or payment
    
    // Constructor to create a new refund with required fields
    public Refund(String patientId, String patientName, double amount, String createdBy, 
                  String modeOfPayment, String reason, String originalBillId) {
        this.setPatientId(patientId);
        this.setPatientName(patientName);
        this.setAmount(amount);
        this.setCreatedBy(createdBy);
        this.setModeOfPayment(modeOfPayment);
        this.setReason(reason);
        this.setOriginalBillId(originalBillId);
        this.setCreatedDate(java.time.LocalDate.now());
        this.setDate(java.time.LocalDate.now());
    }
}
