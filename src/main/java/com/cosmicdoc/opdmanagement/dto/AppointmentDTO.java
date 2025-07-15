package com.cosmicdoc.opdmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private String appointmentId;
    
    @NotBlank(message = "Patient ID is required")
    private String patientId;
    
    @NotBlank(message = "Doctor ID is required")
    private String doctorId;
    
    @NotNull(message = "Appointment date/time is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDateTime;
    
    private String status = "SCHEDULED"; // SCHEDULED, COMPLETED, CANCELLED, REASSIGNED
    
    private String category;
    
    private String subCategory;
    
    private String notes;
    
    // GPS location fields
    private Double patientLatitude;
    private Double patientLongitude;
    private Double doctorLatitude;
    private Double doctorLongitude;
}
