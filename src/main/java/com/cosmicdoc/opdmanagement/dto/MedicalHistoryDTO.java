package com.cosmicdoc.opdmanagement.dto;

import com.cosmicdoc.opdmanagement.model.PastSurgery;
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
public class MedicalHistoryDTO {
    
    private String id;
    
    private String patientId;
    
    private String bloodGroup;
    
    private List<String> allergies;
    
    private List<String> chronicDiseases;
    
    private List<String> currentMedications;
    
    private List<PastSurgery> pastSurgeries;
    
    private String familyHistory;
    
    private String lastUpdated;
}
