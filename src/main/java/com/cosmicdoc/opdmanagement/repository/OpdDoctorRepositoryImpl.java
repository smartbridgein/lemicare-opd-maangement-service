package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.Doctor;
import com.cosmicdoc.opdmanagement.model.FirestoreDoctor;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
@Primary
public class OpdDoctorRepositoryImpl implements DoctorRepository {

    private static final Logger logger = LoggerFactory.getLogger(OpdDoctorRepositoryImpl.class);
    private static final String COLLECTION_NAME = "doctors";
    
    private final Firestore firestore;
    
    @Autowired
    public OpdDoctorRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }
    
    @Override
    public List<Doctor> findAll() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream()
                .map(document -> document.toObject(FirestoreDoctor.class).toDoctor())
                .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching all doctors", e);
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        }
    }
    
    @Override
    public Optional<Doctor> findById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                FirestoreDoctor firestoreDoctor = document.toObject(FirestoreDoctor.class);
                return Optional.ofNullable(firestoreDoctor).map(FirestoreDoctor::toDoctor);
            } else {
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching doctor with ID: {}", id, e);
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Doctor> findByEmail(String email) {
        try {
            ApiFuture<QuerySnapshot> future = 
                firestore.collection(COLLECTION_NAME)
                        .whereEqualTo("email", email)
                        .get();
            
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            if (!documents.isEmpty()) {
                FirestoreDoctor firestoreDoctor = documents.get(0).toObject(FirestoreDoctor.class);
                return Optional.of(firestoreDoctor.toDoctor());
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching doctor by email: {}", email, e);
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }
    
    @Override
    public List<Doctor> findBySpecialization(String specialization) {
        try {
            ApiFuture<QuerySnapshot> future = 
                firestore.collection(COLLECTION_NAME)
                        .whereEqualTo("specialization", specialization)
                        .get();
            
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            return documents.stream()
                .map(document -> document.toObject(FirestoreDoctor.class).toDoctor())
                .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching doctors by specialization: {}", specialization, e);
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        }
    }
    
    @Override
    public Doctor save(Doctor doctor) {
        try {
            FirestoreDoctor firestoreDoctor = FirestoreDoctor.fromDoctor(doctor);
            
            // Generate ID if it's a new doctor
            if (firestoreDoctor.getId() == null || firestoreDoctor.getId().isEmpty()) {
                String docId = UUID.randomUUID().toString();
                firestoreDoctor.setId(docId);
                doctor.setId(docId);
            }
            
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(firestoreDoctor.getId());
            
            // Convert to a map for Firestore
            Map<String, Object> docMap = firestoreDoctor.toMap();
            
            ApiFuture<WriteResult> result = docRef.set(docMap);
            result.get(); // Wait for the write to complete
            
            return doctor;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error saving doctor: {}", doctor.getId(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to save doctor", e);
        }
    }
    
    @Override
    public boolean deleteById(String id) {
        try {
            ApiFuture<WriteResult> writeResult = firestore.collection(COLLECTION_NAME).document(id).delete();
            writeResult.get(); // Wait for the delete to complete
            return true;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error deleting doctor with ID: {}", id, e);
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    public boolean existsById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            return document.exists();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error checking if doctor exists with ID: {}", id, e);
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    public List<Doctor> findAvailable() {
        try {
            ApiFuture<QuerySnapshot> future = 
                firestore.collection(COLLECTION_NAME)
                        .whereEqualTo("isActive", true)
                        .get();
            
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            return documents.stream()
                .map(document -> document.toObject(FirestoreDoctor.class).toDoctor())
                .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching available doctors", e);
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Doctor> findAvailableBySpecialization(String specialization) {
        try {
            ApiFuture<QuerySnapshot> future = 
                firestore.collection(COLLECTION_NAME)
                        .whereEqualTo("isActive", true)
                        .whereEqualTo("specialization", specialization)
                        .get();
            
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            return documents.stream()
                .map(document -> document.toObject(FirestoreDoctor.class).toDoctor())
                .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching available doctors by specialization: {}", specialization, e);
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        }
    }
    
    @Override
    public boolean updateAvailability(String id, boolean isAvailable) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            Map<String, Object> updates = new HashMap<>();
            updates.put("isActive", isAvailable);
            
            ApiFuture<WriteResult> writeResult = docRef.update(updates);
            writeResult.get(); // Wait for the update to complete
            return true;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error updating availability for doctor: {}", id, e);
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    public boolean addLeave(String id, LocalDate startDate, LocalDate endDate, String reason) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                FirestoreDoctor doctor = document.toObject(FirestoreDoctor.class);
                
                if (doctor != null) {
                    if (doctor.getLeaves() == null) {
                        doctor.setLeaves(new ArrayList<>());
                    }
                    
                    FirestoreDoctor.Leave leave = new FirestoreDoctor.Leave();
                    leave.setStartDate(startDate.toString());
                    leave.setEndDate(endDate.toString());
                    leave.setReason(reason);
                    leave.setStatus("PENDING"); // Default status
                    
                    doctor.getLeaves().add(leave);
                    
                    // Update the document
                    ApiFuture<WriteResult> writeResult = docRef.update("leaves", doctor.getLeaves());
                    writeResult.get(); // Wait for the update to complete
                    return true;
                }
            }
            return false;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error adding leave for doctor: {}", id, e);
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    public boolean cancelLeave(String id, String leaveId) {
        // Implementation would require storing a unique ID for each leave
        // This is a simplified implementation
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                FirestoreDoctor doctor = document.toObject(FirestoreDoctor.class);
                
                if (doctor != null && doctor.getLeaves() != null) {
                    // In a real implementation, we would find the leave by ID
                    // For now, we'll just remove the first leave with status PENDING
                    
                    boolean removed = false;
                    List<FirestoreDoctor.Leave> updatedLeaves = new ArrayList<>(doctor.getLeaves());
                    
                    for (int i = 0; i < updatedLeaves.size(); i++) {
                        FirestoreDoctor.Leave leave = updatedLeaves.get(i);
                        if (leave.getStatus().equals("PENDING")) {
                            updatedLeaves.remove(i);
                            removed = true;
                            break;
                        }
                    }
                    
                    if (removed) {
                        ApiFuture<WriteResult> writeResult = docRef.update("leaves", updatedLeaves);
                        writeResult.get(); // Wait for the update to complete
                        return true;
                    }
                }
            }
            return false;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error canceling leave for doctor: {}", id, e);
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    public List<Doctor.DoctorLeave> getDoctorLeaves(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                FirestoreDoctor doctor = document.toObject(FirestoreDoctor.class);
                List<Doctor.DoctorLeave> result = new ArrayList<>();
                
                if (doctor != null && doctor.getLeaves() != null) {
                    // Convert FirestoreDoctor.Leave to Doctor.DoctorLeave
                    for (FirestoreDoctor.Leave leave : doctor.getLeaves()) {
                        Doctor.DoctorLeave doctorLeave = new Doctor.DoctorLeave();
                        // Parse string dates to LocalDateTime
                        doctorLeave.setStartDate(LocalDate.parse(leave.getStartDate()).atStartOfDay());
                        doctorLeave.setEndDate(LocalDate.parse(leave.getEndDate()).atStartOfDay());
                        doctorLeave.setReason(leave.getReason());
                        doctorLeave.setStatus(leave.getStatus());
                        result.add(doctorLeave);
                    }
                    return result;
                }
            }
            return Collections.emptyList();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching leaves for doctor: {}", id, e);
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        }
    }
    
    @Override
    public boolean updateLocation(String id, double latitude, double longitude) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            Map<String, Object> updates = new HashMap<>();
            updates.put("latitude", latitude);
            updates.put("longitude", longitude);
            
            ApiFuture<WriteResult> writeResult = docRef.update(updates);
            writeResult.get(); // Wait for the update to complete
            return true;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error updating location for doctor: {}", id, e);
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
