package com.krushna.commercecore.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.krushna.commercecore.dto.AssistantMessageDTO;
import com.krushna.commercecore.dto.AssistantRequestDTO;
import com.krushna.commercecore.dto.AssistantResponseDTO;
import com.krushna.commercecore.dto.ProductResponseDTO;
import com.krushna.commercecore.model.Order;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.repository.CouponRepository;
import com.krushna.commercecore.repository.OrderRepository;
import com.krushna.commercecore.repository.ProductRepository;

@Service
public class AssistantService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CouponRepository couponRepository;
    private final OpenAIService openAIService;
    private final AssistantConversationStore conversationStore;

    // Patterns for extracting information from user messages
    private final Pattern orderIdPattern = Pattern.compile("(?:order\\s*#?\\s*)?(\\d{4,12})", Pattern.CASE_INSENSITIVE);
    private final Pattern priceUnderPattern = Pattern.compile("(?:under|below|less than|within)\\s*[rs.]*\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
    private final Pattern priceAbovePattern = Pattern.compile("(?:above|over|more than|greater than)\\s*[rs.]*\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
    private final Pattern priceBetweenPattern = Pattern.compile("[rs.]*\\s*(\\d+)\\s*(?:to|-|and)\\s*[rs.]*\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    public AssistantService(ProductRepository productRepository,
                            OrderRepository orderRepository,
                            CouponRepository couponRepository,
                            OpenAIService openAIService,
                            AssistantConversationStore conversationStore) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.couponRepository = couponRepository;
        this.openAIService = openAIService;
        this.conversationStore = conversationStore;
    }

    /**
     * Regular chat endpoint – returns the full response after processing.
     */
    public AssistantResponseDTO chat(AssistantRequestDTO request) {
        String conversationId = conversationStore.getOrCreateConversationId(request.getConversationId());
        String userMessage = request.getMessage() == null ? "" : request.getMessage().trim();
        conversationStore.append(conversationId, new AssistantMessageDTO("user", userMessage));
        AssistantResponseDTO response = handleMessage(conversationId, userMessage);
        conversationStore.append(conversationId, new AssistantMessageDTO("assistant", response.getText()));
        return response;
    }

    /**
     * Streaming chat endpoint – streams characters to the client via SSE.
     */
    public SseResponse streamChat(AssistantRequestDTO request, Consumer<String> deltaConsumer) {
        String conversationId = conversationStore.getOrCreateConversationId(request.getConversationId());
        String userMessage = request.getMessage() == null ? "" : request.getMessage().trim();
        conversationStore.append(conversationId, new AssistantMessageDTO("user", userMessage));
        AssistantResponseDTO response = handleMessage(conversationId, userMessage);
        for (char c : response.getText().toCharArray()) {
            deltaConsumer.accept(String.valueOf(c));
        }
        conversationStore.append(conversationId, new AssistantMessageDTO("assistant", response.getText()));
        return new SseResponse(conversationId, response.getText(), response.getCards());
    }

    public List<AssistantMessageDTO> getConversation(String conversationId) {
        return conversationStore.getHistory(conversationId);
    }

    private AssistantResponseDTO handleMessage(String conversationId, String message) {
        if (message.isBlank()) {
            return new AssistantResponseDTO(conversationId,
                "Hi! I am your AI shopping assistant. Ask me things like: Show me laptops under 50000, Suggest a birthday gift, Track my order 12345, What coupons are available?",
                List.of(), null);
        }
        String lower = message.toLowerCase(Locale.ROOT);
        Optional<Order> order = parseOrderId(message).flatMap(orderRepository::findById);
        if (order.isPresent() && (lower.contains("track") || lower.contains("order") || lower.contains("status"))) {
            return trackOrderResponse(conversationId, order.get());
        }
        if (lower.contains("coupon") || lower.contains("offer") || lower.contains("discount") || lower.contains("deal") || lower.contains("promo")) {
            return checkoutAssistResponse(conversationId, message);
        }
        if (isComparisonRequest(message)) {
            List<Product> products = findComparisonProducts(message);
            if (!products.isEmpty()) {
                return handleProductComparison(conversationId, message, products);
            }
        }
        if (isAvailabilityRequest(message)) {
            return handleAvailabilityRequest(conversationId, message);
        }
        List<Product> products = searchProducts(message);
        if (!products.isEmpty()) {
            return new AssistantResponseDTO(conversationId,
                buildProductIntro(message, products),
                toProductCards(products),
                null);
        }
        return generalResponse(conversationId, message);
    }

    private List<Product> searchProducts(String message) {
        Double maxPrice = null, minPrice = null;
        Matcher um = priceUnderPattern.matcher(message);
        if (um.find()) maxPrice = Double.parseDouble(um.group(1));
        Matcher am = priceAbovePattern.matcher(message);
        if (am.find()) minPrice = Double.parseDouble(am.group(1));
        Matcher bm = priceBetweenPattern.matcher(message);
        if (bm.find()) {
            double a = Double.parseDouble(bm.group(1));
            double b = Double.parseDouble(bm.group(2));
            minPrice = Math.min(a, b);
            maxPrice = Math.max(a, b);
        }
        List<String> stopWords = List.of(
            "i", "want", "need", "show", "me", "find", "get", "give", "a", "an", "the", "some", "any",
            "good", "best", "top", "cheap", "buy", "looking", "for", "under", "above", "below", "within",
            "budget", "price", "rupees", "rs", "inr", "please", "can", "you", "with", "and", "or", "between", "to", "of", "my",
            "than", "less", "more", "great", "nice", "latest", "new"
        );
        List<String> keywords = new ArrayList<>();
        for (String word : message.toLowerCase().split("[\\s,!?]+")) {
            if (word.length() > 2 && !stopWords.contains(word) && !word.matches("\\d+")) {
                keywords.add(word);
            }
        }
        if (keywords.isEmpty() && maxPrice == null && minPrice == null) return List.of();
        final Double fMax = maxPrice, fMin = minPrice;
        List<Product> all = productRepository.findAll();
        List<Product> filtered = all.stream()
            .filter(p -> fMax == null || p.getPrice() <= fMax)
            .filter(p -> fMin == null || p.getPrice() >= fMin)
            .filter(p -> keywords.isEmpty() || keywords.stream().anyMatch(kw ->
                (p.getName() != null && p.getName().toLowerCase().contains(kw)) ||
                (p.getDescription() != null && p.getDescription().toLowerCase().contains(kw)) ||
                (p.getBrand() != null && p.getBrand().toLowerCase().contains(kw)) ||
                (p.getCategory() != null && p.getCategory().getName() != null && p.getCategory().getName().toLowerCase().contains(kw))
            ))
            .sorted(Comparator.comparingDouble(Product::getPrice))
            .limit(6)
            .collect(Collectors.toList());
        if (filtered.isEmpty() && (fMax != null || fMin != null)) {
            filtered = all.stream()
                .filter(p -> fMax == null || p.getPrice() <= fMax)
                .filter(p -> fMin == null || p.getPrice() >= fMin)
                .sorted(Comparator.comparingDouble(Product::getPrice))
                .limit(6)
                .collect(Collectors.toList());
        }
        return filtered;
    }

    private String buildProductIntro(String message, List<Product> products) {
        String lower = message.toLowerCase(Locale.ROOT);
        if (lower.contains("gift") || lower.contains("recommend")) return "Here are some great gift recommendations for you!";
        if (lower.contains("cheap") || lower.contains("budget") || lower.contains("affordable")) return "Here are the best budget‑friendly options, sorted by price!";
        if (priceUnderPattern.matcher(message).find())
            return "Found " + products.size() + " product" + (products.size() > 1 ? "s" : "") + " within your budget!";
        return "Here are " + products.size() + " product" + (products.size() > 1 ? "s" : "") + " matching your search!";
    }

    private boolean isComparisonRequest(String message) {
        String lower = message.toLowerCase(Locale.ROOT);
        return lower.contains("compare") || lower.contains("vs") || lower.contains("versus");
    }

    private boolean isAvailabilityRequest(String message) {
        String lower = message.toLowerCase(Locale.ROOT);
        return lower.contains("available") || lower.contains("in stock") || lower.contains("stock") || lower.contains("buy now");
    }

    private List<Product> findComparisonProducts(String message) {
        String normalized = message.toLowerCase(Locale.ROOT);
        List<Product> all = productRepository.findAll();
        List<Product> matches = new ArrayList<>();
        for (Product product : all) {
            String name = product.getName() != null ? product.getName().toLowerCase(Locale.ROOT) : "";
            if (name.contains("iphone") && normalized.contains("iphone")) {
                matches.add(product);
            } else if (name.contains("samsung") && normalized.contains("samsung")) {
                matches.add(product);
            } else if (name.contains("oneplus") && normalized.contains("oneplus")) {
                matches.add(product);
            } else if (name.contains("pixel") && normalized.contains("pixel")) {
                matches.add(product);
            } else if (name.contains("galaxy") && normalized.contains("galaxy")) {
                matches.add(product);
            }
        }
        return matches.stream().distinct().limit(2).toList();
    }

    protected AssistantResponseDTO handleProductComparison(String conversationId, String message, List<Product> products) {
        if (products.size() < 2) {
            return new AssistantResponseDTO(conversationId,
                "I can compare them once I find both products in the catalog. Try naming the two products clearly.",
                List.of(), null);
        }
        Product first = products.get(0);
        Product second = products.get(1);
        StringBuilder text = new StringBuilder();
        text.append("Comparison: \n");
        text.append(first.getName()).append(" — ₹").append(String.format(Locale.US, "%,.0f", first.getPrice()));
        text.append(" • Stock: ").append(first.getStockQuantity() > 0 ? "In stock" : "Out of stock");
        text.append("\n");
        text.append(second.getName()).append(" — ₹").append(String.format(Locale.US, "%,.0f", second.getPrice()));
        text.append(" • Stock: ").append(second.getStockQuantity() > 0 ? "In stock" : "Out of stock");
        text.append("\n\nIf you want, I can also help you choose the better value based on price, rating, or features.");
        return new AssistantResponseDTO(conversationId, text.toString(), List.of(), null);
    }

    protected AssistantResponseDTO handleAvailabilityRequest(String conversationId, String message) {
        String lower = message.toLowerCase(Locale.ROOT);
        String rawKeyword = lower.replace("is", "").replace("available", "").replace("in stock", "").replace("stock", "").trim();
        final String keyword = rawKeyword.replaceAll("[^a-z0-9 ]", " ").trim();
        List<Product> matches = productRepository.findAll().stream()
            .filter(product -> product.getName() != null && product.getName().toLowerCase(Locale.ROOT).contains(keyword))
            .limit(3)
            .toList();

        if (!matches.isEmpty()) {
            Product product = matches.get(0);
            String status = product.getStockQuantity() > 0 ? "is available" : "is currently out of stock";
            return new AssistantResponseDTO(conversationId,
                product.getName() + " " + status + " right now. Price: ₹" + String.format(Locale.US, "%,.0f", product.getPrice()),
                List.of(), null);
        }

        return new AssistantResponseDTO(conversationId,
            "That product is not available in our current catalog. If you share a product name or category, I can check the closest match.",
            List.of(), null);
    }

    private Optional<Long> parseOrderId(String message) {
        Matcher m = orderIdPattern.matcher(message);
        if (m.find()) {
            try {
                return Optional.of(Long.parseLong(m.group(1)));
            } catch (NumberFormatException ignored) {}
        }
        return Optional.empty();
    }

    private AssistantResponseDTO trackOrderResponse(String conversationId, Order order) {
        String text = String.format("Order #%d - Status: %s | Total: Rs.%s | Placed: %s",
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt() != null ? order.getCreatedAt().toLocalDate() : "N/A");
        return new AssistantResponseDTO(conversationId, text, List.of(), null);
    }

    private AssistantResponseDTO checkoutAssistResponse(String conversationId, String message) {
        try {
            String aiReply = openAIService.complete(buildPromptContext(conversationId, message));
            return new AssistantResponseDTO(conversationId, aiReply, List.of(), null);
        } catch (Exception ignored) {
            // fall back to keyword based handling
        }
        String lower = message.toLowerCase();
        if (lower.contains("hello") || lower.contains("hi") || lower.contains("hey"))
            return new AssistantResponseDTO(conversationId, "Hello! I can help you find products, track orders, or discover deals. What are you looking for?", List.of(), null);
        if (lower.contains("return") || lower.contains("refund"))
            return new AssistantResponseDTO(conversationId, "Most items can be returned within 10 days. Go to your Orders page and select Return Item. Refunds take 5‑7 business days.", List.of(), null);
        if (lower.contains("shipping") || lower.contains("delivery"))
            return new AssistantResponseDTO(conversationId, "Standard delivery: 5‑7 days (FREE above Rs.499). Express: 2‑3 days. Track your order from the Orders page.", List.of(), null);
        if (lower.contains("payment") || lower.contains("pay"))
            return new AssistantResponseDTO(conversationId, "We accept Credit/Debit Cards, UPI (GPay, PhonePe, Paytm), and Cash on Delivery. Secured by Stripe.", List.of(), null);
        List<Product> popular = productRepository.findAll().stream()
                .filter(p -> p.getStockQuantity() > 0)
                .limit(4)
                .collect(Collectors.toList());
        if (!popular.isEmpty()) {
            return new AssistantResponseDTO(conversationId,
                "Here are some popular items. Try searching for specific products or ask within a budget!",
                toProductCards(popular),
                null);
        }
        return new AssistantResponseDTO(conversationId,
            "Try asking: Show me phones under 15000, I need a birthday gift, or What coupons are available?",
            List.of(),
            null);
    }

    private String buildPromptContext(String conversationId, String message) {
        StringBuilder p = new StringBuilder("You are an AI shopping assistant for an Indian online store. Be concise. History:\n");
        for (AssistantMessageDTO h : conversationStore.getHistory(conversationId)) {
            p.append(h.getRole()).append(": ").append(h.getContent()).append("\n");
        }
        p.append("User: ").append(message).append("\nAssistant:");
        return p.toString();
    }

    private List<ProductResponseDTO> toProductCards(List<Product> products) {
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private ProductResponseDTO convertToDTO(Product product) {
        var cat = product.getCategory();
        com.krushna.commercecore.dto.CategoryResponseDTO catDTO = cat != null ? new com.krushna.commercecore.dto.CategoryResponseDTO(cat.getId(), cat.getName(), cat.getDescription()) : null;
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.getStockQuantity(),
                product.isInStock(),
                catDTO);
    }

    private AssistantResponseDTO generalResponse(String conversationId, String message) {
        // Simple fallback using OpenAI if available, else a generic answer.
        try {
            String reply = openAIService.complete(buildPromptContext(conversationId, message));
            return new AssistantResponseDTO(conversationId, reply, List.of(), null);
        } catch (Exception e) {
            return new AssistantResponseDTO(conversationId, "I'm here to help with product search, order tracking, and coupons. How can I assist you?", List.of(), null);
        }
    }

    public static class SseResponse {
        private final String conversationId;
        private final String text;
        private final List<ProductResponseDTO> cards;

        public SseResponse(String conversationId, String text, List<ProductResponseDTO> cards) {
            this.conversationId = conversationId;
            this.text = text;
            this.cards = cards;
        }

        public String getConversationId() { return conversationId; }
        public String getText() { return text; }
        public List<ProductResponseDTO> getCards() { return cards; }
    }
}
