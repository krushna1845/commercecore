package com.krushna.commercecore.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.krushna.commercecore.dto.AssistantResponseDTO;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.repository.CouponRepository;
import com.krushna.commercecore.repository.OrderRepository;
import com.krushna.commercecore.repository.ProductRepository;

class AssistantServiceTest {

    @Test
    void compareRequestReturnsComparisonForKnownProducts() {
        ProductRepository productRepository = mock(ProductRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        CouponRepository couponRepository = mock(CouponRepository.class);
        OpenAIService openAIService = mock(OpenAIService.class);
        AssistantConversationStore conversationStore = new AssistantConversationStore();

        AssistantService service = new AssistantService(productRepository, orderRepository, couponRepository, openAIService, conversationStore);

        Product iphone = new Product();
        iphone.setName("iPhone 15");
        iphone.setPrice(79999);
        iphone.setStockQuantity(12);
        iphone.setDescription("A flagship smartphone with great camera performance.");

        Product samsung = new Product();
        samsung.setName("Samsung Galaxy S24");
        samsung.setPrice(74999);
        samsung.setStockQuantity(8);
        samsung.setDescription("Premium smartphone with excellent display and AI features.");

        when(productRepository.findAll()).thenReturn(List.of(iphone, samsung));

        AssistantResponseDTO response = service.handleProductComparison("conv-1", "Compare iPhone 15 and Samsung Galaxy S24", List.of(iphone, samsung));

        assertNotNull(response);
        assertTrue(response.getText().contains("iPhone 15"));
        assertTrue(response.getText().contains("Samsung Galaxy S24"));
        assertTrue(response.getText().contains("₹79,999") || response.getText().contains("79999"));
    }

    @Test
    void availabilityRequestReturnsNotAvailableForUnknownProduct() {
        ProductRepository productRepository = mock(ProductRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        CouponRepository couponRepository = mock(CouponRepository.class);
        OpenAIService openAIService = mock(OpenAIService.class);
        AssistantConversationStore conversationStore = new AssistantConversationStore();

        AssistantService service = new AssistantService(productRepository, orderRepository, couponRepository, openAIService, conversationStore);

        when(productRepository.findAll()).thenReturn(List.of());

        AssistantResponseDTO response = service.handleAvailabilityRequest("conv-2", "Is Pixel 9 available?");

        assertNotNull(response);
        assertTrue(response.getText().contains("not available") || response.getText().contains("not currently available"));
    }
}
