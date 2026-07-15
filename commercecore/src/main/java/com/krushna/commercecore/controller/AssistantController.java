package com.krushna.commercecore.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.krushna.commercecore.dto.AssistantMessageDTO;
import com.krushna.commercecore.dto.AssistantRequestDTO;
import com.krushna.commercecore.dto.AssistantResponseDTO;
import com.krushna.commercecore.service.AssistantConversationStore;
import com.krushna.commercecore.service.AssistantService;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {
    private final AssistantService assistantService;
    private final AssistantConversationStore conversationStore;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AssistantController(AssistantService assistantService, AssistantConversationStore conversationStore) {
        this.assistantService = assistantService;
        this.conversationStore = conversationStore;
    }

    @PostMapping("/session")
    public ResponseEntity<Map<String, String>> createSession(@RequestBody(required = false) Map<String, String> body) {
        String requestedId = body != null ? body.get("conversationId") : null;
        String id = conversationStore.getOrCreateConversationId(requestedId);
        return ResponseEntity.ok(Map.of("conversationId", id));
    }

    @PostMapping("/chat")
    public ResponseEntity<AssistantResponseDTO> chat(@RequestBody AssistantRequestDTO request) {
        return ResponseEntity.ok(assistantService.chat(request));
    }

    @PostMapping(path = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestBody AssistantRequestDTO request) {
        return createStreamEmitter(request.getConversationId(), request.getMessage());
    }

    @GetMapping(path = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChatGet(@RequestParam(required = false) String conversationId,
                                    @RequestParam String message) {
        return createStreamEmitter(conversationId, message);
    }

    private SseEmitter createStreamEmitter(String conversationId, String message) {
        SseEmitter emitter = new SseEmitter(0L);
        executor.execute(() -> {
            try {
                assistantService.streamChat(new AssistantRequestDTO() {{
                    setConversationId(conversationId);
                    setMessage(message);
                }}, delta -> {
                    try {
                        emitter.send(SseEmitter.event().name("assistant-message").data(delta));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                emitter.send(SseEmitter.event().name("assistant-done").data(""));
                emitter.complete();
            } catch (Exception ex) {
                try {
                    emitter.send(SseEmitter.event().name("assistant-error").data(ex.getMessage()));
                } catch (IOException ignored) {}
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

    @GetMapping("/history/{conversationId}")
    public ResponseEntity<List<AssistantMessageDTO>> history(@PathVariable String conversationId) {
        return ResponseEntity.ok(conversationStore.getHistory(conversationId));
    }
}
