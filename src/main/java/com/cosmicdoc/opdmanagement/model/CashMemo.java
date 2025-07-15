package com.cosmicdoc.opdmanagement.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cash memo billing item in the OPD
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CashMemo extends BillingItem {
    // Cash memo specific fields
    private String billId;
    private String category;
    private String taxation;
    private String account;
    private String packageName; // Changed from 'package' to avoid Java keyword conflict
    private String referenceNo;
    private String notes;
    private double overallDiscount;
    private String discountType;
    private double totalTax;
    private List<TaxComponent> taxBreakdown = new ArrayList<>();
    private List<LineItem> lineItems = new ArrayList<>();
    
    // Constructor to create a new cash memo with required fields
    public CashMemo(String patientId, String patientName, double amount, String createdBy, String modeOfPayment) {
        this.setPatientId(patientId);
        this.setPatientName(patientName);
        this.setAmount(amount);
        this.setCreatedBy(createdBy);
        this.setModeOfPayment(modeOfPayment);
        this.setCreatedDate(java.time.LocalDate.now());
        this.setDate(java.time.LocalDate.now());
    }
}
