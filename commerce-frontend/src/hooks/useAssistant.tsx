import { useCallback, useEffect, useMemo, useState } from 'react';
import { assistantApi, AssistantResponse } from '../services/assistantApi';

const STORAGE_KEY = 'assistant_conversation_id_v1';
const MESSAGE_KEY = 'assistant_messages_v1';

type Role = 'user' | 'assistant';

export interface ChatMessage {
  id: string;
  role: Role;
  text: string;
  cards?: Array<{ id: string | number; name: string; description: string; price: number; imageUrl: string; inStock: boolean }>;
}

export function useAssistant() {
  const [conversationId, setConversationId] = useState<string | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [draft, setDraft] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const existingId = localStorage.getItem(STORAGE_KEY);
    const existingMessages = localStorage.getItem(MESSAGE_KEY);
    if (existingId) setConversationId(existingId);
    if (existingMessages) {
      try { setMessages(JSON.parse(existingMessages)); } catch (e) {}
    }
  }, []);

  const saveState = useCallback((id: string, messageList: ChatMessage[]) => {
    localStorage.setItem(STORAGE_KEY, id);
    localStorage.setItem(MESSAGE_KEY, JSON.stringify(messageList));
  }, []);

  const ensureSession = useCallback(async () => {
    const existingId = conversationId ?? localStorage.getItem(STORAGE_KEY) ?? undefined;
    try {
      const { conversationId: id } = await assistantApi.createSession(existingId);
      setConversationId(id);
      return id;
    } catch {
      const fallbackId = existingId ?? 'fallback-session';
      setConversationId(fallbackId);
      return fallbackId;
    }
  }, [conversationId]);

  const sendMessage = useCallback(async (text: string) => {
    setError(null);
    const message = { id: crypto.randomUUID(), role: 'user' as const, text };
    setMessages(prevMessages => {
      const next = [...prevMessages, message];
      saveState(conversationId ?? localStorage.getItem(STORAGE_KEY) ?? '', next);
      return next;
    });
    setDraft('');
    setIsTyping(true);
    const id = await ensureSession();
    try {
      const response = await assistantApi.chat({ conversationId: id, message: text });
      const assistantMessage = {
        id: crypto.randomUUID(),
        role: 'assistant' as const,
        text: response.text,
        cards: response.cards?.map(card => ({
          id: card.id,
          name: card.name,
          description: card.description,
          price: card.price,
          imageUrl: card.imageUrl,
          inStock: card.inStock,
        })),
      };
      setMessages(prevMessages => {
        const next = [...prevMessages, assistantMessage];
        saveState(id, next);
        return next;
      });
      setConversationId(id);
    } catch (err: any) {
      setError(err.message || 'Failed to send message');
    } finally {
      setIsTyping(false);
    }
  }, [ensureSession, saveState, conversationId]);

  const streamMessage = useCallback(async (text: string) => {
    setError(null);
    const message = { id: crypto.randomUUID(), role: 'user' as const, text };
    setMessages(prevMessages => {
      const next = [...prevMessages, message];
      saveState(conversationId ?? localStorage.getItem(STORAGE_KEY) ?? '', next);
      return next;
    });
    setDraft('');
    setIsTyping(true);
    const id = await ensureSession();
    let accumulated = '';
    const assistantId = crypto.randomUUID();
    setMessages(prevMessages => {
      const next = [...prevMessages, { id: assistantId, role: 'assistant' as const, text: '' }];
      saveState(id, next);
      return next;
    });
    const closeSource = assistantApi.stream(
      { conversationId: id, message: text },
      (delta) => {
        accumulated += delta;
        setMessages(prev => prev.map(msg => msg.id === assistantId ? { ...msg, text: accumulated } : msg));
      },
      () => {
        setMessages(prevMessages => {
          const next = prevMessages.map(msg => msg.id === assistantId ? { ...msg, text: accumulated } : msg);
          saveState(id, next);
          return next;
        });
        setIsTyping(false);
      },
      (reason) => {
        void assistantApi.chat({ conversationId: id, message: text }).then((response) => {
          setMessages(prevMessages => {
            const next = prevMessages.map(msg => msg.id === assistantId ? {
              ...msg,
              text: response.text,
              cards: response.cards?.map(card => ({
                id: card.id,
                name: card.name,
                description: card.description,
                price: card.price,
                imageUrl: card.imageUrl,
                inStock: card.inStock,
              })),
            } : msg);
            saveState(id, next);
            return next;
          });
        }).catch(() => {
          setError(reason);
        }).finally(() => {
          setIsTyping(false);
        });
      }
    );
    return closeSource;
  }, [ensureSession, saveState, conversationId]);

  const resetConversation = useCallback(() => {
    localStorage.removeItem(STORAGE_KEY);
    localStorage.removeItem(MESSAGE_KEY);
    setConversationId(null);
    setMessages([]);
  }, []);

  const canSend = useMemo(() => draft.trim().length > 0 && !isTyping, [draft, isTyping]);

  return {
    conversationId,
    messages,
    draft,
    setDraft,
    isTyping,
    error,
    sendMessage,
    streamMessage,
    resetConversation,
    canSend,
  };
}
