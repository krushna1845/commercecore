package com.krushna.commercecore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.krushna.commercecore.dto.AssistantMessageDTO;

@Component
public class AssistantConversationStore {
    private final Map<String, List<AssistantMessageDTO>> conversations = new ConcurrentHashMap<>();

    public String getOrCreateConversationId(String conversationId) {
        if (conversationId != null && conversations.containsKey(conversationId)) {
            return conversationId;
        }
        String id = UUID.randomUUID().toString();
        conversations.put(id, new ArrayList<>());
        return id;
    }

    public List<AssistantMessageDTO> getHistory(String conversationId) {
        return conversations.getOrDefault(conversationId, Collections.emptyList());
    }

    public void append(String conversationId, AssistantMessageDTO message) {
        conversations.computeIfAbsent(conversationId, id -> new ArrayList<>()).add(message);
    }
}
