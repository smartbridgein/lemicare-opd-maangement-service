package com.cosmicdoc.opdmanagement.model;

import com.google.cloud.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.Date;

/**
 * Firestore representation of a PaymentRecord
 */
@Data
@NoArgsConstructor
public class FirestorePaymentRecord {
    private String paymentId;
    private Timestamp paymentDate;
    private Double amount;
    private String paymentMethod;
    private String referenceNumber;
    private String notes;
    private String status;
    
    public FirestorePaymentRecord(PaymentRecord paymentRecord) {
        this.paymentId = paymentRecord.getPaymentId();
        this.amount = paymentRecord.getAmount();
        this.paymentMethod = paymentRecord.getPaymentMethod();
        this.referenceNumber = paymentRecord.getReferenceNumber();
        this.notes = paymentRecord.getNotes();
        this.status = paymentRecord.getStatus();
        
        if (paymentRecord.getPaymentDate() != null) {
            this.paymentDate = Timestamp.of(Date.from(paymentRecord.getPaymentDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
    }
    
    public PaymentRecord toPaymentRecord() {
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPaymentId(this.paymentId);
        paymentRecord.setAmount(this.amount != null ? this.amount : 0.0);
        paymentRecord.setPaymentMethod(this.paymentMethod);
        paymentRecord.setReferenceNumber(this.referenceNumber);
        paymentRecord.setNotes(this.notes);
        paymentRecord.setStatus(this.status);
        
        if (this.paymentDate != null) {
            paymentRecord.setPaymentDate(this.paymentDate.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        return paymentRecord;
    }
}
