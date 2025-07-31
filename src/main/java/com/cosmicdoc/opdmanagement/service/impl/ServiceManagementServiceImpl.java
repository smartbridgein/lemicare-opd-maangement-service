package com.cosmicdoc.opdmanagement.service.impl;

import com.cosmicdoc.opdmanagement.exception.ResourceNotFoundException;
import com.cosmicdoc.opdmanagement.model.FirestoreService;
import com.cosmicdoc.opdmanagement.model.Service;
import com.cosmicdoc.opdmanagement.service.ServiceManagementService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Implementation of ServiceManagementService with Firestore integration
 */
@org.springframework.stereotype.Service
@Slf4j
public class ServiceManagementServiceImpl implements ServiceManagementService {

    private static final String COLLECTION_NAME = "services";

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    @Override
    public com.cosmicdoc.opdmanagement.model.Service createService(com.cosmicdoc.opdmanagement.model.Service service) {
        try {
            log.info("Creating new service: {}", service.getName());
            
            // Generate a new ID if not provided
            if (service.getId() == null || service.getId().isEmpty()) {
                service.setId(UUID.randomUUID().toString());
            }
            
            // Set timestamps if not provided
            if (service.getCreatedAt() == null) {
                service.setCreatedAt(LocalDateTime.now());
            }
            if (service.getUpdatedAt() == null) {
                service.setUpdatedAt(LocalDateTime.now());
            }
            
            // Default status to active if not specified
            if (!service.isActive()) {
                service.setActive(true);
            }
            
            // Convert to Firestore model
            FirestoreService firestoreService = FirestoreService.fromService(service);
            
            // Save to Firestore
            getFirestore().collection(COLLECTION_NAME).document(service.getId())
                .set(firestoreService).get();
            
            log.info("Service created with ID: {}", service.getId());
            return service;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error creating service", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to create service: " + e.getMessage(), e);
        }
    }

    @Override
    public List<com.cosmicdoc.opdmanagement.model.Service> getAllServices() {
        try {
            log.info("Fetching all services");
            List<com.cosmicdoc.opdmanagement.model.Service> services = new ArrayList<>();
            
            // Query all documents in the collection
            ApiFuture<QuerySnapshot> future = getFirestore().collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            for (DocumentSnapshot document : documents) {
                FirestoreService firestoreService = document.toObject(FirestoreService.class);
                if (firestoreService != null) {
                    services.add(firestoreService.toService());
                }
            }
            
            log.info("Retrieved {} services", services.size());
            return services;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error fetching services", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to retrieve services: " + e.getMessage(), e);
        }
    }

    @Override
    public List<com.cosmicdoc.opdmanagement.model.Service> getServicesByGroup(String group) {
        try {
            log.info("Fetching services for group: {}", group);
            List<com.cosmicdoc.opdmanagement.model.Service> services = new ArrayList<>();
            
            // Query documents filtered by group
            ApiFuture<QuerySnapshot> future = 
                getFirestore().collection(COLLECTION_NAME)
                    .whereEqualTo("group", group)
                    .get();
                    
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            for (DocumentSnapshot document : documents) {
                FirestoreService firestoreService = document.toObject(FirestoreService.class);
                if (firestoreService != null) {
                    services.add(firestoreService.toService());
                }
            }
            
            log.info("Retrieved {} services for group: {}", services.size(), group);
            return services;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error fetching services for group: {}", group, e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to retrieve services by group: " + e.getMessage(), e);
        }
    }

    @Override
    public com.cosmicdoc.opdmanagement.model.Service getServiceById(String id) {
        try {
            log.info("Fetching service with ID: {}", id);
            DocumentSnapshot document = 
                getFirestore().collection(COLLECTION_NAME).document(id).get().get();
            
            if (document.exists()) {
                FirestoreService firestoreService = document.toObject(FirestoreService.class);
                if (firestoreService != null) {
                    log.info("Service found: {}", firestoreService.getName());
                    return firestoreService.toService();
                }
            }
            
            log.warn("Service not found with ID: {}", id);
            throw new ResourceNotFoundException("Service not found with id: " + id);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error fetching service with ID: {}", id, e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to retrieve service: " + e.getMessage(), e);
        }
    }

    @Override
    public com.cosmicdoc.opdmanagement.model.Service updateService(String id, com.cosmicdoc.opdmanagement.model.Service service) {
        try {
            log.info("Updating service with ID: {}", id);
            
            // Check if service exists
            DocumentSnapshot document = getFirestore().collection(COLLECTION_NAME).document(id).get().get();
            if (!document.exists()) {
                log.warn("Service not found with ID: {}", id);
                throw new ResourceNotFoundException("Service not found with id: " + id);
            }
            
            // Preserve existing data
            FirestoreService existingService = document.toObject(FirestoreService.class);
            if (existingService == null) {
                throw new ResourceNotFoundException("Error retrieving existing service data");
            }
            
            // Update service with new data, preserving id and creation timestamp
            service.setId(id);
            service.setUpdatedAt(LocalDateTime.now());
            
            // If createdAt is null, preserve the original value
            if (service.getCreatedAt() == null && existingService.getCreatedAt() != null) {
                service.setCreatedAt(existingService.toService().getCreatedAt());
            }
            
            // If createdBy is null, preserve the original value
            if ((service.getCreatedBy() == null || service.getCreatedBy().isEmpty()) 
                    && existingService.getCreatedBy() != null) {
                service.setCreatedBy(existingService.getCreatedBy());
            }
            
            FirestoreService firestoreService = FirestoreService.fromService(service);
            getFirestore().collection(COLLECTION_NAME).document(id).set(firestoreService).get();
            
            log.info("Service updated: {}", service.getName());
            return service;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error updating service with ID: {}", id, e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to update service: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteService(String id) {
        try {
            log.info("Deleting service with ID: {}", id);
            
            // Check if service exists
            DocumentSnapshot document = getFirestore().collection(COLLECTION_NAME).document(id).get().get();
            if (!document.exists()) {
                log.warn("Service not found with ID: {}", id);
                throw new ResourceNotFoundException("Service not found with id: " + id);
            }
            
            // Delete the document
            getFirestore().collection(COLLECTION_NAME).document(id).delete().get();
            
            log.info("Service deleted with ID: {}", id);
            return true;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error deleting service with ID: {}", id, e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to delete service: " + e.getMessage(), e);
        }
    }

    @Override
    public com.cosmicdoc.opdmanagement.model.Service updateServiceStatus(String id, boolean active) {
        try {
            log.info("Updating status for service ID: {} to {}", id, active);
            
            // Get the existing service
            com.cosmicdoc.opdmanagement.model.Service service = getServiceById(id);
            
            // Update only the status
            service.setActive(active);
            service.setUpdatedAt(LocalDateTime.now());
            
            // Convert to Firestore model and save
            FirestoreService firestoreService = FirestoreService.fromService(service);
            getFirestore().collection(COLLECTION_NAME).document(id)
                .set(firestoreService).get();
            
            log.info("Service status updated: {}", active);
            return service;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error updating service status for ID: {}", id, e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to update service status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<com.cosmicdoc.opdmanagement.model.Service> searchServicesByName(String name) {
        try {
            log.info("Searching for services with name containing: {}", name);
            
            // Firebase doesn't support direct contains/like queries, so we need to fetch all and filter
            List<com.cosmicdoc.opdmanagement.model.Service> allServices = getAllServices();
            
            // Filter services by name containing the search term (case-insensitive)
            List<com.cosmicdoc.opdmanagement.model.Service> matchingServices = allServices.stream()
                .filter(service -> service.getName() != null && 
                                  service.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
            
            log.info("Found {} services matching: {}", matchingServices.size(), name);
            return matchingServices;
        } catch (Exception e) {
            log.error("Error searching services by name: {}", name, e);
            throw new RuntimeException("Failed to search services: " + e.getMessage(), e);
        }
    }
}
