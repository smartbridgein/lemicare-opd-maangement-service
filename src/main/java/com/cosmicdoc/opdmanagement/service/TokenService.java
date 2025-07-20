package com.cosmicdoc.opdmanagement.service;

import com.cosmicdoc.opdmanagement.dto.AppointmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service to manage appointment tokens and queue
 */
@Service
public class TokenService {

    private final AppointmentService appointmentService;
    
    // Use a map to track token counters per doctor per date
    private java.util.Map<String, AtomicInteger> doctorDateTokenCounters = new java.util.HashMap<>();
    private LocalDate lastTokenDate = null;

    @Autowired
    public TokenService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
        
        // Initialize the counter based on existing appointments for today
        resetTokenCounterIfNeeded();
    }

    /**
     * Generate a new token number for an appointment today for specific doctor
     * @param doctorId The doctor's ID
     * @param appointmentDate The appointment date
     * @return the next token number for this doctor on this date
     */
    public int generateTokenForDoctor(String doctorId, LocalDate appointmentDate) {
        String key = generateDoctorDateKey(doctorId, appointmentDate);
        doctorDateTokenCounters.putIfAbsent(key, new AtomicInteger(0));
        
        // Find max existing token for this doctor on this date
        int maxExistingToken = findMaxTokenForDoctorOnDate(doctorId, appointmentDate);
        
        // Make sure our counter is at least as high as the max existing token
        AtomicInteger counter = doctorDateTokenCounters.get(key);
        while (counter.get() < maxExistingToken) {
            counter.incrementAndGet();
        }
        
        // Now increment and get the next value
        return counter.incrementAndGet();
    }
    
    /**
     * Generate a new token number for an appointment today
     * @return the next token number
     */
    public int generateTokenForToday() {
        resetTokenCounterIfNeeded();
        // Use current date and a default doctor ID (this method should be deprecated)
        return generateTokenForDoctor("default", LocalDate.now());
    }

    /**
     * Generate a key for the doctor-date token counter map
     */
    private String generateDoctorDateKey(String doctorId, LocalDate date) {
        return doctorId + "_" + date.toString();
    }
    
    /**
     * Find the highest token number used for a doctor on a specific date
     */
    private int findMaxTokenForDoctorOnDate(String doctorId, LocalDate date) {
        List<AppointmentDTO> doctorAppointments = appointmentService.getAppointmentsByDoctorId(doctorId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        return doctorAppointments.stream()
                .filter(app -> app.getAppointmentDateTime() != null 
                        && app.getAppointmentDateTime().toLocalDate().equals(date)
                        && app.getTokenNumber() != null)
                .mapToInt(AppointmentDTO::getTokenNumber)
                .max()
                .orElse(0); // Return 0 if no tokens found
    }
    
    /**
     * Reset token counters if date changes
     */
    private void resetTokenCounterIfNeeded() {
        LocalDate today = LocalDate.now();
        if (lastTokenDate == null || !lastTokenDate.equals(today)) {
            // Only clear counters if the date has changed
            doctorDateTokenCounters.clear();
            lastTokenDate = today;
            
            // Initialize counters based on existing appointments
            List<AppointmentDTO> todayAppointments = appointmentService.getAllAppointments();
            
            // Group by doctor and find max token for each
            for (AppointmentDTO appointment : todayAppointments) {
                if (appointment.getDoctorId() != null && appointment.getAppointmentDateTime() != null) {
                    LocalDate appointmentDate = appointment.getAppointmentDateTime().toLocalDate();
                    String doctorId = appointment.getDoctorId();
                    String key = generateDoctorDateKey(doctorId, appointmentDate);
                    
                    if (!doctorDateTokenCounters.containsKey(key)) {
                        doctorDateTokenCounters.put(key, new AtomicInteger(0));
                    }
                    
                    // Update counter if this appointment has a higher token number
                    if (appointment.getTokenNumber() != null) {
                        AtomicInteger counter = doctorDateTokenCounters.get(key);
                        while (counter.get() < appointment.getTokenNumber()) {
                            counter.incrementAndGet();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Calculate the order in queue based on appointment time
     * Earlier appointments get lower numbers (higher priority)
     */
    public int calculateTokenOrder(AppointmentDTO appointment) {
        if (appointment.getAppointmentDateTime() == null) {
            // If no time specified, put at end of queue
            return Integer.MAX_VALUE;
        }
        
        // Get all appointments for today with the same doctor
        List<AppointmentDTO> doctorAppointments = appointmentService.getAppointmentsByDoctorId(
                appointment.getDoctorId());
        
        // Filter to today's appointments
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        long queuePosition = doctorAppointments.stream()
                .filter(app -> app.getAppointmentDateTime() != null 
                        && app.getAppointmentDateTime().format(formatter).equals(today.format(formatter))
                        && !app.getAppointmentId().equals(appointment.getAppointmentId()))
                .count();
                
        return (int) queuePosition + 1;
    }
    
    /**
     * Get the current token being served
     * @return the current token appointment or null if no active token
     */
    public AppointmentDTO getCurrentToken(String doctorId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);
        
        // Filter to today's appointments with CURRENT token status
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        return appointments.stream()
                .filter(app -> app.getTokenStatus() != null 
                        && app.getTokenStatus().equals("CURRENT")
                        && app.getAppointmentDateTime() != null
                        && app.getAppointmentDateTime().format(formatter).equals(today.format(formatter)))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get the next token in the queue
     * @return the next appointment in the queue or null if no waiting appointments
     */
    public AppointmentDTO getNextToken(String doctorId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);
        
        // Filter to today's appointments with WAITING token status
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        return appointments.stream()
                .filter(app -> app.getTokenStatus() != null 
                        && app.getTokenStatus().equals("WAITING")
                        && app.getAppointmentDateTime() != null
                        && app.getAppointmentDateTime().format(formatter).equals(today.format(formatter)))
                .sorted((a1, a2) -> {
                    // Sort by token order first, then by token number
                    int orderCompare = Integer.compare(
                            a1.getTokenOrder() != null ? a1.getTokenOrder() : Integer.MAX_VALUE,
                            a2.getTokenOrder() != null ? a2.getTokenOrder() : Integer.MAX_VALUE
                    );
                    
                    if (orderCompare != 0) {
                        return orderCompare;
                    }
                    
                    return Integer.compare(
                            a1.getTokenNumber() != null ? a1.getTokenNumber() : Integer.MAX_VALUE,
                            a2.getTokenNumber() != null ? a2.getTokenNumber() : Integer.MAX_VALUE
                    );
                })
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Mark the current token as complete and activate the next token
     * @return the next active token, or null if none
     */
    public AppointmentDTO completeCurrentToken(String doctorId) {
        AppointmentDTO currentToken = getCurrentToken(doctorId);
        
        if (currentToken != null) {
            // Mark current token as completed
            currentToken.setTokenStatus("COMPLETED");
            currentToken.setStatus("COMPLETED");
            appointmentService.updateAppointment(currentToken.getAppointmentId(), currentToken);
        }
        
        // Find and activate the next token
        AppointmentDTO nextToken = getNextToken(doctorId);
        
        if (nextToken != null) {
            nextToken.setTokenStatus("CURRENT");
            nextToken.setStatus("ENGAGED");
            appointmentService.updateAppointment(nextToken.getAppointmentId(), nextToken);
            return nextToken;
        }
        
        return null;
    }
    
    /**
     * Skip the current token and move to next
     * @return the next active token, or null if none
     */
    public AppointmentDTO skipCurrentToken(String doctorId) {
        AppointmentDTO currentToken = getCurrentToken(doctorId);
        
        if (currentToken != null) {
            // Mark current token as skipped
            currentToken.setTokenStatus("SKIPPED");
            appointmentService.updateAppointment(currentToken.getAppointmentId(), currentToken);
        }
        
        // Find and activate the next token
        AppointmentDTO nextToken = getNextToken(doctorId);
        
        if (nextToken != null) {
            nextToken.setTokenStatus("CURRENT");
            nextToken.setStatus("ENGAGED");
            appointmentService.updateAppointment(nextToken.getAppointmentId(), nextToken);
            return nextToken;
        }
        
        return null;
    }
    
    // This duplicate method has been removed as it's already defined above
}
