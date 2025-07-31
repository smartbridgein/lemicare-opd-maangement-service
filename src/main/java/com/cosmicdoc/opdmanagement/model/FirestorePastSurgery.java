package com.cosmicdoc.opdmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A wrapper class for PastSurgery that handles the conversion for Firestore compatibility.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirestorePastSurgery {
    
    private String surgeryName;
    private String date;
    private String hospital;
    private String notes;
    
    /**
     * Creates a FirestorePastSurgery from a standard PastSurgery entity
     */
    public static FirestorePastSurgery fromPastSurgery(PastSurgery surgery) {
        if (surgery == null) {
            return null;
        }
        
        return new FirestorePastSurgery(
            surgery.getSurgeryName(),
            surgery.getDate(),
            surgery.getHospital(),
            surgery.getNotes()
        );
    }
    
    /**
     * Converts this FirestorePastSurgery to a standard PastSurgery entity
     */
    public PastSurgery toPastSurgery() {
        return new PastSurgery(
            this.surgeryName,
            this.date,
            this.hospital,
            this.notes
        );
    }
}
