package com.cosmicdoc.opdmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A wrapper class for VisitHistory that handles the conversion for Firestore compatibility.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirestoreVisitHistory {
    
    private String id;
    private String patientId;
    private String visitDate;
    private String doctorId;
    private String doctorName;
    private List<String> symptoms;
    private String diagnosis;
    private String treatment;
    private List<Prescription> prescriptions;
    private String followUpDate;
    private String notes;
    
    /**
     * Creates a FirestoreVisitHistory from a standard VisitHistory entity
     */
    public static FirestoreVisitHistory fromVisitHistory(VisitHistory visitHistory) {
        if (visitHistory == null) {
            return null;
        }
        
        FirestoreVisitHistory firestoreVisitHistory = new FirestoreVisitHistory();
        firestoreVisitHistory.setId(visitHistory.getId());
        firestoreVisitHistory.setPatientId(visitHistory.getPatientId());
        firestoreVisitHistory.setVisitDate(visitHistory.getVisitDate());
        firestoreVisitHistory.setDoctorId(visitHistory.getDoctorId());
        firestoreVisitHistory.setDoctorName(visitHistory.getDoctorName());
        firestoreVisitHistory.setSymptoms(visitHistory.getSymptoms());
        firestoreVisitHistory.setDiagnosis(visitHistory.getDiagnosis());
        firestoreVisitHistory.setTreatment(visitHistory.getTreatment());
        firestoreVisitHistory.setPrescriptions(visitHistory.getPrescriptions());
        firestoreVisitHistory.setFollowUpDate(visitHistory.getFollowUpDate());
        firestoreVisitHistory.setNotes(visitHistory.getNotes());
        
        return firestoreVisitHistory;
    }
    
    /**
     * Converts this FirestoreVisitHistory to a standard VisitHistory entity
     */
    public VisitHistory toVisitHistory() {
        VisitHistory visitHistory = new VisitHistory();
        visitHistory.setId(this.id);
        visitHistory.setPatientId(this.patientId);
        visitHistory.setVisitDate(this.visitDate);
        visitHistory.setDoctorId(this.doctorId);
        visitHistory.setDoctorName(this.doctorName);
        visitHistory.setSymptoms(this.symptoms);
        visitHistory.setDiagnosis(this.diagnosis);
        visitHistory.setTreatment(this.treatment);
        visitHistory.setPrescriptions(this.prescriptions);
        visitHistory.setFollowUpDate(this.followUpDate);
        visitHistory.setNotes(this.notes);
        
        return visitHistory;
    }
}
