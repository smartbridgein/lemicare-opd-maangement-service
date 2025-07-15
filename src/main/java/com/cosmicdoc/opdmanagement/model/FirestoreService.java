package com.cosmicdoc.opdmanagement.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Firestore representation of a Service entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirestoreService {
    @DocumentId
    private String id;
    private String name;
    private String description;
    private String group;  // OPD, CONSULTATION, PACKAGE, etc.
    private Double rate;
    private Boolean active;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Convert from domain model to Firestore model
     */
    public static FirestoreService fromService(Service service) {
        FirestoreService firestoreService = new FirestoreService();
        firestoreService.setId(service.getId());
        firestoreService.setName(service.getName());
        firestoreService.setDescription(service.getDescription());
        firestoreService.setGroup(service.getGroup());
        firestoreService.setRate(service.getRate());
        firestoreService.setActive(service.isActive());
        firestoreService.setCreatedBy(service.getCreatedBy());
        firestoreService.setUpdatedBy(service.getUpdatedBy());
        
        // Convert LocalDateTime to Timestamp
        if (service.getCreatedAt() != null) {
            firestoreService.setCreatedAt(Timestamp.of(
                java.sql.Timestamp.valueOf(service.getCreatedAt())
            ));
        }
        
        if (service.getUpdatedAt() != null) {
            firestoreService.setUpdatedAt(Timestamp.of(
                java.sql.Timestamp.valueOf(service.getUpdatedAt())
            ));
        }
        
        return firestoreService;
    }

    /**
     * Convert from Firestore model to domain model
     */
    public Service toService() {
        Service service = new Service();
        service.setId(this.getId());
        service.setName(this.getName());
        service.setDescription(this.getDescription());
        service.setGroup(this.getGroup());
        service.setRate(this.getRate() != null ? this.getRate() : 0.0);
        service.setActive(this.getActive() != null ? this.getActive() : true);
        service.setCreatedBy(this.getCreatedBy());
        service.setUpdatedBy(this.getUpdatedBy());
        
        // Convert Timestamp to LocalDateTime
        if (this.getCreatedAt() != null) {
            service.setCreatedAt(this.getCreatedAt()
                .toDate()
                .toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime());
        }
        
        if (this.getUpdatedAt() != null) {
            service.setUpdatedAt(this.getUpdatedAt()
                .toDate()
                .toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime());
        }
        
        return service;
    }
}
