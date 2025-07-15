package com.cosmicdoc.opdmanagement.model;

import com.google.cloud.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper class for Appointment that handles the conversion between Timestamp
 * objects and string representations for Firestore compatibility.
 */
@Data
@NoArgsConstructor
public class FirestoreAppointment {
    
    private String appointmentId;
    private String userId;
    private String doctorId;
    private Timestamp appointmentDate;
    private String status;
    private String category;
    private String subCategory;
    private GpsLocation patientGpsLocation;
    private GpsLocation doctorGpsLocation;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String notes;
    private Timestamp date;
    
    @Data
    @NoArgsConstructor
    public static class GpsLocation {
        private double latitude;
        private double longitude;
        
        public static GpsLocation fromAppointmentGpsLocation(com.cosmicdoc.opdmanagement.model.Appointment.GpsLocation location) {
            if (location == null) {
                return null;
            }
            
            GpsLocation firestoreLocation = new GpsLocation();
            firestoreLocation.setLatitude(location.getLatitude());
            firestoreLocation.setLongitude(location.getLongitude());
            
            return firestoreLocation;
        }
        
        public com.cosmicdoc.opdmanagement.model.Appointment.GpsLocation toAppointmentGpsLocation() {
            com.cosmicdoc.opdmanagement.model.Appointment.GpsLocation location = new com.cosmicdoc.opdmanagement.model.Appointment.GpsLocation();
            location.setLatitude(this.latitude);
            location.setLongitude(this.longitude);
            
            return location;
        }
    }
    
    /**
     * Creates a FirestoreAppointment from a standard Appointment entity
     */
    public static FirestoreAppointment fromAppointment(com.cosmicdoc.opdmanagement.model.Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        
        FirestoreAppointment firestoreAppointment = new FirestoreAppointment();
        firestoreAppointment.setAppointmentId(appointment.getAppointmentId());
        firestoreAppointment.setUserId(appointment.getUserId());
        firestoreAppointment.setDoctorId(appointment.getDoctorId());
        firestoreAppointment.setAppointmentDate(appointment.getAppointmentDate());
        firestoreAppointment.setStatus(appointment.getStatus());
        firestoreAppointment.setCategory(appointment.getCategory());
        firestoreAppointment.setSubCategory(appointment.getSubCategory());
        
        if (appointment.getPatientGpsLocation() != null) {
            firestoreAppointment.setPatientGpsLocation(
                GpsLocation.fromAppointmentGpsLocation(appointment.getPatientGpsLocation())
            );
        }
        
        if (appointment.getDoctorGpsLocation() != null) {
            firestoreAppointment.setDoctorGpsLocation(
                GpsLocation.fromAppointmentGpsLocation(appointment.getDoctorGpsLocation())
            );
        }
        
        firestoreAppointment.setCreatedAt(appointment.getCreatedAt());
        firestoreAppointment.setUpdatedAt(appointment.getUpdatedAt());
        firestoreAppointment.setNotes(appointment.getNotes());
        firestoreAppointment.setDate(appointment.getDate());
        
        return firestoreAppointment;
    }
    
    /**
     * Converts this FirestoreAppointment to a standard Appointment entity
     */
    public Appointment toAppointment() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(this.appointmentId);
        appointment.setUserId(this.userId);
        appointment.setDoctorId(this.doctorId);
        appointment.setAppointmentDate(this.appointmentDate);
        appointment.setStatus(this.status);
        appointment.setCategory(this.category);
        appointment.setSubCategory(this.subCategory);
        
        if (this.patientGpsLocation != null) {
            appointment.setPatientGpsLocation(this.patientGpsLocation.toAppointmentGpsLocation());
        }
        
        if (this.doctorGpsLocation != null) {
            appointment.setDoctorGpsLocation(this.doctorGpsLocation.toAppointmentGpsLocation());
        }
        
        appointment.setCreatedAt(this.createdAt);
        appointment.setUpdatedAt(this.updatedAt);
        appointment.setNotes(this.notes);
        appointment.setDate(this.date);
        
        return appointment;
    }
    
    /**
     * Convert a list of FirestoreAppointments to a list of Appointments
     */
    public static List<Appointment> toAppointmentList(List<FirestoreAppointment> firestoreAppointments) {
        List<Appointment> appointments = new ArrayList<>();
        if (firestoreAppointments != null) {
            for (FirestoreAppointment firestoreAppointment : firestoreAppointments) {
                appointments.add(firestoreAppointment.toAppointment());
            }
        }
        return appointments;
    }
    
    /**
     * Convert a list of Appointments to a list of FirestoreAppointments
     */
    public static List<FirestoreAppointment> fromAppointmentList(List<Appointment> appointments) {
        List<FirestoreAppointment> firestoreAppointments = new ArrayList<>();
        if (appointments != null) {
            for (Appointment appointment : appointments) {
                firestoreAppointments.add(FirestoreAppointment.fromAppointment(appointment));
            }
        }
        return firestoreAppointments;
    }
}
