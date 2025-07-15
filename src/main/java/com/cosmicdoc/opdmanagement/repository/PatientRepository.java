package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.Patient;
import java.util.List;
import java.util.Optional;

public interface PatientRepository {
    List<Patient> findAll();
    Optional<Patient> findById(String id);
    Optional<Patient> findByPhoneNumber(String phoneNumber);
    Optional<Patient> findByEmail(String email);
    Patient save(Patient patient);
    void deleteById(String id);
    boolean existsById(String id);
}
