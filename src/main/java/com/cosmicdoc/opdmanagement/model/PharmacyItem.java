package com.cosmicdoc.opdmanagement.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PharmacyItem {
    private String id;
    private String name;
    private String description;
    private String dosageForm; // tablets, syrup, injection, etc.
    private String manufacturer;
    private String batchNumber;
    private Double price;
    private Integer stockQuantity;
    private LocalDate expiryDate;
    private Boolean requiresPrescription;
    private String category;
    private Boolean isActive;
}
