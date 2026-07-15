package com.krushna.commercecore.dto;

import java.util.List;

public class SuggestionDTO {
    private List<String> terms;
    private List<CategorySuggestion> categories;
    private List<String> brands;
    private List<String> priceRanges;

    public static class CategorySuggestion {
        public Long id;
        public String name;
        public CategorySuggestion() {}
        public CategorySuggestion(Long id, String name) { this.id = id; this.name = name; }
    }

    public List<String> getTerms() { return terms; }
    public void setTerms(List<String> terms) { this.terms = terms; }
    public List<CategorySuggestion> getCategories() { return categories; }
    public void setCategories(List<CategorySuggestion> categories) { this.categories = categories; }
    public List<String> getBrands() { return brands; }
    public void setBrands(List<String> brands) { this.brands = brands; }
    public List<String> getPriceRanges() { return priceRanges; }
    public void setPriceRanges(List<String> priceRanges) { this.priceRanges = priceRanges; }
}
