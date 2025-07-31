package com.cosmicdoc.opdmanagement.service;

import com.cosmicdoc.opdmanagement.model.Advance;
import com.cosmicdoc.opdmanagement.repository.AdvanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Advance operations
 */
@Service
@Slf4j
public class AdvanceService {

    private final AdvanceRepository advanceRepository;

    @Autowired
    public AdvanceService(AdvanceRepository advanceRepository) {
        this.advanceRepository = advanceRepository;
    }

    /**
     * Create a new advance payment
     *
     * @param advance The advance to create
     * @return The created advance
     */
    public Advance createAdvance(Advance advance) {
        log.info("Creating advance payment for patient: {}", advance.getPatientId());
        return advanceRepository.save(advance);
    }

    /**
     * Retrieve all advances
     *
     * @return List of all advances
     */
    public List<Advance> getAllAdvances() {
        log.info("Retrieving all advances");
        return advanceRepository.findAll();
    }

    /**
     * Retrieve an advance by ID
     *
     * @param id The advance ID
     * @return Optional containing the advance if found
     */
    public Optional<Advance> getAdvanceById(String id) {
        log.info("Retrieving advance with ID: {}", id);
        return advanceRepository.findById(id);
    }

    /**
     * Retrieve advances for a specific patient
     *
     * @param patientId The patient ID
     * @return List of advances for the patient
     */
    public List<Advance> getAdvancesByPatientId(String patientId) {
        log.info("Retrieving advances for patient: {}", patientId);
        return advanceRepository.findByPatientId(patientId);
    }

    /**
     * Update an existing advance
     *
     * @param id The advance ID
     * @param updatedAdvance The updated advance details
     * @return The updated advance
     * @throws RuntimeException if the advance is not found
     */
    public Advance updateAdvance(String id, Advance updatedAdvance) {
        log.info("Updating advance with ID: {}", id);
        
        Advance existingAdvance = advanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Advance not found with ID: " + id));
        
        // Update fields while preserving ID and advance ID
        updatedAdvance.setId(existingAdvance.getId());
        if (updatedAdvance.getAdvanceId() == null) {
            updatedAdvance.setAdvanceId(existingAdvance.getAdvanceId());
        }
        
        return advanceRepository.save(updatedAdvance);
    }

    /**
     * Delete an advance
     *
     * @param id The advance ID
     */
    public void deleteAdvance(String id) {
        log.info("Deleting advance with ID: {}", id);
        advanceRepository.delete(id);
    }
}
