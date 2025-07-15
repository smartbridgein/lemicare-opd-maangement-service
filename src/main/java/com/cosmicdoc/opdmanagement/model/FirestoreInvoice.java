package com.cosmicdoc.opdmanagement.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.Date;

/**
 * Firestore compatible wrapper for Invoice
 */
@Data
@NoArgsConstructor
public class FirestoreInvoice {
    @DocumentId
    private String id;
    private String patientId;
    private String patientName;
    private Timestamp date;
    private Double amount;
    private String createdBy;
    private String invoiceId;
    private Timestamp createdDate;
    
    public FirestoreInvoice(Invoice invoice) {
        this.id = invoice.getId();
        this.patientId = invoice.getPatientId();
        this.patientName = invoice.getPatientName();
        this.amount = invoice.getAmount();
        this.createdBy = invoice.getCreatedBy();
        this.invoiceId = invoice.getInvoiceId();
        
        if (invoice.getDate() != null) {
            this.date = Timestamp.of(Date.from(invoice.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        
        if (invoice.getCreatedDate() != null) {
            this.createdDate = Timestamp.of(Date.from(invoice.getCreatedDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            this.createdDate = Timestamp.now();
        }
    }
    
    public Invoice toInvoice() {
        Invoice invoice = new Invoice();
        invoice.setId(this.id);
        invoice.setPatientId(this.patientId);
        invoice.setPatientName(this.patientName);
        invoice.setAmount(this.amount != null ? this.amount : 0.0);
        invoice.setCreatedBy(this.createdBy);
        invoice.setInvoiceId(this.invoiceId);
        
        if (this.date != null) {
            invoice.setDate(this.date.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        if (this.createdDate != null) {
            invoice.setCreatedDate(this.createdDate.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        return invoice;
    }
}
