package com.cosmicdoc.opdmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistory {
    
    private String id;
    
    private String patientId;
    
    private String bloodGroup;
    
    private List<String> allergies;
    
    private List<String> chronicDiseases;
    
    private List<String> currentMedications;
    
    private List<PastSurgery> pastSurgeries;
    
    private String familyHistory;
    
    private LocalDateTime lastUpdated;
}
