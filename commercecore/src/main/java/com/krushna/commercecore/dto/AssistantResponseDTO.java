package com.krushna.commercecore.dto;

import java.util.List;

public class AssistantResponseDTO {
    private String conversationId;
    private String text;
    private List<ProductResponseDTO> cards;
    private String type;

    public AssistantResponseDTO() {}

    public AssistantResponseDTO(String conversationId, String text, List<ProductResponseDTO> cards, String type) {
        this.conversationId = conversationId;
        this.text = text;
        this.cards = cards;
        this.type = type;
    }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public List<ProductResponseDTO> getCards() { return cards; }
    public void setCards(List<ProductResponseDTO> cards) { this.cards = cards; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
