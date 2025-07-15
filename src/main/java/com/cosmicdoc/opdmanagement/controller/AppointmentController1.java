package com.cosmicdoc.opdmanagement.controller;

import com.cosmicdoc.opdmanagement.dto.AppointmentDTO;
import com.cosmicdoc.opdmanagement.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController1 {
    
    private final AppointmentService appointmentService;
    
    @Autowired
    public AppointmentController1(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
    
    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        List<AppointmentDTO> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable String id) {
        AppointmentDTO appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByPatientId(@PathVariable String patientId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByPatientId(patientId);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByDoctorId(@PathVariable String doctorId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByStatus(@PathVariable String status) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByStatus(status);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/month")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsForMonth(
            @RequestParam int year,
            @RequestParam int month) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsForMonth(year, month);
        return ResponseEntity.ok(appointments);
    }
    
    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(@Valid @RequestBody AppointmentDTO appointmentDTO) {
        AppointmentDTO createdAppointment = appointmentService.createAppointment(appointmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> updateAppointment(
            @PathVariable String id,
            @Valid @RequestBody AppointmentDTO appointmentDTO) {
        AppointmentDTO updatedAppointment = appointmentService.updateAppointment(id, appointmentDTO);
        return ResponseEntity.ok(updatedAppointment);
    }
    
    @PutMapping("/{id}/reassign")
    public ResponseEntity<Map<String, Boolean>> reassignAppointment(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        String newDoctorId = request.get("newDoctorId");
        if (newDoctorId == null || newDoctorId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        boolean result = appointmentService.reassignAppointment(id, newDoctorId);
        return ResponseEntity.ok(Map.of("success", result));
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Boolean>> cancelAppointment(@PathVariable String id) {
        boolean result = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(Map.of("success", result));
    }
    
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<AppointmentDTO> rescheduleAppointment(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        String appointmentDateTime = request.get("appointmentDateTime");
        String doctorId = request.get("doctorId"); // Optional doctor ID for reassignment
        
        if (appointmentDateTime == null || appointmentDateTime.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Call the service method with both parameters
        AppointmentDTO updatedAppointment = appointmentService.rescheduleAppointment(id, appointmentDateTime, doctorId);
        return ResponseEntity.ok(updatedAppointment);
    }
    
    @PutMapping("/{id}/type")
    public ResponseEntity<AppointmentDTO> updateAppointmentType(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        String appointmentType = request.get("appointmentType");
        if (appointmentType == null || appointmentType.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        AppointmentDTO updatedAppointment = appointmentService.updateAppointmentType(id, appointmentType);
        return ResponseEntity.ok(updatedAppointment);
    }
    
    @GetMapping("/doctor/{doctorId}/availability")
    public ResponseEntity<List<LocalTime>> getDoctorAvailableTimeSlots(
            @PathVariable String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<LocalTime> availableSlots = appointmentService.getDoctorAvailableTimeSlots(doctorId, date);
        return ResponseEntity.ok(availableSlots);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}

