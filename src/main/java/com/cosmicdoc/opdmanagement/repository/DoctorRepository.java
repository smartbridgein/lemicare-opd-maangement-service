package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.Doctor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository {
    
    List<Doctor> findAll();
    
    Optional<Doctor> findById(String id);
    
    Optional<Doctor> findByEmail(String email);
    
    List<Doctor> findBySpecialization(String specialization);
    
    Doctor save(Doctor doctor);
    
    boolean deleteById(String id);
    
    boolean existsById(String id);
    
    List<Doctor> findAvailable();
    
    List<Doctor> findAvailableBySpecialization(String specialization);
    
    boolean updateAvailability(String id, boolean isAvailable);
    
    boolean addLeave(String id, LocalDate startDate, LocalDate endDate, String reason);
    
    boolean cancelLeave(String id, String leaveId);
    
    List<Doctor.DoctorLeave> getDoctorLeaves(String id);
    
    boolean updateLocation(String id, double latitude, double longitude);
}
