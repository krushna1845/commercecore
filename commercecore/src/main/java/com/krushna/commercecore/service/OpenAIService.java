package com.krushna.commercecore.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class OpenAIService {
    private final String apiKey;
    private final String provider;
    private final String endpoint;
    private final String model;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public OpenAIService(@Value("${openai.api.key:}") String openAiApiKey,
                         @Value("${grok.api.key:}") String grokApiKey) {
        if (grokApiKey != null && !grokApiKey.isBlank()) {
            this.apiKey = grokApiKey;
            this.provider = "grok";
            this.endpoint = "https://api.x.ai/v1/chat/completions";
            this.model = "grok-2-latest";
        } else {
            this.apiKey = openAiApiKey;
            this.provider = "openai";
            this.endpoint = "https://api.openai.com/v1/chat/completions";
            this.model = "gpt-4o-mini";
        }
    }

    public String complete(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OpenAI API key is not configured");
        }
        try {
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("model", model);
            payload.put("max_tokens", 450);
            payload.put("temperature", 0.8);
            payload.put("stream", false);
            ArrayNode messages = payload.putArray("messages");
            ObjectNode system = objectMapper.createObjectNode();
            system.put("role", "system");
            system.put("content", "You are a friendly AI shopping assistant for an Indian ecommerce platform. Provide concise advice, product recommendations, and checkout help.");
            messages.add(system);
            ObjectNode user = objectMapper.createObjectNode();
            user.put("role", "user");
            user.put("content", prompt);
            messages.add(user);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 300) {
                throw new IllegalStateException("OpenAI request failed: " + response.body());
            }
            JsonNode root = objectMapper.readTree(response.body());
            return root.path("choices").get(0).path("message").path("content").asText("");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("OpenAI request failed", e);
        }
    }

    public void streamChat(List<String> messages, Consumer<String> deltaConsumer) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OpenAI API key is not configured");
        }
        try {
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("model", model);
            payload.put("temperature", 0.8);
            payload.put("stream", true);
            ArrayNode messageArray = payload.putArray("messages");
            for (String content : messages) {
                ObjectNode msg = objectMapper.createObjectNode();
                msg.put("role", "user");
                msg.put("content", content);
                messageArray.add(msg);
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofMinutes(2))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<java.io.InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() >= 300) {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
                    StringBuilder err = new StringBuilder();
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        err.append(line).append('\n');
                    }
                    throw new IllegalStateException("OpenAI stream failed: " + err);
                }
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) continue;
                    if (line.startsWith("data: ")) {
                        String payloadText = line.substring(6).trim();
                        if (payloadText.equals("[DONE]")) {
                            break;
                        }
                        JsonNode chunk = objectMapper.readTree(payloadText);
                        JsonNode delta = chunk.path("choices").get(0).path("delta").path("content");
                        if (!delta.isMissingNode()) {
                            deltaConsumer.accept(delta.asText());
                        }
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("OpenAI stream failed", e);
        }
    }
}
