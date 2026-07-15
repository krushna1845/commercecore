package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.ProductResponseDTO;
import com.krushna.commercecore.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin("*")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> advancedSearch(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false) Long userId
    ) {
        Page<ProductResponseDTO> res = searchService.advancedSearch(q, minPrice, maxPrice, brand, minRating, inStock, page, size, sortBy, sortDir, userId);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> suggestions(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(searchService.autocompleteSuggest(q));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<String>> popular() {
        return ResponseEntity.ok(searchService.getTrendingSearches());
    }
}
