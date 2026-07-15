package com.krushna.commercecore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class ComparisonResultDTO {
    private List<ProductComparisonDTO> products;
    private int totalCount;
    private boolean canAddMore;

    public ComparisonResultDTO(List<ProductComparisonDTO> products, int totalCount, boolean canAddMore) {
        this.products = products;
        this.totalCount = totalCount;
        this.canAddMore = canAddMore;
    }

    public List<ProductComparisonDTO> getProducts() { return products; }
    public void setProducts(List<ProductComparisonDTO> products) { this.products = products; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public boolean isCanAddMore() { return canAddMore; }
    public void setCanAddMore(boolean canAddMore) { this.canAddMore = canAddMore; }
}
