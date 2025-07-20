package com.cosmicdoc.opdmanagement.model;

import com.google.cloud.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@NoArgsConstructor
public class Appointment {
    private String appointmentId;
    private String userId;  // patientId
    private String doctorId;
    private Timestamp appointmentDate; // replaces date + startTime
    private String status; // SCHEDULED, COMPLETED, CANCELLED, RESCHEDULED
    private String category;
    private String subCategory;
    private GpsLocation patientGpsLocation;
    private GpsLocation doctorGpsLocation;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String notes;
    private Timestamp date;
    private String reason;
    private String diagnosis;
    private String treatment;
    
    // Token system fields
    private Integer tokenNumber;
    private String tokenStatus;  // WAITING, CURRENT, COMPLETED, SKIPPED
    private String tokenTime;    // Time when token was assigned
    private Integer tokenOrder;  // Order based on appointment time
    private String patientName;  // For displaying patient name with token
    
    @Data
    @NoArgsConstructor
    public static class GpsLocation {
        private double latitude;
        private double longitude;
    }
    
    /**
     * Convert the Timestamp appointmentDate to LocalDateTime
     * @return LocalDateTime representation of appointment date
     */
    public LocalDateTime getAppointmentDateAsLocalDateTime() {
        if (appointmentDate == null) {
            return null;
        }
        return LocalDateTime.ofInstant(
            Instant.ofEpochSecond(
                appointmentDate.getSeconds(), 
                appointmentDate.getNanos()
            ),
            ZoneId.systemDefault()
        );
    }
}
