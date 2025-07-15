package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.Appointment;
import com.cosmicdoc.opdmanagement.repository.AppointmentRepository;
import com.cosmicdoc.opdmanagement.model.FirestoreAppointment;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Custom implementation of AppointmentRepository that directly uses Firestore
 * instead of relying on JSON loading.
 */
@Repository
@Primary
public class OpdAppointmentRepositoryImpl implements AppointmentRepository {
    private static final Logger logger = LoggerFactory.getLogger(OpdAppointmentRepositoryImpl.class);
    private static final String COLLECTION_NAME = "appointments";

    private final Firestore firestore;

    @Autowired
    public OpdAppointmentRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
        logger.info("Initialized OpdAppointmentRepositoryImpl with direct Firestore access");
    }

    @Override
    public List<Appointment> findAll() {
        try {
            return firestore.collection(COLLECTION_NAME).get().get().getDocuments().stream()
                    .map(this::documentToFirestoreAppointment)
                    .filter(Objects::nonNull)
                    .map(FirestoreAppointment::toAppointment)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching all appointments", e);
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Appointment> findById(String id) {
        try {
            DocumentSnapshot document = firestore.collection(COLLECTION_NAME).document(id).get().get();
            if (document.exists()) {
                FirestoreAppointment firestoreAppointment = documentToFirestoreAppointment(document);
                return Optional.ofNullable(firestoreAppointment).map(FirestoreAppointment::toAppointment);
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching appointment by ID: " + id, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean existsById(String id) {
        try {
            return firestore.collection(COLLECTION_NAME).document(id).get().get().exists();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error checking if appointment exists by ID: " + id, e);
            return false;
        }
    }

    @Override
    public Appointment save(Appointment appointment) {
        if (appointment.getAppointmentId() == null || appointment.getAppointmentId().isEmpty()) {
            appointment.setAppointmentId(UUID.randomUUID().toString());
        }
        
        // Convert Appointment to FirestoreAppointment
        FirestoreAppointment firestoreAppointment = FirestoreAppointment.fromAppointment(appointment);
        
        // Save the FirestoreAppointment to Firestore
        firestore.collection(COLLECTION_NAME).document(appointment.getAppointmentId()).set(firestoreAppointment);
        
        return appointment;
    }

    @Override
    public void deleteById(String id) {
        try {
            firestore.collection(COLLECTION_NAME).document(id).delete().get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error deleting appointment: " + id, e);
            throw new RuntimeException("Failed to delete appointment with ID: " + id, e);
        }
    }

    @Override
    public List<Appointment> findByUserId(String userId) {
        try {
            return firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("userId", userId)
                    .get().get().getDocuments().stream()
                    .map(this::documentToFirestoreAppointment)
                    .filter(Objects::nonNull)
                    .map(FirestoreAppointment::toAppointment)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching appointments by user ID: " + userId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Appointment> findByDoctorId(String doctorId) {
        try {
            return firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("doctorId", doctorId)
                    .get().get().getDocuments().stream()
                    .map(this::documentToFirestoreAppointment)
                    .filter(Objects::nonNull)
                    .map(FirestoreAppointment::toAppointment)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching appointments by doctor ID: " + doctorId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Appointment> findByStatus(String status) {
        try {
            return firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("status", status)
                    .get().get().getDocuments().stream()
                    .map(this::documentToFirestoreAppointment)
                    .filter(Objects::nonNull)
                    .map(FirestoreAppointment::toAppointment)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching appointments by status: " + status, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Appointment> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end) {
        try {
            // Convert LocalDateTime to Timestamp for Firestore querying
            Timestamp startTimestamp = Timestamp.ofTimeSecondsAndNanos(
                    start.atZone(ZoneId.systemDefault()).toEpochSecond(), 0);
            Timestamp endTimestamp = Timestamp.ofTimeSecondsAndNanos(
                    end.atZone(ZoneId.systemDefault()).toEpochSecond(), 0);
            
            return firestore.collection(COLLECTION_NAME)
                    .whereGreaterThanOrEqualTo("appointmentDate", startTimestamp)
                    .whereLessThanOrEqualTo("appointmentDate", endTimestamp)
                    .get().get().getDocuments().stream()
                    .map(this::documentToFirestoreAppointment)
                    .filter(Objects::nonNull)
                    .map(FirestoreAppointment::toAppointment)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching appointments between dates", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Appointment> findByUserIdAndStatus(String userId, String status) {
        try {
            return firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("status", status)
                    .get().get().getDocuments().stream()
                    .map(this::documentToFirestoreAppointment)
                    .filter(Objects::nonNull)
                    .map(FirestoreAppointment::toAppointment)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching appointments by user ID and status", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Appointment> findByPatientId(String patientId) {
        try {
            return firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("patientId", patientId)
                    .get().get().getDocuments().stream()
                    .map(this::documentToFirestoreAppointment)
                    .filter(Objects::nonNull)
                    .map(FirestoreAppointment::toAppointment)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching appointments by patient ID: " + patientId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Appointment> findByDoctorIdAndDate(String doctorId, LocalDate date) {
        try {
            // Convert LocalDate to start and end timestamps for the day
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            
            Timestamp startTimestamp = Timestamp.ofTimeSecondsAndNanos(
                    startOfDay.atZone(ZoneId.systemDefault()).toEpochSecond(), 0);
            Timestamp endTimestamp = Timestamp.ofTimeSecondsAndNanos(
                    endOfDay.atZone(ZoneId.systemDefault()).toEpochSecond(), 999999999);
            
            return firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("doctorId", doctorId)
                    .whereGreaterThanOrEqualTo("appointmentDate", startTimestamp)
                    .whereLessThanOrEqualTo("appointmentDate", endTimestamp)
                    .get().get().getDocuments().stream()
                    .map(this::documentToFirestoreAppointment)
                    .filter(Objects::nonNull)
                    .map(FirestoreAppointment::toAppointment)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching appointments by doctor ID and date", e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Appointment> findByDate(LocalDate date) {
        try {
            // Convert LocalDate to start and end timestamps for the day
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            
            Timestamp startTimestamp = Timestamp.ofTimeSecondsAndNanos(
                    startOfDay.atZone(ZoneId.systemDefault()).toEpochSecond(), 0);
            Timestamp endTimestamp = Timestamp.ofTimeSecondsAndNanos(
                    endOfDay.atZone(ZoneId.systemDefault()).toEpochSecond(), 999999999);
            
            return firestore.collection(COLLECTION_NAME)
                    .whereGreaterThanOrEqualTo("appointmentDate", startTimestamp)
                    .whereLessThanOrEqualTo("appointmentDate", endTimestamp)
                    .get().get().getDocuments().stream()
                    .map(this::documentToFirestoreAppointment)
                    .filter(Objects::nonNull)
                    .map(FirestoreAppointment::toAppointment)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching appointments by date", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Appointment> findByDoctorIdAndMonth(String doctorId, YearMonth month) {
        try {
            // Convert YearMonth to start and end timestamps for the month
            LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = month.atEndOfMonth().atTime(LocalTime.MAX);
            
            Timestamp startTimestamp = Timestamp.ofTimeSecondsAndNanos(
                    startOfMonth.atZone(ZoneId.systemDefault()).toEpochSecond(), 0);
            Timestamp endTimestamp = Timestamp.ofTimeSecondsAndNanos(
                    endOfMonth.atZone(ZoneId.systemDefault()).toEpochSecond(), 0);
            
            return firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("doctorId", doctorId)
                    .whereGreaterThanOrEqualTo("appointmentDate", startTimestamp)
                    .whereLessThanOrEqualTo("appointmentDate", endTimestamp)
                    .get().get().getDocuments().stream()
                    .map(this::documentToFirestoreAppointment)
                    .filter(Objects::nonNull)
                    .map(FirestoreAppointment::toAppointment)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching appointments by doctor ID and month", e);
            return Collections.emptyList();
        }
    }



    @Override
    public boolean reassignAppointment(String appointmentId, String newDoctorId) {
        try {
            Optional<Appointment> appointmentOpt = findById(appointmentId);
            if (appointmentOpt.isPresent()) {
                Appointment appointment = appointmentOpt.get();
                appointment.setDoctorId(newDoctorId);
                save(appointment);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Error reassigning appointment: " + appointmentId, e);
            return false;
        }
    }

    @Override
    public boolean cancelAppointment(String appointmentId) {
        try {
            Optional<Appointment> appointmentOpt = findById(appointmentId);
            if (appointmentOpt.isPresent()) {
                Appointment appointment = appointmentOpt.get();
                appointment.setStatus("CANCELLED");
                save(appointment);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Error cancelling appointment: " + appointmentId, e);
            return false;
        }
    }

    @Override
    public boolean isDoctorAvailable(String doctorId, LocalDate date, LocalTime time) {
        try {
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            
            // Add a buffer of 30 minutes before and after for appointment duration
            LocalDateTime startBuffer = dateTime.minusMinutes(30);
            LocalDateTime endBuffer = dateTime.plusMinutes(30);
            
            Timestamp startTimestamp = Timestamp.ofTimeSecondsAndNanos(
                    startBuffer.atZone(ZoneId.systemDefault()).toEpochSecond(), 0);
            Timestamp endTimestamp = Timestamp.ofTimeSecondsAndNanos(
                    endBuffer.atZone(ZoneId.systemDefault()).toEpochSecond(), 0);
            
            // Check if there are any appointments for this doctor within the time buffer
            long count = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("doctorId", doctorId)
                    .whereGreaterThanOrEqualTo("appointmentDate", startTimestamp)
                    .whereLessThanOrEqualTo("appointmentDate", endTimestamp)
                    .whereNotEqualTo("status", "CANCELLED")
                    .get().get().size();
            
            return count == 0;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error checking doctor availability", e);
            return false;
        }
    }

    @Override
    public List<LocalTime> getDoctorAvailableTimeSlots(String doctorId, LocalDate date) {
        try {
            // Get all appointments for this doctor on the given date
            List<Appointment> doctorAppointments = findByDoctorIdAndDate(doctorId, date);
            
            // Define the working hours (e.g., 9 AM to 5 PM)
            LocalTime startTime = LocalTime.of(9, 0);
            LocalTime endTime = LocalTime.of(17, 0);
            
            // Create time slots every 30 minutes
            List<LocalTime> allTimeSlots = new ArrayList<>();
            for (LocalTime time = startTime; !time.isAfter(endTime); time = time.plusMinutes(30)) {
                allTimeSlots.add(time);
            }
            
            // Filter out time slots that conflict with existing appointments
            return allTimeSlots.stream()
                    .filter(time -> isDoctorAvailable(doctorId, date, time))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting doctor available time slots", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Converts a Firestore document to a FirestoreAppointment object.
     * 
     * @param document The Firestore document
     * @return The FirestoreAppointment object or null if conversion fails
     */
    private FirestoreAppointment documentToFirestoreAppointment(DocumentSnapshot document) {
        try {
            FirestoreAppointment firestoreAppointment = document.toObject(FirestoreAppointment.class);
            if (firestoreAppointment != null) {
                firestoreAppointment.setAppointmentId(document.getId());
            }
            return firestoreAppointment;
        } catch (Exception e) {
            logger.error("Error converting document to FirestoreAppointment: " + document.getId(), e);
            return null;
        }
    }
}
