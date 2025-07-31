package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.Appointment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {
    List<Appointment> findAll();
    Optional<Appointment> findById(String id);
    Appointment save(Appointment appointment);
    void deleteById(String id);
    boolean existsById(String id);
    List<Appointment> findByDoctorId(String doctorId);
    List<Appointment> findByPatientId(String patientId);
    List<Appointment> findByDate(LocalDate date);
    List<Appointment> findByDoctorIdAndDate(String doctorId, LocalDate date);
    boolean isDoctorAvailable(String doctorId, LocalDate date, LocalTime time);
    boolean reassignAppointment(String appointmentId, String newDoctorId);
    boolean cancelAppointment(String appointmentId);
    List<LocalTime> getDoctorAvailableTimeSlots(String doctorId, LocalDate date);
    
    // Additional methods implemented by OpdAppointmentRepositoryImpl
    List<Appointment> findByUserId(String userId);
    List<Appointment> findByStatus(String status);
    List<Appointment> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);
    List<Appointment> findByUserIdAndStatus(String userId, String status);
    List<Appointment> findByDoctorIdAndMonth(String doctorId, YearMonth month);
}
