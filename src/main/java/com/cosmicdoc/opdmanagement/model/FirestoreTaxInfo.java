package com.cosmicdoc.opdmanagement.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Firestore representation of TaxInfo
 */
@Data
@NoArgsConstructor
public class FirestoreTaxInfo {
    private String taxProfileName;
    private Double taxRate;
    
    public FirestoreTaxInfo(TaxInfo taxInfo) {
        this.taxProfileName = taxInfo.getTaxProfileName();
        this.taxRate = taxInfo.getTaxRate();
    }
    
    public TaxInfo toTaxInfo() {
        return new TaxInfo(this.taxProfileName, this.taxRate != null ? this.taxRate : 0.0);
    }
}
