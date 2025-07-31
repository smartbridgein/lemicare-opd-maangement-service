package com.cosmicdoc.opdmanagement.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Patient {
    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String bloodGroup;
    // Replace single emergencyContact field with separate name and number fields
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String medicalHistory;
    private boolean active = true;
    private LocalDate registrationDate;
    private String allergies;
}
