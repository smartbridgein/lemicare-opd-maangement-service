package com.cosmicdoc.opdmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitHistory {
    
    private String id;
    private String patientId;
    private String visitDate;
    private String doctorId;
    private String doctorName;
    private List<String> symptoms;
    private String diagnosis;
    private String treatment;
    private List<Prescription> prescriptions;
    private String followUpDate;
    private String notes;
}
