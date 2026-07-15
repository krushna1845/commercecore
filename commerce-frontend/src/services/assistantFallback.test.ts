import { describe, expect, it } from 'vitest';
import { buildFallbackAssistantResponse } from './assistantFallback';

describe('buildFallbackAssistantResponse', () => {
  it('returns product cards for a shopping question even without backend responses', () => {
    const response = buildFallbackAssistantResponse('I need headphones under 10000');

    expect(response.text).toContain('Here are a few products');
    expect(response.cards.length).toBeGreaterThan(0);
    expect(response.cards[0].name).toContain('Headphones');
    expect(response.cards[0].price).toBeLessThanOrEqual(10000);
  });
});
