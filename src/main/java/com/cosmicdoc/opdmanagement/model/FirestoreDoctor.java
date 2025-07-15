package com.cosmicdoc.opdmanagement.model;

import com.cosmicdoc.opdmanagement.model.Doctor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A wrapper class for Doctor that handles Firestore compatibility.
 */
@Data
@NoArgsConstructor
public class FirestoreDoctor {
    
    @Data
    public static class Schedule {
        private List<String> days;
        private List<String> timeSlots;
        
        public Schedule() {
            days = new ArrayList<>();
            timeSlots = new ArrayList<>();
        }
    }
    
    @Data
    public static class Leave {
        private String startDate;
        private String endDate;
        private String reason;
        private String status;  // PENDING, APPROVED, REJECTED
    }
    
    private String id;
    private String name;
    private String email;
    private String password;
    private String specialization;
    private String qualification;
    private String experience;
    private String phoneNumber;
    private String address;
    private List<String> availableDays;
    private List<String> availableTimeSlots;
    private boolean isActive;
    private Schedule schedule;
    private List<Leave> leaves;
    private Double latitude;
    private Double longitude;
    
    // Convert from FirestoreDoctor to Doctor
    public Doctor toDoctor() {
        Doctor doctor = new Doctor();
        doctor.setId(this.id);
        doctor.setName(this.name);
        doctor.setEmail(this.email);
        doctor.setSpecialization(this.specialization);
        doctor.setQualification(this.qualification);
        doctor.setPhoneNumber(this.phoneNumber);
        doctor.setAddress(this.address);
        doctor.setAvailable(this.isActive);
        
        // Set GPS location if available
        if (this.latitude != null && this.longitude != null) {
            Doctor.GpsLocation location = new Doctor.GpsLocation();
            location.setLatitude(this.latitude);
            location.setLongitude(this.longitude);
            doctor.setLocation(location);
        }
        
        return doctor;
    }
    
    // Convert from Doctor to FirestoreDoctor
    public static FirestoreDoctor fromDoctor(Doctor doctor) {
        FirestoreDoctor firestoreDoctor = new FirestoreDoctor();
        firestoreDoctor.setId(doctor.getId());
        firestoreDoctor.setName(doctor.getName());
        firestoreDoctor.setEmail(doctor.getEmail());
        firestoreDoctor.setSpecialization(doctor.getSpecialization());
        firestoreDoctor.setQualification(doctor.getQualification());
        firestoreDoctor.setPhoneNumber(doctor.getPhoneNumber());
        firestoreDoctor.setAddress(doctor.getAddress());
        firestoreDoctor.setActive(doctor.isAvailable());
        firestoreDoctor.setLeaves(new ArrayList<>());
        
        // Set GPS coordinates if location exists
        if (doctor.getLocation() != null) {
            firestoreDoctor.setLatitude(doctor.getLocation().getLatitude());
            firestoreDoctor.setLongitude(doctor.getLocation().getLongitude());
        }
        
        return firestoreDoctor;
    }

    // Convert to a Map for Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> docMap = new HashMap<>();
        docMap.put("id", this.id);
        docMap.put("name", this.name);
        docMap.put("email", this.email);
        docMap.put("password", this.password);
        docMap.put("specialization", this.specialization);
        docMap.put("qualification", this.qualification);
        docMap.put("experience", this.experience);
        docMap.put("phoneNumber", this.phoneNumber);
        docMap.put("address", this.address);
        docMap.put("availableDays", this.availableDays);
        docMap.put("availableTimeSlots", this.availableTimeSlots);
        docMap.put("isActive", this.isActive);
        
        if (this.schedule != null) {
            Map<String, Object> scheduleMap = new HashMap<>();
            scheduleMap.put("days", this.schedule.getDays());
            scheduleMap.put("timeSlots", this.schedule.getTimeSlots());
            docMap.put("schedule", scheduleMap);
        }
        
        docMap.put("leaves", this.leaves);
        
        if (this.latitude != null) {
            docMap.put("latitude", this.latitude);
        }
        
        if (this.longitude != null) {
            docMap.put("longitude", this.longitude);
        }
        
        return docMap;
    }
}
