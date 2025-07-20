package com.cosmicdoc.opdmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents tax information for a line item
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxInfo {
    private String taxProfileName;
    private double taxRate;
}
