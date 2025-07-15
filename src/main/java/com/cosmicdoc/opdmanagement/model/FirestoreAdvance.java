package com.cosmicdoc.opdmanagement.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.Date;

/**
 * Firestore compatible wrapper for Advance
 */
@Data
@NoArgsConstructor
public class FirestoreAdvance {
    @DocumentId
    private String id;
    private String patientId;
    private String patientName;
    private Timestamp date;
    private Double amount;
    private String createdBy;
    private String modeOfPayment;
    private String advanceId;
    private Timestamp createdDate;
    
    public FirestoreAdvance(Advance advance) {
        this.id = advance.getId();
        this.patientId = advance.getPatientId();
        this.patientName = advance.getPatientName();
        this.amount = advance.getAmount();
        this.createdBy = advance.getCreatedBy();
        this.modeOfPayment = advance.getModeOfPayment();
        this.advanceId = advance.getAdvanceId();
        
        if (advance.getDate() != null) {
            this.date = Timestamp.of(Date.from(advance.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        
        if (advance.getCreatedDate() != null) {
            this.createdDate = Timestamp.of(Date.from(advance.getCreatedDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            this.createdDate = Timestamp.now();
        }
    }
    
    public Advance toAdvance() {
        Advance advance = new Advance();
        advance.setId(this.id);
        advance.setPatientId(this.patientId);
        advance.setPatientName(this.patientName);
        advance.setAmount(this.amount != null ? this.amount : 0.0);
        advance.setCreatedBy(this.createdBy);
        advance.setModeOfPayment(this.modeOfPayment);
        advance.setAdvanceId(this.advanceId);
        
        if (this.date != null) {
            advance.setDate(this.date.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        if (this.createdDate != null) {
            advance.setCreatedDate(this.createdDate.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        return advance;
    }
}
