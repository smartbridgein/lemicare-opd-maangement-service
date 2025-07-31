package com.cosmicdoc.opdmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PastSurgery {
    
    private String surgeryName;
    
    private String date;
    
    private String hospital;
    
    private String notes;
}
