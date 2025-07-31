package com.cosmicdoc.opdmanagement.controller;

import com.cosmicdoc.opdmanagement.model.Doctor;
import com.cosmicdoc.opdmanagement.response.ApiResponse;
import com.cosmicdoc.opdmanagement.dto.DoctorLoginRequest;
import com.cosmicdoc.opdmanagement.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Slf4j
public class DoctorController {
    private final DoctorService doctorService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Doctor>>> getAllDoctors() {
        log.info("Request received for all doctors");
        List<Doctor> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(ApiResponse.success("Doctors retrieved successfully", doctors));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Doctor>> getDoctorById(@PathVariable String id) {
        log.info("Request received for doctor with id: {}", id);
        Doctor doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(ApiResponse.success("Doctor retrieved successfully", doctor));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Doctor>> login(@RequestBody DoctorLoginRequest request) {
        log.info("Doctor login request received for email: {}", request.getEmail());
        return doctorService.login(request)
                .map(doctor -> ResponseEntity.ok(ApiResponse.success("Login successful", doctor)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid credentials")));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Doctor>> createDoctor(@RequestBody Doctor doctor) {
        log.info("Request received to create new doctor");
        Doctor createdDoctor = doctorService.createDoctor(doctor);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Doctor created successfully", createdDoctor));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Doctor>> updateDoctor(@PathVariable String id, @RequestBody Doctor doctor) {
        log.info("Request received to update doctor with id: {}", id);
        Doctor updatedDoctor = doctorService.updateDoctor(id, doctor);
        return ResponseEntity.ok(ApiResponse.success("Doctor updated successfully", updatedDoctor));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDoctor(@PathVariable String id) {
        log.info("Request received to delete doctor with id: {}", id);
        boolean deleted = doctorService.deleteDoctor(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("Doctor deleted successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete doctor"));
        }
    }
    
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<ApiResponse<List<Doctor>>> getDoctorsBySpecialization(@PathVariable String specialization) {
        log.info("Request received for doctors with specialization: {}", specialization);
        List<Doctor> doctors = doctorService.getDoctorsBySpecialization(specialization);
        return ResponseEntity.ok(ApiResponse.success("Doctors retrieved successfully", doctors));
    }
    
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Doctor>>> getAvailableDoctors() {
        log.info("Request received for available doctors");
        List<Doctor> doctors = doctorService.getAvailableDoctors();
        return ResponseEntity.ok(ApiResponse.success("Available doctors retrieved successfully", doctors));
    }
    
    @GetMapping("/available/{specialization}")
    public ResponseEntity<ApiResponse<List<Doctor>>> getAvailableDoctorsBySpecialization(@PathVariable String specialization) {
        log.info("Request received for available doctors with specialization: {}", specialization);
        List<Doctor> doctors = doctorService.getAvailableDoctorsBySpecialization(specialization);
        return ResponseEntity.ok(ApiResponse.success("Available doctors retrieved successfully", doctors));
    }
    
    @PatchMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<Doctor>> updateAvailability(
            @PathVariable String id,
            @RequestParam boolean isAvailable) {
        log.info("Request received to update availability for doctor: {} to {}", id, isAvailable);
        Doctor doctor = doctorService.updateAvailability(id, isAvailable);
        return ResponseEntity.ok(ApiResponse.success("Doctor availability updated successfully", doctor));
    }
    
    @PostMapping("/{id}/leave")
    public ResponseEntity<ApiResponse<Boolean>> addLeave(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String reason) {
        log.info("Request received to add leave for doctor: {} from {} to {}", id, startDate, endDate);
        boolean success = doctorService.addLeave(id, startDate, endDate, reason);
        if (success) {
            return ResponseEntity.ok(ApiResponse.success("Leave added successfully", true));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to add leave"));
        }
    }
    
    @DeleteMapping("/{id}/leave/{leaveId}")
    public ResponseEntity<ApiResponse<Boolean>> cancelLeave(
            @PathVariable String id,
            @PathVariable String leaveId) {
        log.info("Request received to cancel leave: {} for doctor: {}", leaveId, id);
        boolean success = doctorService.cancelLeave(id, leaveId);
        if (success) {
            return ResponseEntity.ok(ApiResponse.success("Leave cancelled successfully", true));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to cancel leave"));
        }
    }
    
    @GetMapping("/{id}/leaves")
    public ResponseEntity<ApiResponse<List<Doctor.DoctorLeave>>> getDoctorLeaves(@PathVariable String id) {
        log.info("Request received for leaves of doctor: {}", id);
        List<Doctor.DoctorLeave> leaves = doctorService.getDoctorLeaves(id);
        return ResponseEntity.ok(ApiResponse.success("Doctor leaves retrieved successfully", leaves));
    }
    
    @PatchMapping("/{id}/location")
    public ResponseEntity<ApiResponse<Boolean>> updateLocation(
            @PathVariable String id,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        log.info("Request received to update location for doctor: {} to ({}, {})", id, latitude, longitude);
        boolean success = doctorService.updateLocation(id, latitude, longitude);
        if (success) {
            return ResponseEntity.ok(ApiResponse.success("Doctor location updated successfully", true));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update doctor location"));
        }
    }
}
