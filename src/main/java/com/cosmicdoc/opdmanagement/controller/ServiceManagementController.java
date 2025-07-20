package com.cosmicdoc.opdmanagement.controller;

import com.cosmicdoc.opdmanagement.model.ApiResponse;
import com.cosmicdoc.opdmanagement.model.Service;
import com.cosmicdoc.opdmanagement.service.ServiceManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for Service management operations
 */
@RestController
@RequestMapping("/api/services")
@Slf4j
public class ServiceManagementController {

    private final ServiceManagementService serviceManagementService;

    @Autowired
    public ServiceManagementController(ServiceManagementService serviceManagementService) {
        this.serviceManagementService = serviceManagementService;
    }

    /**
     * Create a new service
     *
     * @param service The service to create
     * @return ResponseEntity with the created service
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Service>> createService(@RequestBody Service service) {
        log.info("Received request to create service: {}", service.getName());
        Service createdService = serviceManagementService.createService(service);
        return ResponseEntity.ok(ApiResponse.success("Service created successfully", createdService));
    }

    /**
     * Get all services
     *
     * @return ResponseEntity with list of all services
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Service>>> getAllServices() {
        log.info("Received request to get all services");
        List<Service> services = serviceManagementService.getAllServices();
        return ResponseEntity.ok(ApiResponse.success("Services retrieved successfully", services));
    }

    /**
     * Get services by group
     *
     * @param group The service group to filter by
     * @return ResponseEntity with list of services in the specified group
     */
    @GetMapping("/group/{group}")
    public ResponseEntity<ApiResponse<List<Service>>> getServicesByGroup(@PathVariable String group) {
        log.info("Received request to get services for group: {}", group);
        List<Service> services = serviceManagementService.getServicesByGroup(group);
        return ResponseEntity.ok(ApiResponse.success("Services retrieved successfully for group: " + group, services));
    }

    /**
     * Get a service by ID
     *
     * @param id The service ID
     * @return ResponseEntity with the service if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Service>> getServiceById(@PathVariable String id) {
        log.info("Received request to get service with ID: {}", id);
        Service service = serviceManagementService.getServiceById(id);
        return ResponseEntity.ok(ApiResponse.success("Service retrieved successfully", service));
    }

    /**
     * Update a service
     *
     * @param id      The service ID
     * @param service The updated service data
     * @return ResponseEntity with the updated service
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Service>> updateService(@PathVariable String id, @RequestBody Service service) {
        log.info("Received request to update service with ID: {}", id);
        Service updatedService = serviceManagementService.updateService(id, service);
        return ResponseEntity.ok(ApiResponse.success("Service updated successfully", updatedService));
    }

    /**
     * Delete a service
     *
     * @param id The service ID to delete
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteService(@PathVariable String id) {
        log.info("Received request to delete service with ID: {}", id);
        boolean deleted = serviceManagementService.deleteService(id);
        return ResponseEntity.ok(ApiResponse.success("Service deleted successfully", "Service with ID " + id + " was deleted"));
    }

    /**
     * Update service status (active/inactive)
     *
     * @param id     The service ID
     * @param status Map containing active status
     * @return ResponseEntity with the updated service
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Service>> updateServiceStatus(
            @PathVariable String id,
            @RequestBody Map<String, Boolean> status) {
        
        boolean active = status.getOrDefault("active", true);
        log.info("Received request to update status for service ID: {} to {}", id, active);
        
        Service updatedService = serviceManagementService.updateServiceStatus(id, active);
        return ResponseEntity.ok(ApiResponse.success(
            "Service status updated successfully to " + (active ? "active" : "inactive"),
            updatedService
        ));
    }

    /**
     * Search services by name
     *
     * @param name The name to search for
     * @return ResponseEntity with list of matching services
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Service>>> searchServices(@RequestParam String name) {
        log.info("Received request to search services with name containing: {}", name);
        List<Service> services = serviceManagementService.searchServicesByName(name);
        return ResponseEntity.ok(ApiResponse.success("Service search completed", services));
    }
}
