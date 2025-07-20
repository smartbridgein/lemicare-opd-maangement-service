package com.cosmicdoc.opdmanagement.service;

import com.cosmicdoc.opdmanagement.model.Service;

import java.util.List;

/**
 * Service interface for managing medical services
 */
public interface ServiceManagementService {
    
    /**
     * Create a new service
     *
     * @param service Service to create
     * @return Created service with ID
     */
    Service createService(Service service);
    
    /**
     * Get all services
     *
     * @return List of all services
     */
    List<Service> getAllServices();
    
    /**
     * Get services by group
     *
     * @param group Group name (e.g., OPD, CONSULTATION, PACKAGE)
     * @return List of services in the specified group
     */
    List<Service> getServicesByGroup(String group);
    
    /**
     * Get service by ID
     *
     * @param id Service ID
     * @return Service if found, null otherwise
     */
    Service getServiceById(String id);
    
    /**
     * Update a service
     *
     * @param id Service ID
     * @param service Updated service data
     * @return Updated service
     */
    Service updateService(String id, Service service);
    
    /**
     * Delete a service
     *
     * @param id Service ID
     * @return true if deleted, false otherwise
     */
    boolean deleteService(String id);
    
    /**
     * Change service status (active/inactive)
     *
     * @param id Service ID
     * @param active Status to set
     * @return Updated service
     */
    Service updateServiceStatus(String id, boolean active);
    
    /**
     * Search for services by name
     *
     * @param name Service name to search for
     * @return List of matching services
     */
    List<Service> searchServicesByName(String name);
}
