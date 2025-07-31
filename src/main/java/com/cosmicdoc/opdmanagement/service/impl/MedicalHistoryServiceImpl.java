package com.cosmicdoc.opdmanagement.service.impl;

import com.cosmicdoc.opdmanagement.dto.MedicalHistoryDTO;
import com.cosmicdoc.opdmanagement.model.MedicalHistory;
import com.cosmicdoc.opdmanagement.repository.MedicalHistoryRepository;
import com.cosmicdoc.opdmanagement.response.ApiResponse;
import com.cosmicdoc.opdmanagement.service.MedicalHistoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MedicalHistoryServiceImpl implements MedicalHistoryService {
    
    private final MedicalHistoryRepository medicalHistoryRepository;
    
    @Autowired
    public MedicalHistoryServiceImpl(MedicalHistoryRepository medicalHistoryRepository) {
        this.medicalHistoryRepository = medicalHistoryRepository;
    }
    
    @Override
    public ApiResponse<MedicalHistoryDTO> getMedicalHistoryById(String id) {
        Optional<MedicalHistory> medicalHistoryOpt = medicalHistoryRepository.findById(id);
        
        if (medicalHistoryOpt.isPresent()) {
            MedicalHistoryDTO dto = convertToDTO(medicalHistoryOpt.get());
            return ApiResponse.success("Medical history retrieved successfully", dto);
        } else {
            return ApiResponse.error("Medical history not found with ID: " + id);
        }
    }
    
    @Override
    public ApiResponse<MedicalHistoryDTO> getMedicalHistoryByPatientId(String patientId) {
        Optional<MedicalHistory> medicalHistoryOpt = medicalHistoryRepository.findByPatientId(patientId);
        
        if (medicalHistoryOpt.isPresent()) {
            MedicalHistoryDTO dto = convertToDTO(medicalHistoryOpt.get());
            return ApiResponse.success("Medical history retrieved successfully", dto);
        } else {
            return ApiResponse.error("No medical history found for patient ID: " + patientId);
        }
    }
    
    @Override
    public ApiResponse<MedicalHistoryDTO> createMedicalHistory(MedicalHistoryDTO medicalHistoryDTO) {
        // Check if medical history already exists for the patient
        Optional<MedicalHistory> existingHistory = medicalHistoryRepository.findByPatientId(medicalHistoryDTO.getPatientId());
        if (existingHistory.isPresent()) {
            return ApiResponse.error("Medical history already exists for this patient. Use update instead.");
        }
        
        MedicalHistory medicalHistory = convertToEntity(medicalHistoryDTO);
        medicalHistory.setLastUpdated(LocalDateTime.now());
        
        MedicalHistory savedHistory = medicalHistoryRepository.save(medicalHistory);
        return ApiResponse.success("Medical history created successfully", convertToDTO(savedHistory));
    }
    
    @Override
    public ApiResponse<MedicalHistoryDTO> updateMedicalHistory(String id, MedicalHistoryDTO medicalHistoryDTO) {
        Optional<MedicalHistory> existingHistoryOpt = medicalHistoryRepository.findById(id);
        
        if (existingHistoryOpt.isPresent()) {
            MedicalHistory existingHistory = existingHistoryOpt.get();
            MedicalHistory updatedHistory = convertToEntity(medicalHistoryDTO);
            
            // Preserve ID and update timestamp
            updatedHistory.setId(existingHistory.getId());
            updatedHistory.setLastUpdated(LocalDateTime.now());
            
            MedicalHistory savedHistory = medicalHistoryRepository.save(updatedHistory);
            return ApiResponse.success("Medical history updated successfully", convertToDTO(savedHistory));
        } else {
            return ApiResponse.error("Medical history not found with ID: " + id);
        }
    }
    
    @Override
    public ApiResponse<Void> deleteMedicalHistory(String id) {
        Optional<MedicalHistory> existingHistoryOpt = medicalHistoryRepository.findById(id);
        
        if (existingHistoryOpt.isPresent()) {
            medicalHistoryRepository.deleteById(id);
            return ApiResponse.success("Medical history deleted successfully", null);
        } else {
            return ApiResponse.error("Medical history not found with ID: " + id);
        }
    }
    
    private MedicalHistoryDTO convertToDTO(MedicalHistory medicalHistory) {
        if (medicalHistory == null) {
            return null;
        }
        
        MedicalHistoryDTO dto = new MedicalHistoryDTO();
        
        // Copy simple properties
        dto.setId(medicalHistory.getId());
        dto.setPatientId(medicalHistory.getPatientId());
        dto.setBloodGroup(medicalHistory.getBloodGroup());
        dto.setFamilyHistory(medicalHistory.getFamilyHistory());
        
        // Handle collection properties safely
        dto.setAllergies(medicalHistory.getAllergies() != null ? medicalHistory.getAllergies() : java.util.Collections.emptyList());
        dto.setChronicDiseases(medicalHistory.getChronicDiseases() != null ? medicalHistory.getChronicDiseases() : java.util.Collections.emptyList());
        dto.setCurrentMedications(medicalHistory.getCurrentMedications() != null ? medicalHistory.getCurrentMedications() : java.util.Collections.emptyList());
        
        // Handle nested objects list
        if (medicalHistory.getPastSurgeries() != null) {
            dto.setPastSurgeries(medicalHistory.getPastSurgeries());
        } else {
            dto.setPastSurgeries(java.util.Collections.emptyList());
        }
        
        // Handle date conversion
        if (medicalHistory.getLastUpdated() != null) {
            dto.setLastUpdated(medicalHistory.getLastUpdated().toString());
        }
        
        return dto;
    }
    
    private MedicalHistory convertToEntity(MedicalHistoryDTO dto) {
        if (dto == null) {
            return null;
        }
        
        MedicalHistory entity = new MedicalHistory();
        
        // Copy simple properties
        entity.setId(dto.getId());
        entity.setPatientId(dto.getPatientId());
        entity.setBloodGroup(dto.getBloodGroup());
        entity.setFamilyHistory(dto.getFamilyHistory());
        
        // Handle collection properties safely
        entity.setAllergies(dto.getAllergies() != null ? dto.getAllergies() : java.util.Collections.emptyList());
        entity.setChronicDiseases(dto.getChronicDiseases() != null ? dto.getChronicDiseases() : java.util.Collections.emptyList());
        entity.setCurrentMedications(dto.getCurrentMedications() != null ? dto.getCurrentMedications() : java.util.Collections.emptyList());
        
        // Handle nested objects list
        if (dto.getPastSurgeries() != null) {
            entity.setPastSurgeries(dto.getPastSurgeries());
        } else {
            entity.setPastSurgeries(java.util.Collections.emptyList());
        }
        
        // If lastUpdated was provided, try to parse it
        if (dto.getLastUpdated() != null && !dto.getLastUpdated().isEmpty()) {
            try {
                entity.setLastUpdated(LocalDateTime.parse(dto.getLastUpdated()));
            } catch (Exception e) {
                // If parsing fails, use current time
                entity.setLastUpdated(LocalDateTime.now());
            }
        } else {
            entity.setLastUpdated(LocalDateTime.now());
        }
        
        return entity;
    }
}
