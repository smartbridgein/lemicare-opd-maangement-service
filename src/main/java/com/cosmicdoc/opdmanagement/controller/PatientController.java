package com.cosmicdoc.opdmanagement.controller;

import com.cosmicdoc.opdmanagement.dto.PatientDTO;
import com.cosmicdoc.opdmanagement.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    
    private final PatientService patientService;
    
    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }
    
    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable String id) {
        PatientDTO patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }
    
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<PatientDTO> getPatientByPhoneNumber(@PathVariable String phoneNumber) {
        PatientDTO patient = patientService.getPatientByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(patient);
    }
    
    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO patientDTO) {
        PatientDTO createdPatient = patientService.createPatient(patientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable String id,
            @Valid @RequestBody PatientDTO patientDTO) {
        PatientDTO updatedPatient = patientService.updatePatient(id, patientDTO);
        return ResponseEntity.ok(updatedPatient);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePatient(@PathVariable String id) {
        patientService.deletePatient(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Patient deleted successfully");
        response.put("data", true);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint to migrate legacy patient IDs to the new format
     * @return Response containing the count of updated patients
     */
    @PostMapping("/migrate-ids")
    public ResponseEntity<String> migratePatientIds() {
        int updatedCount = patientService.migratePatientIds();
        return ResponseEntity.ok("Migration complete. Updated " + updatedCount + " patient IDs.");
    }
}
