package com.cosmicdoc.opdmanagement.model;

import com.cosmicdoc.opdmanagement.model.Patient;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A wrapper class for Patient that handles the conversion between LocalDate and String
 * for Firestore compatibility.
 */
@Data
@NoArgsConstructor
public class FirestorePatient {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String dateOfBirth; // Stored as String in ISO format (YYYY-MM-DD)
    private String gender;
    private String bloodGroup;
    private String allergies;
    private String medicalHistory;
    private String emergencyContactName;
    private String emergencyContactNumber;
    private boolean isActive;
    
    /**
     * Creates a FirestorePatient from a standard Patient entity
     */
    public static FirestorePatient fromPatient(Patient patient) {
        if (patient == null) {
            return null;
        }
        
        FirestorePatient firestorePatient = new FirestorePatient();
        firestorePatient.setId(patient.getId());
        firestorePatient.setName(patient.getName());
        firestorePatient.setEmail(patient.getEmail());
        firestorePatient.setPhoneNumber(patient.getPhoneNumber());
        firestorePatient.setAddress(patient.getAddress());
        
        // Convert LocalDate to String
        if (patient.getDateOfBirth() != null) {
            firestorePatient.setDateOfBirth(patient.getDateOfBirth().format(DATE_FORMATTER));
        }
        
        firestorePatient.setGender(patient.getGender());
        firestorePatient.setBloodGroup(patient.getBloodGroup());
        firestorePatient.setAllergies(patient.getAllergies());
        firestorePatient.setMedicalHistory(patient.getMedicalHistory());
        firestorePatient.setEmergencyContactName(patient.getEmergencyContactName());
        firestorePatient.setEmergencyContactNumber(patient.getEmergencyContactNumber());
        firestorePatient.setActive(patient.isActive());
        
        return firestorePatient;
    }
    
    /**
     * Converts this FirestorePatient to a standard Patient entity
     */
    public Patient toPatient() {
        Patient patient = new Patient();
        patient.setId(this.id);
        patient.setName(this.name);
        patient.setEmail(this.email);
        patient.setPhoneNumber(this.phoneNumber);
        patient.setAddress(this.address);
        
        // Convert String to LocalDate
        if (this.dateOfBirth != null && !this.dateOfBirth.isEmpty()) {
            patient.setDateOfBirth(LocalDate.parse(this.dateOfBirth, DATE_FORMATTER));
        }
        
        patient.setGender(this.gender);
        patient.setBloodGroup(this.bloodGroup);
        patient.setAllergies(this.allergies);
        patient.setMedicalHistory(this.medicalHistory);
        patient.setEmergencyContactName(this.emergencyContactName);
        patient.setEmergencyContactNumber(this.emergencyContactNumber);
        patient.setActive(this.isActive);
        
        return patient;
    }
}
