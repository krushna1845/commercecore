package com.krushna.commercecore.service;

import com.krushna.commercecore.dto.RatingDistributionDTO;
import com.krushna.commercecore.dto.ReviewDetailDTO;
import com.krushna.commercecore.dto.ReviewSummaryDTO;
import com.krushna.commercecore.model.*;
import com.krushna.commercecore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnhancedReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository imageRepository;
    private final ReviewVideoRepository videoRepository;
    private final ReviewVoteRepository voteRepository;
    private final ReviewReplyRepository replyRepository;
    private final ReviewSummaryRepository summaryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public ReviewDetailDTO addReview(Long userId, Long productId, int rating, String comment, 
                                      List<String> imageUrls, List<Map<String, String>> videoData) {
        User user = new User();
        user.setId(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Review review = new Review(user, product, rating, comment);
        reviewRepository.save(review);

        // Add images
        if (imageUrls != null) {
            imageUrls.forEach(url -> imageRepository.save(new ReviewImage(review, url)));
        }

        // Add videos
        if (videoData != null) {
            videoData.forEach(data -> {
                String videoUrl = data.get("videoUrl");
                String thumbnailUrl = data.get("thumbnailUrl");
                videoRepository.save(new ReviewVideo(review, videoUrl, thumbnailUrl));
            });
        }

        return getReviewDetail(review.getId(), userId);
    }

    @Transactional
    public void voteReview(Long userId, Long reviewId, boolean isHelpful) {
        User user = new User();
        user.setId(userId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        voteRepository.findByReviewAndUser(review, user)
                .ifPresentOrElse(
                        existingVote -> {
                            existingVote.setHelpful(isHelpful);
                            voteRepository.save(existingVote);
                        },
                        () -> voteRepository.save(new ReviewVote(review, user, isHelpful))
                );
    }

    @Transactional
    public void removeVote(Long userId, Long reviewId) {
        User user = new User();
        user.setId(userId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        voteRepository.deleteByReviewAndUser(review, user);
    }

    @Transactional
    public void addReply(Long userId, Long reviewId, String content) {
        User user = new User();
        user.setId(userId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        replyRepository.save(new ReviewReply(review, user, content));
    }

    @Transactional
    public void updateReply(Long userId, Long replyId, String content) {
        User user = new User();
        user.setId(userId);

        ReviewReply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));

        if (!reply.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this reply");
        }

        reply.setContent(content);
        replyRepository.save(reply);
    }

    @Transactional
    public void deleteReply(Long userId, Long replyId) {
        User user = new User();
        user.setId(userId);

        ReviewReply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));

        if (!reply.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this reply");
        }

        replyRepository.delete(reply);
    }

    @Transactional(readOnly = true)
    public ReviewDetailDTO getReviewDetail(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        User currentUser = new User();
        currentUser.setId(userId);

        boolean verifiedPurchase = checkVerifiedPurchase(review.getUser().getId(), review.getProduct().getId());

        ReviewDetailDTO dto = new ReviewDetailDTO();
        dto.setId(review.getId());
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getUsername());
        dto.setProductId(review.getProduct().getId());
        dto.setProductName(review.getProduct().getName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setVerifiedPurchase(verifiedPurchase);

        // Images
        dto.setImages(imageRepository.findByReview(review).stream()
                .map(ReviewImage::getImageUrl)
                .collect(Collectors.toList()));

        // Videos
        dto.setVideos(videoRepository.findByReview(review).stream()
                .map(video -> {
                    ReviewDetailDTO.ReviewVideoDTO videoDTO = new ReviewDetailDTO.ReviewVideoDTO();
                    videoDTO.setId(video.getId());
                    videoDTO.setVideoUrl(video.getVideoUrl());
                    videoDTO.setThumbnailUrl(video.getThumbnailUrl());
                    videoDTO.setUploadedAt(video.getUploadedAt());
                    return videoDTO;
                })
                .collect(Collectors.toList()));

        // Votes
        dto.setHelpfulCount(voteRepository.countByReviewAndIsHelpfulTrue(review));
        dto.setNotHelpfulCount(voteRepository.countByReviewAndIsHelpfulFalse(review));

        voteRepository.findByReviewAndUser(review, currentUser)
                .ifPresent(vote -> {
                    dto.setUserVotedHelpful(vote.isHelpful());
                    dto.setUserVotedNotHelpful(!vote.isHelpful());
                });

        // Replies
        dto.setReplies(replyRepository.findByReviewOrderByCreatedAtAsc(review).stream()
                .map(reply -> {
                    ReviewDetailDTO.ReviewReplyDTO replyDTO = new ReviewDetailDTO.ReviewReplyDTO();
                    replyDTO.setId(reply.getId());
                    replyDTO.setUserId(reply.getUser().getId());
                    replyDTO.setUserName(reply.getUser().getUsername());
                    replyDTO.setContent(reply.getContent());
                    replyDTO.setCreatedAt(reply.getCreatedAt());
                    replyDTO.setUpdatedAt(reply.getUpdatedAt());
                    return replyDTO;
                })
                .collect(Collectors.toList()));

        return dto;
    }

    @Transactional(readOnly = true)
    public List<ReviewDetailDTO> getProductReviews(Long productId, Long userId, String sortBy, String filter) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<Review> reviews = reviewRepository.findByProduct(product);

        // Apply filters
        if ("verified".equals(filter)) {
            reviews = reviews.stream()
                    .filter(review -> checkVerifiedPurchase(review.getUser().getId(), productId))
                    .collect(Collectors.toList());
        } else if ("with-images".equals(filter)) {
            reviews = reviews.stream()
                    .filter(review -> !imageRepository.findByReview(review).isEmpty())
                    .collect(Collectors.toList());
        } else if ("with-videos".equals(filter)) {
            reviews = reviews.stream()
                    .filter(review -> !videoRepository.findByReview(review).isEmpty())
                    .collect(Collectors.toList());
        }

        // Apply sorting
        if ("recent".equals(sortBy)) {
            reviews.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        } else if ("helpful".equals(sortBy)) {
            reviews.sort((a, b) -> {
                int helpfulA = voteRepository.countByReviewAndIsHelpfulTrue(a);
                int helpfulB = voteRepository.countByReviewAndIsHelpfulTrue(b);
                return Integer.compare(helpfulB, helpfulA);
            });
        } else if ("highest".equals(sortBy)) {
            reviews.sort((a, b) -> Integer.compare(b.getRating(), a.getRating()));
        } else if ("lowest".equals(sortBy)) {
            reviews.sort((a, b) -> Integer.compare(a.getRating(), b.getRating()));
        }

        return reviews.stream()
                .map(review -> getReviewDetail(review.getId(), userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RatingDistributionDTO getRatingDistribution(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<Review> reviews = reviewRepository.findByProduct(product);

        RatingDistributionDTO dto = new RatingDistributionDTO();
        dto.setFiveStar((int) reviews.stream().filter(r -> r.getRating() == 5).count());
        dto.setFourStar((int) reviews.stream().filter(r -> r.getRating() == 4).count());
        dto.setThreeStar((int) reviews.stream().filter(r -> r.getRating() == 3).count());
        dto.setTwoStar((int) reviews.stream().filter(r -> r.getRating() == 2).count());
        dto.setOneStar((int) reviews.stream().filter(r -> r.getRating() == 1).count());
        dto.setTotalReviews(reviews.size());

        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        dto.setAverageRating(Math.round(average * 10.0) / 10.0);

        return dto;
    }

    @Transactional
    public ReviewSummaryDTO generateReviewSummary(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<Review> reviews = reviewRepository.findByProduct(product);

        // Generate AI summary (simplified - in production, integrate with AI service)
        String summary = generateSummaryText(reviews);
        String pros = generateProsText(reviews);
        String cons = generateConsText(reviews);

        ReviewSummary summaryEntity = summaryRepository.findByProduct(product)
                .orElse(new ReviewSummary(product, summary, pros, cons));

        summaryEntity.setSummary(summary);
        summaryEntity.setPros(pros);
        summaryEntity.setCons(cons);
        summaryRepository.save(summaryEntity);

        return new ReviewSummaryDTO(
                productId,
                summary,
                pros,
                cons,
                summaryEntity.getGeneratedAt(),
                summaryEntity.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public ReviewSummaryDTO getReviewSummary(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return summaryRepository.findByProduct(product)
                .map(summary -> new ReviewSummaryDTO(
                        productId,
                        summary.getSummary(),
                        summary.getPros(),
                        summary.getCons(),
                        summary.getGeneratedAt(),
                        summary.getUpdatedAt()
                ))
                .orElse(null);
    }

    private boolean checkVerifiedPurchase(Long userId, Long productId) {
        // Check if user has purchased this product
        User user = new User();
        user.setId(userId);

        Product product = new Product();
        product.setId(productId);

        return orderRepository.findByUser(user).stream()
                .flatMap(order -> order.getItems().stream())
                .anyMatch(item -> item.getProduct().getId().equals(productId));
    }

    private String generateSummaryText(List<Review> reviews) {
        if (reviews.isEmpty()) return "No reviews available.";
        
        // Simplified summary generation - in production, use AI service
        double avgRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        
        return String.format("Based on %d reviews, customers rate this product %.1f out of 5 stars. " +
                "Users generally appreciate the quality and value for money.", 
                reviews.size(), avgRating);
    }

    private String generateProsText(List<Review> reviews) {
        // Simplified pros extraction - in production, use NLP/AI
        return "Good quality, Great value, Fast shipping, Excellent customer service";
    }

    private String generateConsText(List<Review> reviews) {
        // Simplified cons extraction - in production, use NLP/AI
        return "Limited color options, Packaging could be improved";
    }
}
