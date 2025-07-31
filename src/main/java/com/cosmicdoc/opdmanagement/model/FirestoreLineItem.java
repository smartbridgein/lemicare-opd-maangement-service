package com.cosmicdoc.opdmanagement.model;

import com.google.cloud.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Firestore representation of a LineItem
 */
@Data
@NoArgsConstructor
public class FirestoreLineItem {
    private Timestamp date;
    private String serviceId;
    private String serviceName;
    private String description;
    private String incentive;
    private Integer quantity;
    private Double rate;
    private Double discount;
    private String taxProfileId;
    private List<FirestoreTaxComponent> taxDetails = new ArrayList<>();
    private Double totalAmount;
    private FirestoreTaxInfo taxInfo;
    
    public FirestoreLineItem(LineItem lineItem) {
        this.serviceId = lineItem.getServiceId();
        this.serviceName = lineItem.getServiceName();
        this.description = lineItem.getDescription();
        this.incentive = lineItem.getIncentive();
        this.quantity = lineItem.getQuantity();
        this.rate = lineItem.getRate();
        this.discount = lineItem.getDiscount();
        this.taxProfileId = lineItem.getTaxProfileId();
        this.totalAmount = lineItem.getTotalAmount();
        
        if (lineItem.getDate() != null) {
            this.date = Timestamp.of(Date.from(lineItem.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        
        if (lineItem.getTaxDetails() != null) {
            this.taxDetails = lineItem.getTaxDetails().stream()
                .map(FirestoreTaxComponent::new)
                .collect(Collectors.toList());
        }
        
        if (lineItem.getTaxInfo() != null) {
            this.taxInfo = new FirestoreTaxInfo(lineItem.getTaxInfo());
        }
    }
    
    public LineItem toLineItem() {
        LineItem lineItem = new LineItem();
        
        lineItem.setServiceId(this.serviceId);
        lineItem.setServiceName(this.serviceName);
        lineItem.setDescription(this.description);
        lineItem.setIncentive(this.incentive);
        lineItem.setQuantity(this.quantity != null ? this.quantity : 0);
        lineItem.setRate(this.rate != null ? this.rate : 0.0);
        lineItem.setDiscount(this.discount != null ? this.discount : 0.0);
        lineItem.setTaxProfileId(this.taxProfileId);
        lineItem.setTotalAmount(this.totalAmount != null ? this.totalAmount : 0.0);
        
        if (this.date != null) {
            lineItem.setDate(this.date.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        if (this.taxDetails != null) {
            lineItem.setTaxDetails(this.taxDetails.stream()
                .map(FirestoreTaxComponent::toTaxComponent)
                .collect(Collectors.toList()));
        }
        
        if (this.taxInfo != null) {
            lineItem.setTaxInfo(this.taxInfo.toTaxInfo());
        }
        
        return lineItem;
    }
}
