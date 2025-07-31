package com.cosmicdoc.opdmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a line item in a cash memo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineItem {
    private LocalDate date;
    private String serviceId;
    private String serviceName;
    private String description;
    private String incentive;
    private int quantity;
    private double rate;
    private double discount;
    private String taxProfileId;
    private List<TaxComponent> taxDetails;
    private double totalAmount;
    private TaxInfo taxInfo;
}
