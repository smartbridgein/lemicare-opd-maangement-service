package com.cosmicdoc.opdmanagement.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Firestore representation of a TaxComponent
 */
@Data
@NoArgsConstructor
public class FirestoreTaxComponent {
    private String name;
    private Double amount;
    
    public FirestoreTaxComponent(TaxComponent taxComponent) {
        this.name = taxComponent.getName();
        this.amount = taxComponent.getAmount();
    }
    
    public TaxComponent toTaxComponent() {
        return new TaxComponent(this.name, this.amount != null ? this.amount : 0.0);
    }
}
