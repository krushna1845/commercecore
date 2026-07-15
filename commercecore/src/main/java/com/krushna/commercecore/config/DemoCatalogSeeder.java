package com.krushna.commercecore.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.krushna.commercecore.model.ApprovalStatus;
import com.krushna.commercecore.model.Category;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.repository.CategoryRepository;
import com.krushna.commercecore.repository.ProductRepository;

/** Adds a small, functional catalog only for a brand-new empty database. */
@Configuration
public class DemoCatalogSeeder {

    @Bean
    @ConditionalOnProperty(name = "app.demo-catalog.enabled", havingValue = "true", matchIfMissing = true)
    CommandLineRunner seedDemoCatalog(ProductRepository products, CategoryRepository categories) {
        return args -> seedIfEmpty(products, categories);
    }

    @Transactional
    void seedIfEmpty(ProductRepository products, CategoryRepository categories) {
        if (products.count() > 0) {
            return;
        }

        List<CatalogItem> catalog = List.of(
            new CatalogItem("Wireless Noise-Cancelling Headphones", "Electronics", "SonicPro", 8999, 14999, 24, 4.6, 2143, "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=900&q=85&auto=format&fit=crop"),
            new CatalogItem("Smart Fitness Watch Series 7", "Electronics", "PulseFit", 12499, 19999, 12, 4.4, 980, "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=900&q=85&auto=format&fit=crop"),
            new CatalogItem("Minimalist Cotton Tee", "Fashion", "Northvale", 799, 1499, 80, 4.2, 412, "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=900&q=85&auto=format&fit=crop"),
            new CatalogItem("Ergonomic Mesh Office Chair", "Home & Living", "WorkWell", 11999, 17999, 8, 4.5, 712, "https://images.unsplash.com/photo-1592078615290-033ee584e267?w=900&q=85&auto=format&fit=crop"),
            new CatalogItem("Hydra Glow Vitamin C Serum", "Beauty", "Lumiere", 1199, 1799, 60, 4.3, 1521, "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=900&q=85&auto=format&fit=crop"),
            new CatalogItem("Trail Runner Pro Sneakers", "Sports", "Striderix", 4499, 6999, 22, 4.5, 612, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=900&q=85&auto=format&fit=crop"),
            new CatalogItem("The Atlas of Modern Design", "Books", "Penfold", 1299, 1599, 15, 4.8, 233, "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=900&q=85&auto=format&fit=crop"),
            new CatalogItem("Wireless Mechanical Keyboard", "Electronics", "KeyForge", 5999, 8499, 18, 4.7, 902, "https://images.unsplash.com/photo-1541140532154-b024d705b90?w=900&q=85&auto=format&fit=crop")
        );

        for (CatalogItem item : catalog) {
            Category category = categories.findByName(item.category())
                    .orElseGet(() -> categories.save(new Category(item.category(), item.category() + " essentials")));
            Product product = new Product(item.name(), "A curated, quality product with fast delivery and easy returns.",
                    item.price(), item.imageUrl(), item.stock(), category, null);
            product.setOriginalPrice(item.originalPrice());
            product.setBrand(item.brand());
            product.setRating(item.rating());
            product.setReviewCount(item.reviewCount());
            product.setWarranty("1 year");
            product.setReturnPolicy("30-day return");
            product.setApprovalStatus(ApprovalStatus.APPROVED);
            products.save(product);
        }
    }

    private record CatalogItem(String name, String category, String brand, double price, double originalPrice,
                               int stock, double rating, int reviewCount, String imageUrl) { }
}
