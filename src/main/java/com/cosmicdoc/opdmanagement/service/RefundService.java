package com.cosmicdoc.opdmanagement.service;

import com.cosmicdoc.opdmanagement.model.Refund;
import com.cosmicdoc.opdmanagement.repository.RefundRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Refund operations
 */
@Service
@Slf4j
public class RefundService {

    private final RefundRepository refundRepository;

    @Autowired
    public RefundService(RefundRepository refundRepository) {
        this.refundRepository = refundRepository;
    }

    /**
     * Create a new refund
     *
     * @param refund The refund to create
     * @return The created refund
     */
    public Refund createRefund(Refund refund) {
        log.info("Creating refund for patient: {}", refund.getPatientId());
        return refundRepository.save(refund);
    }

    /**
     * Retrieve all refunds
     *
     * @return List of all refunds
     */
    public List<Refund> getAllRefunds() {
        log.info("Retrieving all refunds");
        return refundRepository.findAll();
    }

    /**
     * Retrieve a refund by ID
     *
     * @param id The refund ID
     * @return Optional containing the refund if found
     */
    public Optional<Refund> getRefundById(String id) {
        log.info("Retrieving refund with ID: {}", id);
        return refundRepository.findById(id);
    }

    /**
     * Retrieve refunds for a specific patient
     *
     * @param patientId The patient ID
     * @return List of refunds for the patient
     */
    public List<Refund> getRefundsByPatientId(String patientId) {
        log.info("Retrieving refunds for patient: {}", patientId);
        return refundRepository.findByPatientId(patientId);
    }

    /**
     * Update an existing refund
     *
     * @param id The refund ID
     * @param updatedRefund The updated refund details
     * @return The updated refund
     * @throws RuntimeException if the refund is not found
     */
    public Refund updateRefund(String id, Refund updatedRefund) {
        log.info("Updating refund with ID: {}", id);
        
        Refund existingRefund = refundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Refund not found with ID: " + id));
        
        // Update fields while preserving ID and refund ID
        updatedRefund.setId(existingRefund.getId());
        if (updatedRefund.getRefundId() == null) {
            updatedRefund.setRefundId(existingRefund.getRefundId());
        }
        
        return refundRepository.save(updatedRefund);
    }

    /**
     * Delete a refund
     *
     * @param id The refund ID
     */
    public void deleteRefund(String id) {
        log.info("Deleting refund with ID: {}", id);
        refundRepository.delete(id);
    }
}
