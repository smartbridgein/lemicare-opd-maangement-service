package com.cosmicdoc.opdmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyItemDTO {
    private String id;
    
    @NotBlank(message = "Item name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Dosage form is required")
    private String dosageForm; // tablets, syrup, injection, etc.
    
    private String manufacturer;
    
    private String batchNumber;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be non-negative")
    private Integer stockQuantity;
    
    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;
    
    private Boolean requiresPrescription = false;
    
    private String category;
    
    private Boolean isActive = true;
}
