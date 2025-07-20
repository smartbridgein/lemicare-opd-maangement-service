package com.cosmicdoc.opdmanagement.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a medical service offered in the clinic
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Service {
    private String id;
    private String name;
    private String description;
    private String group;  // OPD, CONSULTATION, PACKAGE, etc.
    private double rate;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Constructor with required fields for creating a new service
     */
    public Service(String name, String description, String group, double rate) {
        this.name = name;
        this.description = description;
        this.group = group;
        this.rate = rate;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
