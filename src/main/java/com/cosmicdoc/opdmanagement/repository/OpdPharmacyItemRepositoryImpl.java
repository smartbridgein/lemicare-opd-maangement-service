package com.cosmicdoc.opdmanagement.repository;

import com.cosmicdoc.opdmanagement.model.PharmacyItem;
import com.cosmicdoc.opdmanagement.repository.PharmacyItemRepository;
import com.cosmicdoc.opdmanagement.model.FirestorePharmacyItem;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
@Primary
public class OpdPharmacyItemRepositoryImpl implements PharmacyItemRepository {
    private static final Logger logger = LoggerFactory.getLogger(OpdPharmacyItemRepositoryImpl.class);
    private static final String COLLECTION_NAME = "pharmacy_items";

    private final Firestore firestore;

    @Autowired
    public OpdPharmacyItemRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
        logger.info("Initialized OpdPharmacyItemRepositoryImpl with direct Firestore access");
    }

    @Override
    public List<PharmacyItem> findAll() {
        List<PharmacyItem> items = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            for (QueryDocumentSnapshot document : documents) {
                FirestorePharmacyItem firestoreItem = document.toObject(FirestorePharmacyItem.class);
                items.add(firestoreItem.toPharmacyItem());
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error retrieving all pharmacy items: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return items;
    }

    @Override
    public Optional<PharmacyItem> findById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                FirestorePharmacyItem firestoreItem = document.toObject(FirestorePharmacyItem.class);
                return Optional.ofNullable(firestoreItem).map(FirestorePharmacyItem::toPharmacyItem);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error retrieving pharmacy item with ID {}: {}", id, e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return Optional.empty();
    }

    @Override
    public List<PharmacyItem> findByCategory(String category) {
        return findByField("category", category);
    }

    @Override
    public List<PharmacyItem> findByManufacturer(String manufacturer) {
        return findByField("manufacturer", manufacturer);
    }

    @Override
    public List<PharmacyItem> findByExpiryDateBefore(LocalDate date) {
        List<PharmacyItem> items = new ArrayList<>();
        String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            for (QueryDocumentSnapshot document : documents) {
                FirestorePharmacyItem firestoreItem = document.toObject(FirestorePharmacyItem.class);
                PharmacyItem item = firestoreItem.toPharmacyItem();
                
                if (item.getExpiryDate() != null && item.getExpiryDate().isBefore(date)) {
                    items.add(item);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error finding pharmacy items with expiry date before {}: {}", dateStr, e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return items;
    }

    @Override
    public List<PharmacyItem> findByNameContaining(String name) {
        List<PharmacyItem> items = new ArrayList<>();
        try {
            // Firestore doesn't support native contains queries, so we'll do a full scan
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            for (QueryDocumentSnapshot document : documents) {
                FirestorePharmacyItem firestoreItem = document.toObject(FirestorePharmacyItem.class);
                PharmacyItem item = firestoreItem.toPharmacyItem();
                if (item.getName() != null && item.getName().toLowerCase().contains(name.toLowerCase())) {
                    items.add(item);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error searching pharmacy items by name containing {}: {}", name, e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return items;
    }

    @Override
    public List<PharmacyItem> findByStockQuantityLessThan(int threshold) {
        List<PharmacyItem> items = new ArrayList<>();
        try {
            Query query = firestore.collection(COLLECTION_NAME).whereLessThan("stockQuantity", threshold);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            for (QueryDocumentSnapshot document : documents) {
                FirestorePharmacyItem firestoreItem = document.toObject(FirestorePharmacyItem.class);
                items.add(firestoreItem.toPharmacyItem());
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error retrieving low stock pharmacy items: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return items;
    }

    @Override
    public List<PharmacyItem> findByRequiresPrescription(boolean requiresPrescription) {
        return findByField("requiresPrescription", requiresPrescription);
    }

    @Override
    public PharmacyItem save(PharmacyItem item) {
        try {
            // Convert PharmacyItem to FirestorePharmacyItem
            FirestorePharmacyItem firestoreItem = FirestorePharmacyItem.fromPharmacyItem(item);
            
            DocumentReference docRef;
            if (firestoreItem.getId() != null && !firestoreItem.getId().isEmpty()) {
                docRef = firestore.collection(COLLECTION_NAME).document(firestoreItem.getId());
            } else {
                docRef = firestore.collection(COLLECTION_NAME).document();
                firestoreItem.setId(docRef.getId());
                item.setId(docRef.getId()); // Update original item with the generated ID
            }
            
            ApiFuture<WriteResult> result = docRef.set(firestoreItem);
            result.get(); // Wait for the write to complete
            
            return item;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error saving pharmacy item: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to save pharmacy item", e);
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<WriteResult> writeResult = docRef.delete();
            writeResult.get(); // Wait for deletion to complete
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error deleting pharmacy item with ID {}: {}", id, e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to delete pharmacy item", e);
        }
    }

    @Override
    public boolean existsById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            return document.exists();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error checking if pharmacy item exists with ID {}: {}", id, e.getMessage(), e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    // Helper method for querying by a specific field value
    private List<PharmacyItem> findByField(String fieldName, Object value) {
        List<PharmacyItem> items = new ArrayList<>();
        try {
            Query query = firestore.collection(COLLECTION_NAME).whereEqualTo(fieldName, value);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            for (QueryDocumentSnapshot document : documents) {
                FirestorePharmacyItem firestoreItem = document.toObject(FirestorePharmacyItem.class);
                items.add(firestoreItem.toPharmacyItem());
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error retrieving pharmacy items by {} = {}: {}", fieldName, value, e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return items;
    }
}
