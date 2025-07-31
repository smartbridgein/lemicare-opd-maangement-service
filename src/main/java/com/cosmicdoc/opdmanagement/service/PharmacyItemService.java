package com.cosmicdoc.opdmanagement.service;

import com.cosmicdoc.opdmanagement.model.PharmacyItem;
import com.cosmicdoc.opdmanagement.repository.PharmacyItemRepository;
import com.cosmicdoc.opdmanagement.dto.PharmacyItemDTO;
import com.cosmicdoc.opdmanagement.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PharmacyItemService {
    private final PharmacyItemRepository pharmacyItemRepository;

    @Autowired
    public PharmacyItemService(PharmacyItemRepository pharmacyItemRepository) {
        this.pharmacyItemRepository = pharmacyItemRepository;
    }

    public List<PharmacyItemDTO> getAllPharmacyItems() {
        return pharmacyItemRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PharmacyItemDTO getPharmacyItemById(String id) {
        Optional<PharmacyItem> itemOptional = pharmacyItemRepository.findById(id);
        return itemOptional.map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy item not found with id: " + id));
    }

    public List<PharmacyItemDTO> getPharmacyItemsByCategory(String category) {
        return pharmacyItemRepository.findByCategory(category).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PharmacyItemDTO> getPharmacyItemsByManufacturer(String manufacturer) {
        return pharmacyItemRepository.findByManufacturer(manufacturer).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PharmacyItemDTO> getPharmacyItemsExpiringBefore(LocalDate date) {
        return pharmacyItemRepository.findByExpiryDateBefore(date).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PharmacyItemDTO> getPharmacyItemsByNameContaining(String name) {
        return pharmacyItemRepository.findByNameContaining(name).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PharmacyItemDTO> getLowStockItems(int threshold) {
        return pharmacyItemRepository.findByStockQuantityLessThan(threshold).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PharmacyItemDTO> getItemsByPrescriptionRequirement(boolean requiresPrescription) {
        return pharmacyItemRepository.findByRequiresPrescription(requiresPrescription).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PharmacyItemDTO createPharmacyItem(PharmacyItemDTO pharmacyItemDTO) {
        // Map DTO to entity
        PharmacyItem item = mapToEntity(pharmacyItemDTO);
        
        // Generate UUID if id is not provided
        if (item.getId() == null || item.getId().isEmpty()) {
            item.setId(UUID.randomUUID().toString());
        }

        // Save the item
        PharmacyItem savedItem = pharmacyItemRepository.save(item);

        // Return mapped DTO
        return mapToDTO(savedItem);
    }

    public PharmacyItemDTO updatePharmacyItem(String id, PharmacyItemDTO pharmacyItemDTO) {
        // Check if pharmacy item exists
        if (!pharmacyItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pharmacy item not found with id: " + id);
        }

        // Map DTO to entity and ensure ID is set
        PharmacyItem item = mapToEntity(pharmacyItemDTO);
        item.setId(id);

        // Save updated pharmacy item
        PharmacyItem savedItem = pharmacyItemRepository.save(item);

        // Return mapped DTO
        return mapToDTO(savedItem);
    }

    public PharmacyItemDTO updateStock(String id, int quantity) {
        // Get existing pharmacy item
        PharmacyItem item = pharmacyItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pharmacy item not found with id: " + id));
        
        // Update stock quantity
        item.setStockQuantity(quantity);
        
        // Save updated pharmacy item
        PharmacyItem savedItem = pharmacyItemRepository.save(item);
        
        // Return mapped DTO
        return mapToDTO(savedItem);
    }

    public void deletePharmacyItem(String id) {
        // Check if pharmacy item exists
        if (!pharmacyItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pharmacy item not found with id: " + id);
        }

        // Delete pharmacy item
        pharmacyItemRepository.deleteById(id);
    }

    // Helper methods to map between entity and DTO
    private PharmacyItemDTO mapToDTO(PharmacyItem item) {
        PharmacyItemDTO dto = new PharmacyItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setDosageForm(item.getDosageForm());
        dto.setManufacturer(item.getManufacturer());
        dto.setBatchNumber(item.getBatchNumber());
        dto.setPrice(item.getPrice());
        dto.setStockQuantity(item.getStockQuantity());
        dto.setExpiryDate(item.getExpiryDate());
        dto.setRequiresPrescription(item.getRequiresPrescription());
        dto.setCategory(item.getCategory());
        dto.setIsActive(item.getIsActive());
        return dto;
    }

    private PharmacyItem mapToEntity(PharmacyItemDTO dto) {
        PharmacyItem item = new PharmacyItem();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setDosageForm(dto.getDosageForm());
        item.setManufacturer(dto.getManufacturer());
        item.setBatchNumber(dto.getBatchNumber());
        item.setPrice(dto.getPrice());
        item.setStockQuantity(dto.getStockQuantity());
        item.setExpiryDate(dto.getExpiryDate());
        item.setRequiresPrescription(dto.getRequiresPrescription());
        item.setCategory(dto.getCategory());
        item.setIsActive(dto.getIsActive());
        return item;
    }
}
