package com.krushna.commercecore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDetailDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long productId;
    private String productName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    private boolean verifiedPurchase;
    private List<String> images;
    private List<ReviewVideoDTO> videos;
    private int helpfulCount;
    private int notHelpfulCount;
    private boolean userVotedHelpful;
    private boolean userVotedNotHelpful;
    private List<ReviewReplyDTO> replies;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isVerifiedPurchase() { return verifiedPurchase; }
    public void setVerifiedPurchase(boolean verifiedPurchase) { this.verifiedPurchase = verifiedPurchase; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public List<ReviewVideoDTO> getVideos() { return videos; }
    public void setVideos(List<ReviewVideoDTO> videos) { this.videos = videos; }
    public int getHelpfulCount() { return helpfulCount; }
    public void setHelpfulCount(int helpfulCount) { this.helpfulCount = helpfulCount; }
    public int getNotHelpfulCount() { return notHelpfulCount; }
    public void setNotHelpfulCount(int notHelpfulCount) { this.notHelpfulCount = notHelpfulCount; }
    public boolean isUserVotedHelpful() { return userVotedHelpful; }
    public void setUserVotedHelpful(boolean userVotedHelpful) { this.userVotedHelpful = userVotedHelpful; }
    public boolean isUserVotedNotHelpful() { return userVotedNotHelpful; }
    public void setUserVotedNotHelpful(boolean userVotedNotHelpful) { this.userVotedNotHelpful = userVotedNotHelpful; }
    public List<ReviewReplyDTO> getReplies() { return replies; }
    public void setReplies(List<ReviewReplyDTO> replies) { this.replies = replies; }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewVideoDTO {
        private Long id;
        private String videoUrl;
        private String thumbnailUrl;
        private LocalDateTime uploadedAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getVideoUrl() { return videoUrl; }
        public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
        public String getThumbnailUrl() { return thumbnailUrl; }
        public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
        public LocalDateTime getUploadedAt() { return uploadedAt; }
        public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewReplyDTO {
        private Long id;
        private Long userId;
        private String userName;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}
