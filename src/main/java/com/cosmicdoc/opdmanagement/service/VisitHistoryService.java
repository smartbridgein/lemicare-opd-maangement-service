package com.cosmicdoc.opdmanagement.service;

import com.cosmicdoc.opdmanagement.dto.VisitHistoryDTO;
import com.cosmicdoc.opdmanagement.response.ApiResponse;

import java.util.List;

public interface VisitHistoryService {
    
    ApiResponse<VisitHistoryDTO> getVisitHistoryById(String id);
    
    ApiResponse<List<VisitHistoryDTO>> getVisitHistoryByPatientId(String patientId);
    
    ApiResponse<VisitHistoryDTO> createVisitHistory(VisitHistoryDTO visitHistoryDTO);
    
    ApiResponse<VisitHistoryDTO> updateVisitHistory(String id, VisitHistoryDTO visitHistoryDTO);
    
    ApiResponse<Void> deleteVisitHistory(String id);
}
