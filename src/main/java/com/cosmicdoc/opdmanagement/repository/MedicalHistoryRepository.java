package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.MedicalHistory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicalHistoryRepository {
    
    Optional<MedicalHistory> findById(String id);
    
    Optional<MedicalHistory> findByPatientId(String patientId);
    
    MedicalHistory save(MedicalHistory medicalHistory);
    
    void deleteById(String id);
    
    void deleteByPatientId(String patientId);
}
