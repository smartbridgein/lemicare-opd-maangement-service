package com.cosmicdoc.opdmanagement.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.Date;

/**
 * Firestore compatible wrapper for Receipt
 */
@Data
@NoArgsConstructor
public class FirestoreReceipt {
    @DocumentId
    private String id;
    private String patientId;
    private String patientName;
    private Timestamp date;
    private Double amount;
    private String createdBy;
    private String modeOfPayment;
    private String receiptId;
    private String invoiceId;
    private String status;
    private Timestamp createdDate;
    
    public FirestoreReceipt(Receipt receipt) {
        this.id = receipt.getId();
        this.patientId = receipt.getPatientId();
        this.patientName = receipt.getPatientName();
        this.amount = receipt.getAmount();
        this.createdBy = receipt.getCreatedBy();
        this.modeOfPayment = receipt.getModeOfPayment();
        this.receiptId = receipt.getReceiptId();
        this.invoiceId = receipt.getInvoiceId();
        this.status = receipt.getStatus();
        
        if (receipt.getDate() != null) {
            this.date = Timestamp.of(Date.from(receipt.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        
        if (receipt.getCreatedDate() != null) {
            this.createdDate = Timestamp.of(Date.from(receipt.getCreatedDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            this.createdDate = Timestamp.now();
        }
    }
    
    public Receipt toReceipt() {
        Receipt receipt = new Receipt();
        receipt.setId(this.id);
        receipt.setPatientId(this.patientId);
        receipt.setPatientName(this.patientName);
        receipt.setAmount(this.amount != null ? this.amount : 0.0);
        receipt.setCreatedBy(this.createdBy);
        receipt.setModeOfPayment(this.modeOfPayment);
        receipt.setReceiptId(this.receiptId);
        receipt.setInvoiceId(this.invoiceId);
        receipt.setStatus(this.status);
        
        if (this.date != null) {
            receipt.setDate(this.date.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        if (this.createdDate != null) {
            receipt.setCreatedDate(this.createdDate.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        return receipt;
    }
}
