import { BASE_URL } from './client';
import { buildFallbackAssistantResponse } from './assistantFallback';

export type AssistantChatRequest = {
  conversationId?: string;
  message: string;
};

export type AssistantCard = {
  id: number | string;
  name: string;
  description: string;
  price: number;
  imageUrl: string;
  inStock: boolean;
};

export type AssistantResponse = {
  conversationId: string;
  text: string;
  cards: AssistantCard[];
  type: string;
};

export const assistantApi = {
  createSession: async (conversationId?: string) => {
    const response = await fetch(`${BASE_URL}/api/assistant/session`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ conversationId }),
    });
    return response.json() as Promise<{ conversationId: string }>;
  },

  chat: async (payload: AssistantChatRequest) => {
    try {
      const response = await fetch(`${BASE_URL}/api/assistant/chat`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        throw new Error(await response.text());
      }

      return response.json() as Promise<AssistantResponse>;
    } catch (error) {
      console.warn('Assistant backend unavailable, using fallback response.', error);
      return buildFallbackAssistantResponse(payload.message);
    }
  },

  stream: (payload: AssistantChatRequest, onDelta: (delta: string) => void, onDone: () => void, onError: (reason: string) => void) => {
    const params = new URLSearchParams();
    if (payload.conversationId) params.set('conversationId', payload.conversationId);
    params.set('message', payload.message);

    const url = `${BASE_URL}/api/assistant/chat/stream?${params.toString()}`;
    const source = new EventSource(url);

    source.addEventListener('assistant-message', (event: MessageEvent) => {
      onDelta(event.data);
    });
    source.addEventListener('assistant-done', () => {
      onDone();
      source.close();
    });
    source.addEventListener('assistant-error', (event: MessageEvent) => {
      onError(event.data || 'Assistant stream error');
      source.close();
    });

    source.onerror = () => {
      onError('Assistant connection failed');
      source.close();
    };

    return () => {
      source.close();
    };
  },
};
