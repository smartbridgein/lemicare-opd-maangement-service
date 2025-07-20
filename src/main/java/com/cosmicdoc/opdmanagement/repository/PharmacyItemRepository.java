package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.PharmacyItem;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PharmacyItemRepository {
    List<PharmacyItem> findAll();
    Optional<PharmacyItem> findById(String id);
    List<PharmacyItem> findByCategory(String category);
    List<PharmacyItem> findByManufacturer(String manufacturer);
    List<PharmacyItem> findByExpiryDateBefore(LocalDate date);
    List<PharmacyItem> findByNameContaining(String name);
    List<PharmacyItem> findByStockQuantityLessThan(int threshold);
    List<PharmacyItem> findByRequiresPrescription(boolean requiresPrescription);
    PharmacyItem save(PharmacyItem item);
    void deleteById(String id);
    boolean existsById(String id);
}
