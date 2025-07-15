package com.cosmicdoc.opdmanagement.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.Date;

/**
 * Firestore compatible wrapper for Refund
 */
@Data
@NoArgsConstructor
public class FirestoreRefund {
    @DocumentId
    private String id;
    private String patientId;
    private String patientName;
    private Timestamp date;
    private Double amount;
    private String createdBy;
    private String modeOfPayment;
    private String refundId;
    private String reason;
    private String originalBillId;
    private Timestamp createdDate;
    
    public FirestoreRefund(Refund refund) {
        this.id = refund.getId();
        this.patientId = refund.getPatientId();
        this.patientName = refund.getPatientName();
        this.amount = refund.getAmount();
        this.createdBy = refund.getCreatedBy();
        this.modeOfPayment = refund.getModeOfPayment();
        this.refundId = refund.getRefundId();
        this.reason = refund.getReason();
        this.originalBillId = refund.getOriginalBillId();
        
        if (refund.getDate() != null) {
            this.date = Timestamp.of(Date.from(refund.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        
        if (refund.getCreatedDate() != null) {
            this.createdDate = Timestamp.of(Date.from(refund.getCreatedDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            this.createdDate = Timestamp.now();
        }
    }
    
    public Refund toRefund() {
        Refund refund = new Refund();
        refund.setId(this.id);
        refund.setPatientId(this.patientId);
        refund.setPatientName(this.patientName);
        refund.setAmount(this.amount != null ? this.amount : 0.0);
        refund.setCreatedBy(this.createdBy);
        refund.setModeOfPayment(this.modeOfPayment);
        refund.setRefundId(this.refundId);
        refund.setReason(this.reason);
        refund.setOriginalBillId(this.originalBillId);
        
        if (this.date != null) {
            refund.setDate(this.date.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        if (this.createdDate != null) {
            refund.setCreatedDate(this.createdDate.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        return refund;
    }
}
