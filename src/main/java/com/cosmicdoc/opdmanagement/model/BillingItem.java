package com.cosmicdoc.opdmanagement.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Base class for all billing-related entities
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BillingItem {
    private String id;
    private String patientId;
    private String patientName;
    private LocalDate date;
    private LocalDateTime timestamp; // Full timestamp for sorting and detailed tracking
    private double amount;
    private String createdBy;
    private String modeOfPayment;
    private LocalDate createdDate;
    private LocalDateTime createdTimestamp; // Full timestamp for creation tracking
}
