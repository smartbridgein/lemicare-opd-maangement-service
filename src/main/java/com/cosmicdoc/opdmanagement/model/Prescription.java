package com.cosmicdoc.opdmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    
    private String medicationName;
    private String dosage;
    private String frequency;
    private String duration;
    private String notes;
}
