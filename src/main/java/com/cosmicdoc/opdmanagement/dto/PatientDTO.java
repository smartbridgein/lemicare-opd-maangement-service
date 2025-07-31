package com.cosmicdoc.opdmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {
    private String id;
    
    @NotBlank(message = "Patient name is required")
    private String name;
    
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10,15}", message = "Phone number must be between 10 and 15 digits")
    private String phoneNumber;
    
    private String address;
    
    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;
    
    @NotBlank(message = "Gender is required")
    private String gender;
    
    private String bloodGroup;
    
    private String allergies;
    
    private String medicalHistory;
    
    // Emergency Contact Fields
    private String emergencyContactName;
    
    @Pattern(regexp = "\\d{10,15}", message = "Emergency contact phone number must be between 10 and 15 digits")
    private String emergencyContactNumber;
    
    // Registration date - can be provided or will default to current date
    private LocalDate registrationDate;
    
    private boolean isActive = true;
}
