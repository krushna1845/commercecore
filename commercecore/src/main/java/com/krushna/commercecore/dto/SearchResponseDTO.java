package com.krushna.commercecore.dto;

import java.util.List;

public class SearchResponseDTO {
    private List<ProductResponseDTO> products;
    private long total;
    private int page;
    private int size;

    public SearchResponseDTO() {}

    public SearchResponseDTO(List<ProductResponseDTO> products, long total, int page, int size) {
        this.products = products;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public List<ProductResponseDTO> getProducts() { return products; }
    public void setProducts(List<ProductResponseDTO> products) { this.products = products; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
