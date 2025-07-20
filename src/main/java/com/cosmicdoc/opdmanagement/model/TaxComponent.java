package com.cosmicdoc.opdmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a tax component in a tax breakdown
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxComponent {
    private String name;
    private double amount;
}
