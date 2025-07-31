package com.cosmicdoc.opdmanagement.dto;

import com.cosmicdoc.opdmanagement.model.Prescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitHistoryDTO {
    
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
