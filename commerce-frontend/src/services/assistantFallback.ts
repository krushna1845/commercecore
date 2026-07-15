import { products } from '../data/products';
import type { AssistantCard, AssistantResponse } from './assistantApi';

const normalize = (value: string) => value.toLowerCase();

const pickProducts = (message: string) => {
  const lower = normalize(message);
  const requested = lower.match(/under\s*₹?\s*(\d+)/i)?.[1] || lower.match(/below\s*₹?\s*(\d+)/i)?.[1];
  const maxPrice = requested ? Number(requested) : undefined;

  const filtered = products.filter((product) => {
    const priceOk = maxPrice ? product.price <= maxPrice : true;
    const matchesCategory =
      lower.includes('headphone') || lower.includes('earbud') || lower.includes('audio')
        ? product.category === 'electronics' || product.name.toLowerCase().includes('headphone')
        : true;

    return priceOk && matchesCategory;
  });

  return filtered.slice(0, 3).map((product) => ({
    id: product.id,
    name: product.name,
    description: product.description,
    price: product.price,
    imageUrl: product.image,
    inStock: product.stock > 0,
  })) as AssistantCard[];
};

export const buildFallbackAssistantResponse = (message: string): AssistantResponse => {
  const cards = pickProducts(message);
  const text = cards.length
    ? `Here are a few products that match your request:\n\n${cards.map((card) => `• ${card.name} — ₹${card.price}`).join('\n')}`
    : `I can help you shop right now. Try asking for a category, budget, or product type like “headphones under 10000”.`;

  return {
    conversationId: 'fallback',
    text,
    cards,
    type: 'fallback',
  };
};
