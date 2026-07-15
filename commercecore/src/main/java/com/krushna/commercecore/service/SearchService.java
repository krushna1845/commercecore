package com.krushna.commercecore.service;

import com.krushna.commercecore.dto.ProductResponseDTO;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.model.RecentSearch;
import com.krushna.commercecore.model.SearchSynonym;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.ProductRepository;
import com.krushna.commercecore.repository.ProductSpecification;
import com.krushna.commercecore.repository.RecentSearchRepository;
import com.krushna.commercecore.repository.SearchSynonymRepository;
import com.krushna.commercecore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RecentSearchRepository recentSearchRepository;

    @Autowired
    private SearchSynonymRepository searchSynonymRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    public Page<ProductResponseDTO> advancedSearch(String keyword, Double minPrice, Double maxPrice, String brand, 
                                                   Double minRating, Boolean inStock, int page, int size, String sortBy, String sortDir, Long userId) {
        
        String resolvedKeyword = keyword;
        if (keyword != null && !keyword.trim().isEmpty()) {
            resolvedKeyword = resolveSynonyms(keyword.trim());
            logSearchQuery(keyword.trim(), userId);
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir != null ? sortDir : "DESC"), sortBy != null ? sortBy : "rating");
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Product> spec = ProductSpecification.filterProducts(resolvedKeyword, minPrice, maxPrice, brand, minRating, inStock);

        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(product -> productService.convertToDTO(product));
    }

    public List<String> autocompleteSuggest(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return List.of();
        }
        List<Product> matches = productRepository.findByNameContainingIgnoreCase(prefix);
        return matches.stream()
                .map(Product::getName)
                .distinct()
                .limit(5)
                .collect(Collectors.toList());
    }

    public List<String> getTrendingSearches() {
        return recentSearchRepository.findTop10ByOrderBySearchCountDesc()
                .stream()
                .map(RecentSearch::getQuery)
                .collect(Collectors.toList());
    }

    private String resolveSynonyms(String keyword) {
        Optional<SearchSynonym> synOpt = searchSynonymRepository.findByKeywordIgnoreCase(keyword);
        if (synOpt.isPresent()) {
            String[] tokens = synOpt.get().getSynonyms().split(",");
            if (tokens.length > 0) {
                return tokens[0].trim();
            }
        }
        return keyword;
    }

    private void logSearchQuery(String query, Long userId) {
        Optional<RecentSearch> searchOpt;
        if (userId != null) {
            searchOpt = recentSearchRepository.findByQueryIgnoreCaseAndUserId(query, userId);
        } else {
            searchOpt = recentSearchRepository.findByQueryIgnoreCaseAndUserIdIsNull(query);
        }

        RecentSearch recentSearch;
        if (searchOpt.isPresent()) {
            recentSearch = searchOpt.get();
            recentSearch.setSearchCount(recentSearch.getSearchCount() + 1);
            recentSearch.setLastSearchedAt(java.time.LocalDateTime.now());
        } else {
            User user = null;
            if (userId != null) {
                user = userRepository.findById(userId).orElse(null);
            }
            recentSearch = new RecentSearch(query, user);
        }
        recentSearchRepository.save(recentSearch);
    }
}
