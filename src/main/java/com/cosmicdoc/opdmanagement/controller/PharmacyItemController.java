package com.cosmicdoc.opdmanagement.controller;

import com.cosmicdoc.opdmanagement.dto.PharmacyItemDTO;
import com.cosmicdoc.opdmanagement.service.PharmacyItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pharmacy-items")
public class PharmacyItemController {
    
    private final PharmacyItemService pharmacyItemService;
    
    @Autowired
    public PharmacyItemController(PharmacyItemService pharmacyItemService) {
        this.pharmacyItemService = pharmacyItemService;
    }
    
    @GetMapping
    public ResponseEntity<List<PharmacyItemDTO>> getAllItems() {
        List<PharmacyItemDTO> items = pharmacyItemService.getAllPharmacyItems();
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PharmacyItemDTO> getItemById(@PathVariable String id) {
        PharmacyItemDTO item = pharmacyItemService.getPharmacyItemById(id);
        return ResponseEntity.ok(item);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<PharmacyItemDTO>> getItemsByCategory(@PathVariable String category) {
        List<PharmacyItemDTO> items = pharmacyItemService.getPharmacyItemsByCategory(category);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/manufacturer/{manufacturer}")
    public ResponseEntity<List<PharmacyItemDTO>> getItemsByManufacturer(@PathVariable String manufacturer) {
        List<PharmacyItemDTO> items = pharmacyItemService.getPharmacyItemsByManufacturer(manufacturer);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/expiring-before")
    public ResponseEntity<List<PharmacyItemDTO>> getItemsExpiringBefore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<PharmacyItemDTO> items = pharmacyItemService.getPharmacyItemsExpiringBefore(date);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<PharmacyItemDTO>> searchItemsByName(@RequestParam String name) {
        List<PharmacyItemDTO> items = pharmacyItemService.getPharmacyItemsByNameContaining(name);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<List<PharmacyItemDTO>> getLowStockItems(@RequestParam(defaultValue = "10") int threshold) {
        List<PharmacyItemDTO> items = pharmacyItemService.getLowStockItems(threshold);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/prescription-required")
    public ResponseEntity<List<PharmacyItemDTO>> getItemsByPrescriptionRequirement(
            @RequestParam(defaultValue = "true") boolean requiresPrescription) {
        List<PharmacyItemDTO> items = pharmacyItemService.getItemsByPrescriptionRequirement(requiresPrescription);
        return ResponseEntity.ok(items);
    }
    
    @PostMapping
    public ResponseEntity<PharmacyItemDTO> createItem(@Valid @RequestBody PharmacyItemDTO itemDTO) {
        PharmacyItemDTO createdItem = pharmacyItemService.createPharmacyItem(itemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PharmacyItemDTO> updateItem(
            @PathVariable String id,
            @Valid @RequestBody PharmacyItemDTO itemDTO) {
        PharmacyItemDTO updatedItem = pharmacyItemService.updatePharmacyItem(id, itemDTO);
        return ResponseEntity.ok(updatedItem);
    }
    
    @PatchMapping("/{id}/stock")
    public ResponseEntity<PharmacyItemDTO> updateStock(
            @PathVariable String id,
            @RequestBody Map<String, Integer> request) {
        Integer quantity = request.get("quantity");
        if (quantity == null) {
            return ResponseEntity.badRequest().build();
        }
        PharmacyItemDTO updatedItem = pharmacyItemService.updateStock(id, quantity);
        return ResponseEntity.ok(updatedItem);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable String id) {
        pharmacyItemService.deletePharmacyItem(id);
        return ResponseEntity.noContent().build();
    }
}
