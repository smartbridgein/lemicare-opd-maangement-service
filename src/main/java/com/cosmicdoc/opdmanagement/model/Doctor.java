package com.cosmicdoc.opdmanagement.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String specialization;
    private String qualification;
    private String licenseNumber;
    private String hospital;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private boolean isAvailable;
    private String profileImage;
    private List<DoctorLeave> leaves = new ArrayList<>();
    private GpsLocation location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorLeave {
        private String id;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String reason;
        private String status; // PENDING, APPROVED, REJECTED
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GpsLocation {
        private double latitude;
        private double longitude;
    }
}
