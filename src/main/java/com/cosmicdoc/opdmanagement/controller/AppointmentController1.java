package com.cosmicdoc.opdmanagement.controller;

import com.cosmicdoc.opdmanagement.dto.AppointmentDTO;
import com.cosmicdoc.opdmanagement.service.AppointmentService;
import com.cosmicdoc.opdmanagement.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController1 {
    
    private final AppointmentService appointmentService;
    private final TokenService tokenService;
    
    @Autowired
    public AppointmentController1(AppointmentService appointmentService, TokenService tokenService) {
        this.appointmentService = appointmentService;
        this.tokenService = tokenService;
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
        // Get appointment date or use today if not specified
        LocalDate appointmentDate = appointmentDTO.getAppointmentDateTime() != null 
            ? appointmentDTO.getAppointmentDateTime().toLocalDate() 
            : LocalDate.now();
            
        // Generate a unique token number for this doctor on this date
        int tokenNumber = tokenService.generateTokenForDoctor(appointmentDTO.getDoctorId(), appointmentDate);
        
        // Set token properties in the appointment
        appointmentDTO.setTokenNumber(tokenNumber);
        appointmentDTO.setTokenStatus("WAITING");
        // Store the token time as a formatted string
        appointmentDTO.setTokenTime(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // Set token order to match token number for consistent sequencing
        appointmentDTO.setTokenOrder(tokenNumber);
        
        // Set patient name if available, or use patient ID as fallback
        if (appointmentDTO.getPatientName() == null || appointmentDTO.getPatientName().isEmpty()) {
            appointmentDTO.setPatientName("Patient #" + appointmentDTO.getPatientId());
        }
        
        AppointmentDTO createdAppointment = appointmentService.createAppointment(appointmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }
    
    /**
     * Get today's current active token
     */
    @GetMapping("/token/current/{doctorId}")
    public ResponseEntity<AppointmentDTO> getCurrentToken(@PathVariable String doctorId) {
        AppointmentDTO currentToken = tokenService.getCurrentToken(doctorId);
        if (currentToken == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(currentToken);
    }
    
    /**
     * Get count of waiting tokens for today
     */
    @GetMapping("/token/waiting-count/{doctorId}")
    public ResponseEntity<Map<String, Object>> getWaitingTokenCount(@PathVariable String doctorId) {
        LocalDate today = LocalDate.now();
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);
        
        long waitingCount = appointments.stream()
                .filter(app -> app.getAppointmentDateTime() != null 
                    && app.getAppointmentDateTime().toLocalDate().equals(today)
                    && "WAITING".equals(app.getTokenStatus()))
                .count();
                
        Map<String, Object> response = new HashMap<>();
        response.put("count", waitingCount);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Mark current token as complete and move to next
     */
    @PostMapping("/token/complete/{doctorId}")
    public ResponseEntity<AppointmentDTO> completeCurrentToken(@PathVariable String doctorId) {
        AppointmentDTO nextToken = tokenService.completeCurrentToken(doctorId);
        if (nextToken == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(nextToken);
    }
    
    /**
     * Skip current token and move to next
     */
    @PostMapping("/token/skip/{doctorId}")
    public ResponseEntity<AppointmentDTO> skipCurrentToken(@PathVariable String doctorId) {
        AppointmentDTO nextToken = tokenService.skipCurrentToken(doctorId);
        if (nextToken == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(nextToken);
    }
    
    /**
     * Update a token status directly
     */
    @PutMapping("/token/{id}/status/{status}")
    public ResponseEntity<AppointmentDTO> updateTokenStatus(
            @PathVariable String id,
            @PathVariable String status) {
        
        AppointmentDTO appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            return ResponseEntity.notFound().build();
        }
        
        appointment.setTokenStatus(status);
        if ("COMPLETED".equals(status)) {
            appointment.setStatus("COMPLETED");
        } else if ("CURRENT".equals(status)) {
            appointment.setStatus("ENGAGED");
        }
        
        AppointmentDTO updatedAppointment = appointmentService.updateAppointment(id, appointment);
        return ResponseEntity.ok(updatedAppointment);
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
    
    /**
     * Initialize token information for existing appointments without token data
     * This endpoint is for migration purposes only and should be called once
     */
    @PostMapping("/token/initialize")
    public ResponseEntity<Map<String, Object>> initializeTokensForExistingAppointments() {
        List<AppointmentDTO> allAppointments = appointmentService.getAllAppointments();
        int updatedCount = 0;
        int totalTokens = 0;
        
        // Group appointments by doctor and date
        Map<String, List<AppointmentDTO>> groupedAppointments = new HashMap<>();
        for (AppointmentDTO appointment : allAppointments) {
            if (appointment.getDoctorId() != null && appointment.getAppointmentDateTime() != null) {
                // Create a key using doctor ID and date part of appointment time
                String dateString = appointment.getAppointmentDateTime().toLocalDate().toString();
                String key = appointment.getDoctorId() + "_" + dateString;
                
                if (!groupedAppointments.containsKey(key)) {
                    groupedAppointments.put(key, new ArrayList<>());
                }
                
                groupedAppointments.get(key).add(appointment);
            }
        }
        
        // Process each group (doctor + date)
        for (Map.Entry<String, List<AppointmentDTO>> entry : groupedAppointments.entrySet()) {
            String key = entry.getKey();
            List<AppointmentDTO> appointmentsForDate = entry.getValue();
            
            if (!appointmentsForDate.isEmpty()) {
                // Reset token counter for each doctor-date combination
                int tokenCounter = 0;
                
                // Sort appointments by time
                appointmentsForDate.sort(Comparator.comparing(AppointmentDTO::getAppointmentDateTime));
                
                // Extract doctor ID from the key for logging
                String doctorId = key.split("_")[0];
                String dateStr = key.split("_")[1];
                System.out.println("Processing " + appointmentsForDate.size() + " appointments for doctor " + doctorId + " on date " + dateStr);
                
                // Assign tokens with order based on time
                for (int i = 0; i < appointmentsForDate.size(); i++) {
                    AppointmentDTO appointment = appointmentsForDate.get(i);
                    
                    // Increment token counter for this doctor-date combination
                    tokenCounter++;
                    
                    // Update token information
                    appointment.setTokenNumber(tokenCounter);
                    appointment.setTokenOrder(tokenCounter);
                    
                    // Only set the token status to WAITING if it's not already set to something else
                    // This preserves any CURRENT tokens we've already set
                    if (appointment.getTokenStatus() == null || !appointment.getTokenStatus().equals("CURRENT")) {
                        appointment.setTokenStatus("WAITING");
                    }
                    
                    // Set token time if not already set
                    if (appointment.getTokenTime() == null) {
                        appointment.setTokenTime(LocalDateTime.now()
                                .format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    }
                    
                    // Set patient name if not already set
                    if (appointment.getPatientName() == null || appointment.getPatientName().isEmpty()) {
                        appointment.setPatientName("Patient #" + appointment.getPatientId());
                    }
                    
                    System.out.println("Setting token " + tokenCounter + " for appointment " + appointment.getAppointmentId());
                    appointmentService.updateAppointment(appointment.getAppointmentId(), appointment);
                    updatedCount++;
                    totalTokens = Math.max(totalTokens, tokenCounter);
                }
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("updatedCount", updatedCount);
        response.put("totalTokensGenerated", totalTokens);
        return ResponseEntity.ok(response);
    }
}

