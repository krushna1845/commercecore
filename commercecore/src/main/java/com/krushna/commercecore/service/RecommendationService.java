package com.krushna.commercecore.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.krushna.commercecore.dto.RecommendationResponse;
import com.krushna.commercecore.dto.RecommendedProductDto;
import com.krushna.commercecore.model.BrowsingHistory;
import com.krushna.commercecore.model.Category;
import com.krushna.commercecore.model.CategorySimilarity;
import com.krushna.commercecore.model.FrequentlyBoughtTogether;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.model.ProductSalesFrequency;
import com.krushna.commercecore.model.UserRecommendation;
import com.krushna.commercecore.repository.BrowsingHistoryRepository;
import com.krushna.commercecore.repository.CategorySimilarityRepository;
import com.krushna.commercecore.repository.FrequentlyBoughtTogetherRepository;
import com.krushna.commercecore.repository.ProductRatingRepository;
import com.krushna.commercecore.repository.ProductRecommendationRepository;
import com.krushna.commercecore.repository.ProductRepository;
import com.krushna.commercecore.repository.ProductSalesFrequencyRepository;
import com.krushna.commercecore.repository.ProductViewStatsRepository;
import com.krushna.commercecore.repository.UserRecommendationRepository;

@Service
@Transactional(readOnly = true)
public class RecommendationService {
    
    private static final int DEFAULT_LIMIT = 10;
    private static final String FREQUENTLY_BOUGHT = "frequently_bought";
    private static final String CUSTOMERS_ALSO_BOUGHT = "customers_also_bought";
    private static final String RECOMMENDED_FOR_YOU = "recommended_for_you";
    private static final String TRENDING = "trending";
    private static final String POPULAR = "popular";
    private static final String RECENTLY_VIEWED = "recently_viewed";
    
    @Autowired private FrequentlyBoughtTogetherRepository frequentlyBoughtRepository;
    @Autowired private BrowsingHistoryRepository browsingHistoryRepository;
    @Autowired private ProductSalesFrequencyRepository salesFrequencyRepository;
    @Autowired private ProductRatingRepository ratingRepository;
    @Autowired private ProductRecommendationRepository recommendationRepository;
    @Autowired private UserRecommendationRepository userRecommendationRepository;
    @Autowired private CategorySimilarityRepository categorySimilarityRepository;
    @Autowired private ProductViewStatsRepository viewStatsRepository;
    @Autowired private ProductRepository productRepository;
    
    // Frequently Bought Together - products often bought with current product
    @Cacheable(value = "frequently_bought", key = "#productId")
    public RecommendationResponse getFrequentlyBoughtTogether(Long productId, int limit) {
        List<FrequentlyBoughtTogether> frequently = frequentlyBoughtRepository
            .findByProductIdOrderByConfidenceScoreDesc(productId);
        
        List<RecommendedProductDto> products = frequently.stream()
            .limit(limit > 0 ? limit : DEFAULT_LIMIT)
            .map(f -> buildProductDto(f.getRelatedProductId(), f.getConfidenceScore(), frequently.indexOf(f) + 1))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        return new RecommendationResponse(FREQUENTLY_BOUGHT, products);
    }
    
    // Customers Also Bought - based on similar categories and purchase patterns
    @Cacheable(value = "customers_also_bought", key = "#productId")
    public RecommendationResponse getCustomersAlsoBought(Long productId, int limit) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) return new RecommendationResponse(CUSTOMERS_ALSO_BOUGHT, new ArrayList<>());
        
        Category category = product.get().getCategory();
        if (category == null) return new RecommendationResponse(CUSTOMERS_ALSO_BOUGHT, new ArrayList<>());
        
        List<CategorySimilarity> similarCategories = categorySimilarityRepository
            .findSimilarCategories(category.getId(), 5);
        
        Set<Long> recommendedIds = new HashSet<>();
        List<RecommendedProductDto> products = new ArrayList<>();
        
        similarCategories.forEach(sim -> {
            Long otherCategoryId = sim.getCategoryId2().equals(category.getId()) ? 
                sim.getCategoryId1() : sim.getCategoryId2();
            
            List<Product> categoryProducts = productRepository.findByCategoryId(otherCategoryId);
            
            categoryProducts.stream()
                .filter(p -> !p.getId().equals(productId) && !recommendedIds.contains(p.getId()))
                .limit(limit)
                .forEach(p -> {
                    RecommendedProductDto dto = buildProductDto(p.getId(), sim.getSimilarityScore(), products.size() + 1);
                    if (dto != null) {
                        products.add(dto);
                        recommendedIds.add(p.getId());
                    }
                });
        });
        
        return new RecommendationResponse(CUSTOMERS_ALSO_BOUGHT, 
            products.stream().limit(limit > 0 ? limit : DEFAULT_LIMIT).collect(Collectors.toList()));
    }
    
    // Recommended For You - personalized based on user history
    @Cacheable(value = "recommended_for_you", key = "#userId")
    public RecommendationResponse getRecommendedForYou(Long userId, int limit) {
        List<UserRecommendation> cached = userRecommendationRepository
            .findByUserIdAndType(userId, RECOMMENDED_FOR_YOU, LocalDateTime.now(), limit > 0 ? limit : DEFAULT_LIMIT);
        
        if (!cached.isEmpty()) {
            List<RecommendedProductDto> products = cached.stream()
                .map(r -> buildProductDto(r.getProductId(), r.getScore(), r.getRankPosition()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            return new RecommendationResponse(RECOMMENDED_FOR_YOU, products);
        }
        
        // Compute from user browsing history and ratings
        List<BrowsingHistory> browsingHistory = browsingHistoryRepository
            .findRecentByUserId(userId, LocalDateTime.now().minusDays(30), 100);
        
        Map<Long, Double> scores = new HashMap<>();
        browsingHistory.forEach(bh -> 
            scores.put(bh.getProductId(), scores.getOrDefault(bh.getProductId(), 0.0) + 0.1)
        );
        
        AtomicInteger rank = new AtomicInteger(1);
        List<RecommendedProductDto> products = scores.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(limit > 0 ? limit : DEFAULT_LIMIT)
            .map(e -> buildProductDto(e.getKey(), e.getValue(), rank.getAndIncrement()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        return new RecommendationResponse(RECOMMENDED_FOR_YOU, products);
    }
    
    // Trending - top products by sales in last 24 hours
    @Cacheable(value = "trending", key = "'all'")
    public RecommendationResponse getTrendingProducts(int limit) {
        List<ProductSalesFrequency> trending = salesFrequencyRepository
            .findTrendingProducts(limit > 0 ? limit : DEFAULT_LIMIT);
        
        List<RecommendedProductDto> products = trending.stream()
            .map(s -> buildProductDto(s.getProductId(), s.getSalesCount24h().doubleValue(), trending.indexOf(s) + 1))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        return new RecommendationResponse(TRENDING, products);
    }
    
    // Popular - top products by sales in 7-30 days
    @Cacheable(value = "popular", key = "'all'")
    public RecommendationResponse getPopularProducts(int limit) {
        List<ProductSalesFrequency> popular = salesFrequencyRepository
            .findPopularProducts30d(limit > 0 ? limit : DEFAULT_LIMIT);
        
        List<RecommendedProductDto> products = popular.stream()
            .map(s -> buildProductDto(s.getProductId(), s.getSalesCount30d().doubleValue() / 30, popular.indexOf(s) + 1))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        return new RecommendationResponse(POPULAR, products);
    }
    
    // Recently Viewed - products user viewed recently
    @Cacheable(value = "recently_viewed", key = "#userId")
    public RecommendationResponse getRecentlyViewed(Long userId, int limit) {
        List<BrowsingHistory> recent = browsingHistoryRepository
            .findRecentByUserId(userId, LocalDateTime.now().minusDays(30), limit > 0 ? limit : DEFAULT_LIMIT);
        
        List<RecommendedProductDto> products = recent.stream()
            .map(b -> buildProductDto(b.getProductId(), 1.0, recent.indexOf(b) + 1))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        return new RecommendationResponse(RECENTLY_VIEWED, products);
    }
    
    @Transactional
    @CacheEvict(value = {"frequently_bought", "customers_also_bought", "trending", "popular"}, allEntries = true)
    public void recordPurchase(Long productId, Long relatedProductId) {
        FrequentlyBoughtTogether existing = frequentlyBoughtRepository
            .findByProductIdAndRelatedProductId(productId, relatedProductId)
            .orElse(new FrequentlyBoughtTogether(productId, relatedProductId));
        
        existing.setPurchaseCount(existing.getPurchaseCount() + 1);
        existing.setConfidenceScore(calculateConfidenceScore(existing.getPurchaseCount()));
        existing.setUpdatedAt(LocalDateTime.now());
        frequentlyBoughtRepository.save(existing);
    }
    
    @Transactional
    @CacheEvict(value = "recently_viewed", key = "#userId")
    public void recordBrowsing(Long userId, Long productId) {
        BrowsingHistory history = new BrowsingHistory(userId, productId);
        browsingHistoryRepository.save(history);
    }
    
    private RecommendedProductDto buildProductDto(Long productId, Double score, Integer position) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) return null;
        
        Product p = product.get();
        Double avgRating = ratingRepository.findAverageRatingByProductId(productId);
        long reviewCount = ratingRepository.countByProductId(productId);
        
        RecommendedProductDto dto = new RecommendedProductDto(productId, p.getName(), p.getPrice(), avgRating);
        dto.setDescription(p.getDescription());
        dto.setReviewCount((int) reviewCount);
        dto.setScore(score);
        dto.setRankPosition(position);
        dto.setImageUrl(p.getImageUrl());
        
        return dto;
    }
    
    private double calculateConfidenceScore(int purchaseCount) {
        return Math.min(1.0, purchaseCount / 100.0);
    }
}
