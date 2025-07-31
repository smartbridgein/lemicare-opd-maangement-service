package com.cosmicdoc.opdmanagement.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.cloud.firestore.annotation.PropertyName;

/**
 * Firestore compatible wrapper for CashMemo
 */
@Data
@NoArgsConstructor
public class FirestoreCashMemo {
    @DocumentId
    private String id;
    private String patientId;
    private String patientName;
    private Timestamp date;
    private Timestamp timestamp; // Full timestamp for sorting and detailed tracking
    private Double amount;
    private String createdBy;
    private String modeOfPayment;
    private String billId;
    private Timestamp createdDate;
    private Timestamp createdTimestamp; // Full timestamp for creation tracking
    private String category;
    private String taxation;
    private String account;
    private String packageName; // 'package' is a Java keyword so we use packageName
    private String referenceNo;
    private String notes;
    private Double overallDiscount;
    private String discountType;
    private Double totalTax;
    private List<FirestoreTaxComponent> taxBreakdown = new ArrayList<>();
    private List<FirestoreLineItem> lineItems = new ArrayList<>();
    
    public FirestoreCashMemo(CashMemo cashMemo) {
        this.id = cashMemo.getId();
        this.patientId = cashMemo.getPatientId();
        this.patientName = cashMemo.getPatientName();
        this.amount = cashMemo.getAmount();
        this.createdBy = cashMemo.getCreatedBy();
        this.modeOfPayment = cashMemo.getModeOfPayment();
        this.billId = cashMemo.getBillId();
        this.category = cashMemo.getCategory();
        this.taxation = cashMemo.getTaxation();
        this.account = cashMemo.getAccount();
        this.packageName = cashMemo.getPackageName();
        this.referenceNo = cashMemo.getReferenceNo();
        this.notes = cashMemo.getNotes();
        this.overallDiscount = cashMemo.getOverallDiscount();
        this.discountType = cashMemo.getDiscountType();
        this.totalTax = cashMemo.getTotalTax();
        
        if (cashMemo.getDate() != null) {
            this.date = Timestamp.of(Date.from(cashMemo.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        
        // Map timestamp field (full datetime)
        if (cashMemo.getTimestamp() != null) {
            this.timestamp = Timestamp.of(Date.from(cashMemo.getTimestamp().atZone(ZoneId.systemDefault()).toInstant()));
        } else {
            this.timestamp = Timestamp.now(); // Set current timestamp if not provided
        }
        
        if (cashMemo.getCreatedDate() != null) {
            this.createdDate = Timestamp.of(Date.from(cashMemo.getCreatedDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            this.createdDate = Timestamp.now();
        }
        
        // Map createdTimestamp field (full datetime)
        if (cashMemo.getCreatedTimestamp() != null) {
            this.createdTimestamp = Timestamp.of(Date.from(cashMemo.getCreatedTimestamp().atZone(ZoneId.systemDefault()).toInstant()));
        } else {
            this.createdTimestamp = Timestamp.now(); // Set current timestamp if not provided
        }
        
        // Convert tax breakdown
        if (cashMemo.getTaxBreakdown() != null && !cashMemo.getTaxBreakdown().isEmpty()) {
            this.taxBreakdown = cashMemo.getTaxBreakdown().stream()
                .map(FirestoreTaxComponent::new)
                .collect(Collectors.toList());
        }
        
        // Convert line items
        if (cashMemo.getLineItems() != null && !cashMemo.getLineItems().isEmpty()) {
            this.lineItems = cashMemo.getLineItems().stream()
                .map(FirestoreLineItem::new)
                .collect(Collectors.toList());
        }
    }
    
    public CashMemo toCashMemo() {
        CashMemo cashMemo = new CashMemo();
        cashMemo.setId(this.id);
        cashMemo.setPatientId(this.patientId);
        cashMemo.setPatientName(this.patientName);
        cashMemo.setAmount(this.amount != null ? this.amount : 0.0);
        cashMemo.setCreatedBy(this.createdBy);
        cashMemo.setModeOfPayment(this.modeOfPayment);
        cashMemo.setBillId(this.billId);
        cashMemo.setCategory(this.category);
        cashMemo.setTaxation(this.taxation);
        cashMemo.setAccount(this.account);
        cashMemo.setPackageName(this.packageName);
        cashMemo.setReferenceNo(this.referenceNo);
        cashMemo.setNotes(this.notes);
        cashMemo.setOverallDiscount(this.overallDiscount != null ? this.overallDiscount : 0.0);
        cashMemo.setDiscountType(this.discountType);
        cashMemo.setTotalTax(this.totalTax != null ? this.totalTax : 0.0);
        
        if (this.date != null) {
            cashMemo.setDate(this.date.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        // Map timestamp field back to LocalDateTime
        if (this.timestamp != null) {
            cashMemo.setTimestamp(this.timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        
        if (this.createdDate != null) {
            cashMemo.setCreatedDate(this.createdDate.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        // Map createdTimestamp field back to LocalDateTime
        if (this.createdTimestamp != null) {
            cashMemo.setCreatedTimestamp(this.createdTimestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        
        // Convert tax breakdown
        if (this.taxBreakdown != null && !this.taxBreakdown.isEmpty()) {
            cashMemo.setTaxBreakdown(this.taxBreakdown.stream()
                .map(FirestoreTaxComponent::toTaxComponent)
                .collect(Collectors.toList()));
        }
        
        // Convert line items
        if (this.lineItems != null && !this.lineItems.isEmpty()) {
            cashMemo.setLineItems(this.lineItems.stream()
                .map(FirestoreLineItem::toLineItem)
                .collect(Collectors.toList()));
        }
        
        return cashMemo;
    }
}
