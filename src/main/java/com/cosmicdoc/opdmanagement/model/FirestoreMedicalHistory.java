package com.cosmicdoc.opdmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper class for MedicalHistory that handles the conversion for Firestore compatibility.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirestoreMedicalHistory {
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    private String id;
    private String patientId;
    private String bloodGroup;
    private List<String> allergies;
    private List<String> chronicDiseases;
    private List<String> currentMedications;
    private List<FirestorePastSurgery> pastSurgeries;
    private String familyHistory;
    private String lastUpdated; // Stored as String in ISO format

    /**
     * Creates a FirestoreMedicalHistory from a standard MedicalHistory entity
     */
    public static FirestoreMedicalHistory fromMedicalHistory(MedicalHistory medicalHistory) {
        if (medicalHistory == null) {
            return null;
        }
        
        FirestoreMedicalHistory firestoreMedicalHistory = new FirestoreMedicalHistory();
        firestoreMedicalHistory.setId(medicalHistory.getId());
        firestoreMedicalHistory.setPatientId(medicalHistory.getPatientId());
        firestoreMedicalHistory.setBloodGroup(medicalHistory.getBloodGroup());
        firestoreMedicalHistory.setAllergies(medicalHistory.getAllergies());
        firestoreMedicalHistory.setChronicDiseases(medicalHistory.getChronicDiseases());
        firestoreMedicalHistory.setCurrentMedications(medicalHistory.getCurrentMedications());
        
        // Convert PastSurgery list to FirestorePastSurgery list
        List<FirestorePastSurgery> firestorePastSurgeries = new ArrayList<>();
        if (medicalHistory.getPastSurgeries() != null) {
            for (PastSurgery surgery : medicalHistory.getPastSurgeries()) {
                firestorePastSurgeries.add(FirestorePastSurgery.fromPastSurgery(surgery));
            }
        }
        firestoreMedicalHistory.setPastSurgeries(firestorePastSurgeries);
        
        firestoreMedicalHistory.setFamilyHistory(medicalHistory.getFamilyHistory());
        
        // Convert LocalDateTime to String
        if (medicalHistory.getLastUpdated() != null) {
            firestoreMedicalHistory.setLastUpdated(medicalHistory.getLastUpdated().format(DATE_TIME_FORMATTER));
        }
        
        return firestoreMedicalHistory;
    }
    
    /**
     * Converts this FirestoreMedicalHistory to a standard MedicalHistory entity
     */
    public MedicalHistory toMedicalHistory() {
        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setId(this.id);
        medicalHistory.setPatientId(this.patientId);
        medicalHistory.setBloodGroup(this.bloodGroup);
        medicalHistory.setAllergies(this.allergies);
        medicalHistory.setChronicDiseases(this.chronicDiseases);
        medicalHistory.setCurrentMedications(this.currentMedications);
        
        // Convert FirestorePastSurgery list to PastSurgery list
        List<PastSurgery> pastSurgeries = new ArrayList<>();
        if (this.pastSurgeries != null) {
            for (FirestorePastSurgery surgery : this.pastSurgeries) {
                pastSurgeries.add(surgery.toPastSurgery());
            }
        }
        medicalHistory.setPastSurgeries(pastSurgeries);
        
        medicalHistory.setFamilyHistory(this.familyHistory);
        
        // Convert String to LocalDateTime
        if (this.lastUpdated != null && !this.lastUpdated.isEmpty()) {
            medicalHistory.setLastUpdated(LocalDateTime.parse(this.lastUpdated, DATE_TIME_FORMATTER));
        }
        
        return medicalHistory;
    }
}
