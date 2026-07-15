package com.krushna.commercecore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class ReviewSummaryDTO {
    private Long productId;
    private String summary;
    private String pros;
    private String cons;
    private LocalDateTime generatedAt;
    private LocalDateTime updatedAt;

    public ReviewSummaryDTO(Long productId, String summary, String pros, String cons, LocalDateTime generatedAt, LocalDateTime updatedAt) {
        this.productId = productId;
        this.summary = summary;
        this.pros = pros;
        this.cons = cons;
        this.generatedAt = generatedAt;
        this.updatedAt = updatedAt;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getPros() { return pros; }
    public void setPros(String pros) { this.pros = pros; }
    public String getCons() { return cons; }
    public void setCons(String cons) { this.cons = cons; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
