package com.cosmicdoc.opdmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Represents a payment record for an invoice
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecord {
    private String paymentId;
    private LocalDate paymentDate;
    private double amount;
    private String paymentMethod;
    private String referenceNumber;
    private String notes;
    private String status;
}
