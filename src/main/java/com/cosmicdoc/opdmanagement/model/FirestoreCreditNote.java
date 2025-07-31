package com.cosmicdoc.opdmanagement.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.Date;

/**
 * Firestore compatible wrapper for CreditNote
 */
@Data
@NoArgsConstructor
public class FirestoreCreditNote {
    @DocumentId
    private String id;
    private String patientId;
    private String patientName;
    private Timestamp date;
    private Double amount;
    private String createdBy;
    private String creditNoteId;
    private String reason;
    private Timestamp createdDate;
    
    public FirestoreCreditNote(CreditNote creditNote) {
        this.id = creditNote.getId();
        this.patientId = creditNote.getPatientId();
        this.patientName = creditNote.getPatientName();
        this.amount = creditNote.getAmount();
        this.createdBy = creditNote.getCreatedBy();
        this.creditNoteId = creditNote.getCreditNoteId();
        this.reason = creditNote.getReason();
        
        if (creditNote.getDate() != null) {
            this.date = Timestamp.of(Date.from(creditNote.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        
        if (creditNote.getCreatedDate() != null) {
            this.createdDate = Timestamp.of(Date.from(creditNote.getCreatedDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            this.createdDate = Timestamp.now();
        }
    }
    
    public CreditNote toCreditNote() {
        CreditNote creditNote = new CreditNote();
        creditNote.setId(this.id);
        creditNote.setPatientId(this.patientId);
        creditNote.setPatientName(this.patientName);
        creditNote.setAmount(this.amount != null ? this.amount : 0.0);
        creditNote.setCreatedBy(this.createdBy);
        creditNote.setCreditNoteId(this.creditNoteId);
        creditNote.setReason(this.reason);
        
        if (this.date != null) {
            creditNote.setDate(this.date.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        if (this.createdDate != null) {
            creditNote.setCreatedDate(this.createdDate.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        return creditNote;
    }
}
