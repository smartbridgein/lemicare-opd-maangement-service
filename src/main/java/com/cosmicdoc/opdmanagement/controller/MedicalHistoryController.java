package com.cosmicdoc.opdmanagement.controller;

import com.cosmicdoc.opdmanagement.dto.MedicalHistoryDTO;
import com.cosmicdoc.opdmanagement.response.ApiResponse;
import com.cosmicdoc.opdmanagement.service.MedicalHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/medical-history")
public class MedicalHistoryController {
    
    private final MedicalHistoryService medicalHistoryService;
    
    @Autowired
    public MedicalHistoryController(MedicalHistoryService medicalHistoryService) {
        this.medicalHistoryService = medicalHistoryService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicalHistoryDTO>> getMedicalHistoryById(@PathVariable String id) {
        ApiResponse<MedicalHistoryDTO> response = medicalHistoryService.getMedicalHistoryById(id);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(response);
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<MedicalHistoryDTO>> getMedicalHistoryByPatientId(@PathVariable String patientId) {
        ApiResponse<MedicalHistoryDTO> response = medicalHistoryService.getMedicalHistoryByPatientId(patientId);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(response);
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<MedicalHistoryDTO>> createMedicalHistory(@Valid @RequestBody MedicalHistoryDTO medicalHistoryDTO) {
        ApiResponse<MedicalHistoryDTO> response = medicalHistoryService.createMedicalHistory(medicalHistoryDTO);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicalHistoryDTO>> updateMedicalHistory(
            @PathVariable String id,
            @Valid @RequestBody MedicalHistoryDTO medicalHistoryDTO) {
        ApiResponse<MedicalHistoryDTO> response = medicalHistoryService.updateMedicalHistory(id, medicalHistoryDTO);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMedicalHistory(@PathVariable String id) {
        ApiResponse<Void> response = medicalHistoryService.deleteMedicalHistory(id);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND).body(response);
    }
}
