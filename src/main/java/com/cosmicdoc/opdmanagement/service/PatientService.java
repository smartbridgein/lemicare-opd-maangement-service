package com.cosmicdoc.opdmanagement.service;

import com.cosmicdoc.opdmanagement.model.Patient;
import com.cosmicdoc.opdmanagement.repository.PatientRepository;
import com.cosmicdoc.opdmanagement.dto.PatientDTO;
import com.cosmicdoc.opdmanagement.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);
    private static final Pattern PATIENT_ID_PATTERN = Pattern.compile("PAT-(\\d{4})-(\\d{4})");
    private final AtomicInteger sequenceCounter;
    private final PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
        this.sequenceCounter = initializeSequenceCounter();
    }
    
    /**
     * Initializes the sequence counter by finding the highest patient ID
     * in the current year
     */
    private AtomicInteger initializeSequenceCounter() {
        String currentYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        int highestSequence = 0;
        
        try {
            // Find all patients and determine the highest sequence number
            List<PatientDTO> allPatients = getAllPatients();
            
            for (PatientDTO patient : allPatients) {
                String patientId = patient.getId();
                if (patientId != null) {
                    Matcher matcher = PATIENT_ID_PATTERN.matcher(patientId);
                    if (matcher.matches()) {
                        String year = matcher.group(1);
                        String sequence = matcher.group(2);
                        
                        // Only consider IDs from the current year
                        if (year.equals(currentYear)) {
                            int sequenceNum = Integer.parseInt(sequence);
                            highestSequence = Math.max(highestSequence, sequenceNum);
                        }
                    }
                }
            }
            
            // Start from one higher than the highest found
            logger.info("Initialized patient sequence counter starting from {} for year {}", 
                    highestSequence + 1, currentYear);
            return new AtomicInteger(highestSequence + 1);
            
        } catch (Exception e) {
            // Default to 1 if something goes wrong
            logger.error("Error initializing sequence counter: {}", e.getMessage(), e);
            logger.warn("Using default sequence counter starting from 1");
            return new AtomicInteger(1);
        }
    }

    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PatientDTO getPatientById(String id) {
        Optional<Patient> patientOptional = patientRepository.findById(id);
        return patientOptional.map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    public PatientDTO getPatientByPhoneNumber(String phoneNumber) {
        Optional<Patient> patientOptional = patientRepository.findByPhoneNumber(phoneNumber);
        return patientOptional.map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with phone number: " + phoneNumber));
    }

    public PatientDTO createPatient(PatientDTO patientDTO) {
        // Check if patient with same phone number already exists
        Optional<Patient> existingPatient = patientRepository.findByPhoneNumber(patientDTO.getPhoneNumber());
        if (existingPatient.isPresent()) {
            throw new IllegalArgumentException("Patient with this phone number already exists");
        }

        // Map DTO to entity
        Patient patient = mapToEntity(patientDTO);
        
        // Generate incremental ID - always overwrite any provided ID to ensure format consistency
        String generatedId = generateReadablePatientId();
        patient.setId(generatedId);
        logger.info("Generated new patient ID: {}", generatedId);

        // Set registration date if not already set
        if (patient.getRegistrationDate() == null) {
            patient.setRegistrationDate(LocalDate.now());
        }

        // Save patient
        Patient savedPatient = patientRepository.save(patient);

        // Return mapped DTO
        return mapToDTO(savedPatient);
    }

    public PatientDTO updatePatient(String id, PatientDTO patientDTO) {
        // Check if patient exists
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found with id: " + id);
        }

        // Map DTO to entity and ensure ID is set
        Patient patient = mapToEntity(patientDTO);
        patient.setId(id);

        // Save updated patient
        Patient savedPatient = patientRepository.save(patient);

        // Return mapped DTO
        return mapToDTO(savedPatient);
    }

    public void deletePatient(String id) {
        // Check if patient exists
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found with id: " + id);
        }

        // Delete patient
        patientRepository.deleteById(id);
    }
    
    /**
     * Migrates legacy UUID patient IDs to the new format PAT-YYYY-XXXX
     * @return Number of patients updated
     */
    public int migratePatientIds() {
        logger.info("Starting patient ID migration process");
        List<Patient> allPatients = patientRepository.findAll();
        int updatedCount = 0;
        
        for (Patient patient : allPatients) {
            String currentId = patient.getId();
            
            // Check if the ID is not in the new format (PAT-YYYY-XXXX)
            if (currentId == null || !currentId.matches("PAT-\\d{4}-\\d{4}")) {
                // Generate a new ID
                String newId = generateReadablePatientId();
                logger.info("Migrating patient ID from {} to {}", currentId, newId);
                
                // Create a new patient object with the new ID but all the same data
                Patient updatedPatient = new Patient();
                updatedPatient.setId(newId);
                updatedPatient.setName(patient.getName());
                updatedPatient.setPhoneNumber(patient.getPhoneNumber());
                updatedPatient.setEmail(patient.getEmail());
                updatedPatient.setAddress(patient.getAddress());
                updatedPatient.setDateOfBirth(patient.getDateOfBirth());
                updatedPatient.setGender(patient.getGender());
                updatedPatient.setBloodGroup(patient.getBloodGroup());
                updatedPatient.setAllergies(patient.getAllergies());
                updatedPatient.setMedicalHistory(patient.getMedicalHistory());
                updatedPatient.setEmergencyContactName(patient.getEmergencyContactName());
                updatedPatient.setEmergencyContactNumber(patient.getEmergencyContactNumber());
                updatedPatient.setRegistrationDate(patient.getRegistrationDate());
                updatedPatient.setActive(patient.isActive());
                
                // Save the updated patient with the new ID
                Patient saved = patientRepository.save(updatedPatient);
                
                // Delete the old patient record (with old UUID)
                patientRepository.deleteById(currentId);
                
                updatedCount++;
            }
        }
        
        logger.info("Patient ID migration complete. Updated {} patients", updatedCount);
        return updatedCount;
    }

    /**
     * Generates a readable patient ID in the format PAT-YYYY-XXXX
     * Where YYYY is the current year and XXXX is a sequential number
     * @return a formatted patient ID string
     */
    private String generateReadablePatientId() {
        // Get current year
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        
        // Get next sequence number with padding
        String sequence = String.format("%04d", sequenceCounter.getAndIncrement());
        
        // Combine into format PAT-YYYY-XXXX
        return "PAT-" + year + "-" + sequence;
    }
    
    // Helper methods to map between entity and DTO
    private PatientDTO mapToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setName(patient.getName());
        dto.setEmail(patient.getEmail());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setAddress(patient.getAddress());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setGender(patient.getGender());
        dto.setBloodGroup(patient.getBloodGroup());
        dto.setAllergies(patient.getAllergies());
        dto.setMedicalHistory(patient.getMedicalHistory());
        dto.setEmergencyContactName(patient.getEmergencyContactName());
        dto.setEmergencyContactNumber(patient.getEmergencyContactNumber());
        dto.setActive(patient.isActive());
        return dto;
    }

    private Patient mapToEntity(PatientDTO dto) {
        Patient patient = new Patient();
        patient.setId(dto.getId());
        patient.setName(dto.getName());
        patient.setEmail(dto.getEmail());
        patient.setPhoneNumber(dto.getPhoneNumber());
        patient.setAddress(dto.getAddress());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setGender(dto.getGender());
        patient.setBloodGroup(dto.getBloodGroup());
        patient.setAllergies(dto.getAllergies());
        patient.setMedicalHistory(dto.getMedicalHistory());
        patient.setEmergencyContactName(dto.getEmergencyContactName());
        patient.setEmergencyContactNumber(dto.getEmergencyContactNumber());
        patient.setActive(dto.isActive());
        return patient;
    }
}
