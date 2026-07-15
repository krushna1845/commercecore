package com.krushna.commercecore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingMethodDTO {
    private Long id;
    private String name;
    private String description;
    private double basePrice;
    private double pricePerKg;
    private int estimatedDaysMin;
    private int estimatedDaysMax;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public double getPricePerKg() { return pricePerKg; }
    public void setPricePerKg(double pricePerKg) { this.pricePerKg = pricePerKg; }
    public int getEstimatedDaysMin() { return estimatedDaysMin; }
    public void setEstimatedDaysMin(int estimatedDaysMin) { this.estimatedDaysMin = estimatedDaysMin; }
    public int getEstimatedDaysMax() { return estimatedDaysMax; }
    public void setEstimatedDaysMax(int estimatedDaysMax) { this.estimatedDaysMax = estimatedDaysMax; }
}
