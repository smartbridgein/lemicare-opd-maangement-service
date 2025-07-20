package com.cosmicdoc.opdmanagement.model;

import com.cosmicdoc.opdmanagement.model.PharmacyItem;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A wrapper class for PharmacyItem that handles the conversion between LocalDate and String
 * for Firestore compatibility.
 */
@Data
@NoArgsConstructor
public class FirestorePharmacyItem {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    private String id;
    private String name;
    private String description;
    private String dosageForm; 
    private String manufacturer;
    private String batchNumber;
    private Double price;
    private Integer stockQuantity;
    private String expiryDate; // Stored as String in ISO format (YYYY-MM-DD)
    private Boolean requiresPrescription;
    private String category;
    private Boolean isActive;
    
    /**
     * Creates a FirestorePharmacyItem from a standard PharmacyItem entity
     */
    public static FirestorePharmacyItem fromPharmacyItem(PharmacyItem item) {
        if (item == null) {
            return null;
        }
        
        FirestorePharmacyItem firestoreItem = new FirestorePharmacyItem();
        firestoreItem.setId(item.getId());
        firestoreItem.setName(item.getName());
        firestoreItem.setDescription(item.getDescription());
        firestoreItem.setDosageForm(item.getDosageForm());
        firestoreItem.setManufacturer(item.getManufacturer());
        firestoreItem.setBatchNumber(item.getBatchNumber());
        firestoreItem.setPrice(item.getPrice());
        firestoreItem.setStockQuantity(item.getStockQuantity());
        
        // Convert LocalDate to String
        if (item.getExpiryDate() != null) {
            firestoreItem.setExpiryDate(item.getExpiryDate().format(DATE_FORMATTER));
        }
        
        firestoreItem.setRequiresPrescription(item.getRequiresPrescription());
        firestoreItem.setCategory(item.getCategory());
        firestoreItem.setIsActive(item.getIsActive());
        
        return firestoreItem;
    }
    
    /**
     * Converts this FirestorePharmacyItem to a standard PharmacyItem entity
     */
    public PharmacyItem toPharmacyItem() {
        PharmacyItem item = new PharmacyItem();
        item.setId(this.id);
        item.setName(this.name);
        item.setDescription(this.description);
        item.setDosageForm(this.dosageForm);
        item.setManufacturer(this.manufacturer);
        item.setBatchNumber(this.batchNumber);
        item.setPrice(this.price);
        item.setStockQuantity(this.stockQuantity);
        
        // Convert String to LocalDate
        if (this.expiryDate != null && !this.expiryDate.isEmpty()) {
            item.setExpiryDate(LocalDate.parse(this.expiryDate, DATE_FORMATTER));
        }
        
        item.setRequiresPrescription(this.requiresPrescription);
        item.setCategory(this.category);
        item.setIsActive(this.isActive);
        
        return item;
    }
}
