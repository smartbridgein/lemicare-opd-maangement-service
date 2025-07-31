package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.VisitHistory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitHistoryRepository {
    
    Optional<VisitHistory> findById(String id);
    
    List<VisitHistory> findByPatientId(String patientId);
    
    VisitHistory save(VisitHistory visitHistory);
    
    void deleteById(String id);
    
    void deleteByPatientId(String patientId);
}
