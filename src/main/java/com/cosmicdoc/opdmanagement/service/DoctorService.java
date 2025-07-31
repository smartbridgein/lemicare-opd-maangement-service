package com.cosmicdoc.opdmanagement.service;

import com.cosmicdoc.opdmanagement.model.Doctor;
import com.cosmicdoc.opdmanagement.dto.DoctorLoginRequest;
import com.cosmicdoc.opdmanagement.exception.ResourceNotFoundException;
import com.cosmicdoc.opdmanagement.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {
    private final DoctorRepository doctorRepository;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }
    
    public Doctor getDoctorById(String id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }
    
    public Optional<Doctor> login(DoctorLoginRequest request) {
        // For testing purposes, using a simple email check
        // Password verification would need to be implemented separately
        return doctorRepository.findByEmail(request.getEmail());
    }

    public Doctor createDoctor(Doctor doctor) {
        // No password encoding for now
        // In production, use a secure password hashing library
        return doctorRepository.save(doctor);
    }
    
    public Doctor updateDoctor(String id, Doctor updatedDoctor) {
        Doctor existingDoctor = getDoctorById(id);
        
        existingDoctor.setName(updatedDoctor.getName());
        existingDoctor.setEmail(updatedDoctor.getEmail());
        existingDoctor.setSpecialization(updatedDoctor.getSpecialization());
        existingDoctor.setQualification(updatedDoctor.getQualification());
        existingDoctor.setPhoneNumber(updatedDoctor.getPhoneNumber());
        existingDoctor.setAddress(updatedDoctor.getAddress());
        existingDoctor.setHospital(updatedDoctor.getHospital());
        existingDoctor.setCity(updatedDoctor.getCity());
        existingDoctor.setState(updatedDoctor.getState());
        existingDoctor.setCountry(updatedDoctor.getCountry());
        existingDoctor.setZipCode(updatedDoctor.getZipCode());
        existingDoctor.setProfileImage(updatedDoctor.getProfileImage());
        existingDoctor.setLicenseNumber(updatedDoctor.getLicenseNumber());
        existingDoctor.setLocation(updatedDoctor.getLocation());
        existingDoctor.setAvailable(updatedDoctor.isAvailable());
        
        return doctorRepository.save(existingDoctor);
    }
    
    public boolean deleteDoctor(String id) {
        getDoctorById(id); // Verify doctor exists
        return doctorRepository.deleteById(id);
    }
    
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }
    
    public List<Doctor> getAvailableDoctors() {
        return doctorRepository.findAvailable();
    }
    
    public List<Doctor> getAvailableDoctorsBySpecialization(String specialization) {
        return doctorRepository.findAvailableBySpecialization(specialization);
    }
    
    public Doctor updateAvailability(String id, boolean isAvailable) {
        Doctor doctor = getDoctorById(id);
        
        boolean success = doctorRepository.updateAvailability(id, isAvailable);
        
        if (success) {
            doctor.setAvailable(isAvailable);
            return doctor;
        }
        
        throw new RuntimeException("Failed to update doctor availability");
    }
    
    public boolean addLeave(String doctorId, LocalDate startDate, LocalDate endDate, String reason) {
        getDoctorById(doctorId); // Verify doctor exists
        return doctorRepository.addLeave(doctorId, startDate, endDate, reason);
    }
    
    public boolean cancelLeave(String doctorId, String leaveId) {
        getDoctorById(doctorId); // Verify doctor exists
        return doctorRepository.cancelLeave(doctorId, leaveId);
    }
    
    public List<Doctor.DoctorLeave> getDoctorLeaves(String id) {
        getDoctorById(id); // Verify doctor exists
        return doctorRepository.getDoctorLeaves(id);
    }
    
    public boolean updateLocation(String id, double latitude, double longitude) {
        getDoctorById(id); // Verify doctor exists
        return doctorRepository.updateLocation(id, latitude, longitude);
    }
}
