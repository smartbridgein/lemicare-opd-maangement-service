package com.cosmicdoc.opdmanagement.service;

import com.cosmicdoc.opdmanagement.dto.MedicalHistoryDTO;
import com.cosmicdoc.opdmanagement.response.ApiResponse;

public interface MedicalHistoryService {
    
    ApiResponse<MedicalHistoryDTO> getMedicalHistoryById(String id);
    
    ApiResponse<MedicalHistoryDTO> getMedicalHistoryByPatientId(String patientId);
    
    ApiResponse<MedicalHistoryDTO> createMedicalHistory(MedicalHistoryDTO medicalHistoryDTO);
    
    ApiResponse<MedicalHistoryDTO> updateMedicalHistory(String id, MedicalHistoryDTO medicalHistoryDTO);
    
    ApiResponse<Void> deleteMedicalHistory(String id);
}
