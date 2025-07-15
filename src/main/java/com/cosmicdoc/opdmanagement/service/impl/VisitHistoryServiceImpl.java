package com.cosmicdoc.opdmanagement.service.impl;

import com.cosmicdoc.opdmanagement.dto.VisitHistoryDTO;
import com.cosmicdoc.opdmanagement.model.VisitHistory;
import com.cosmicdoc.opdmanagement.repository.VisitHistoryRepository;
import com.cosmicdoc.opdmanagement.response.ApiResponse;
import com.cosmicdoc.opdmanagement.service.VisitHistoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VisitHistoryServiceImpl implements VisitHistoryService {
    
    private final VisitHistoryRepository visitHistoryRepository;
    
    @Autowired
    public VisitHistoryServiceImpl(VisitHistoryRepository visitHistoryRepository) {
        this.visitHistoryRepository = visitHistoryRepository;
    }
    
    @Override
    public ApiResponse<VisitHistoryDTO> getVisitHistoryById(String id) {
        Optional<VisitHistory> visitHistoryOpt = visitHistoryRepository.findById(id);
        
        if (visitHistoryOpt.isPresent()) {
            VisitHistoryDTO dto = convertToDTO(visitHistoryOpt.get());
            return ApiResponse.success("Visit history retrieved successfully", dto);
        } else {
            return ApiResponse.error("Visit history not found with ID: " + id);
        }
    }
    
    @Override
    public ApiResponse<List<VisitHistoryDTO>> getVisitHistoryByPatientId(String patientId) {
        List<VisitHistory> visitHistories = visitHistoryRepository.findByPatientId(patientId);
        
        if (!visitHistories.isEmpty()) {
            List<VisitHistoryDTO> dtoList = visitHistories.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ApiResponse.success("Visit histories retrieved successfully", dtoList);
        } else {
            return ApiResponse.success("No visit histories found for patient ID: " + patientId, List.of());
        }
    }
    
    @Override
    public ApiResponse<VisitHistoryDTO> createVisitHistory(VisitHistoryDTO visitHistoryDTO) {
        VisitHistory visitHistory = convertToEntity(visitHistoryDTO);
        
        VisitHistory savedHistory = visitHistoryRepository.save(visitHistory);
        return ApiResponse.success("Visit history created successfully", convertToDTO(savedHistory));
    }
    
    @Override
    public ApiResponse<VisitHistoryDTO> updateVisitHistory(String id, VisitHistoryDTO visitHistoryDTO) {
        Optional<VisitHistory> existingHistoryOpt = visitHistoryRepository.findById(id);
        
        if (existingHistoryOpt.isPresent()) {
            VisitHistory updatedHistory = convertToEntity(visitHistoryDTO);
            
            // Preserve ID
            updatedHistory.setId(existingHistoryOpt.get().getId());
            
            VisitHistory savedHistory = visitHistoryRepository.save(updatedHistory);
            return ApiResponse.success("Visit history updated successfully", convertToDTO(savedHistory));
        } else {
            return ApiResponse.error("Visit history not found with ID: " + id);
        }
    }
    
    @Override
    public ApiResponse<Void> deleteVisitHistory(String id) {
        Optional<VisitHistory> existingHistoryOpt = visitHistoryRepository.findById(id);
        
        if (existingHistoryOpt.isPresent()) {
            visitHistoryRepository.deleteById(id);
            return ApiResponse.success("Visit history deleted successfully", null);
        } else {
            return ApiResponse.error("Visit history not found with ID: " + id);
        }
    }
    
    private VisitHistoryDTO convertToDTO(VisitHistory visitHistory) {
        VisitHistoryDTO dto = new VisitHistoryDTO();
        BeanUtils.copyProperties(visitHistory, dto);
        return dto;
    }
    
    private VisitHistory convertToEntity(VisitHistoryDTO dto) {
        VisitHistory entity = new VisitHistory();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
