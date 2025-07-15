package com.cosmicdoc.opdmanagement.controller;

import com.cosmicdoc.opdmanagement.dto.VisitHistoryDTO;
import com.cosmicdoc.opdmanagement.response.ApiResponse;
import com.cosmicdoc.opdmanagement.service.VisitHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/visit-history")
public class VisitHistoryController {
    
    private final VisitHistoryService visitHistoryService;
    
    @Autowired
    public VisitHistoryController(VisitHistoryService visitHistoryService) {
        this.visitHistoryService = visitHistoryService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VisitHistoryDTO>> getVisitHistoryById(@PathVariable String id) {
        ApiResponse<VisitHistoryDTO> response = visitHistoryService.getVisitHistoryById(id);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(response);
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<VisitHistoryDTO>>> getVisitHistoryByPatientId(@PathVariable String patientId) {
        ApiResponse<List<VisitHistoryDTO>> response = visitHistoryService.getVisitHistoryByPatientId(patientId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<VisitHistoryDTO>> createVisitHistory(@Valid @RequestBody VisitHistoryDTO visitHistoryDTO) {
        ApiResponse<VisitHistoryDTO> response = visitHistoryService.createVisitHistory(visitHistoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VisitHistoryDTO>> updateVisitHistory(
            @PathVariable String id,
            @Valid @RequestBody VisitHistoryDTO visitHistoryDTO) {
        ApiResponse<VisitHistoryDTO> response = visitHistoryService.updateVisitHistory(id, visitHistoryDTO);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVisitHistory(@PathVariable String id) {
        ApiResponse<Void> response = visitHistoryService.deleteVisitHistory(id);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND).body(response);
    }
}
