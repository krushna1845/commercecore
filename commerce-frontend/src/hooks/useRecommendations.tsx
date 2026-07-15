import { useState, useEffect, useCallback } from 'react';
import { recommendationApi } from '@/services/recommendationApi';
import type { RecommendationResponse } from '@/types';

export interface UseRecommendationsOptions {
  limit?: number;
  autoFetch?: boolean;
  cacheTime?: number;
}

export const useRecommendations = (options: UseRecommendationsOptions = {}) => {
  const { limit = 10, autoFetch = true, cacheTime = 3600000 } = options;
  const [data, setData] = useState<RecommendationResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const fetchFrequentlyBoughtTogether = useCallback(async (productId: string) => {
    setLoading(true);
    setError(null);
    try {
      const result = await recommendationApi.getFrequentlyBoughtTogether(productId, limit);
      setData(result);
      return result;
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Failed to fetch recommendations');
      setError(error);
      throw error;
    } finally {
      setLoading(false);
    }
  }, [limit]);

  const fetchCustomersAlsoBought = useCallback(async (productId: string) => {
    setLoading(true);
    setError(null);
    try {
      const result = await recommendationApi.getCustomersAlsoBought(productId, limit);
      setData(result);
      return result;
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Failed to fetch recommendations');
      setError(error);
      throw error;
    } finally {
      setLoading(false);
    }
  }, [limit]);

  const fetchRecommendedForYou = useCallback(async (userId: string) => {
    setLoading(true);
    setError(null);
    try {
      const result = await recommendationApi.getRecommendedForYou(userId, limit);
      setData(result);
      return result;
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Failed to fetch recommendations');
      setError(error);
      throw error;
    } finally {
      setLoading(false);
    }
  }, [limit]);

  const fetchRecentlyViewed = useCallback(async (userId: string) => {
    setLoading(true);
    setError(null);
    try {
      const result = await recommendationApi.getRecentlyViewed(userId, limit);
      setData(result);
      return result;
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Failed to fetch recommendations');
      setError(error);
      throw error;
    } finally {
      setLoading(false);
    }
  }, [limit]);

  const fetchTrendingProducts = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await recommendationApi.getTrendingProducts(limit);
      setData(result);
      return result;
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Failed to fetch trending products');
      setError(error);
      throw error;
    } finally {
      setLoading(false);
    }
  }, [limit]);

  const fetchPopularProducts = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await recommendationApi.getPopularProducts(limit);
      setData(result);
      return result;
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Failed to fetch popular products');
      setError(error);
      throw error;
    } finally {
      setLoading(false);
    }
  }, [limit]);

  const trackBrowse = useCallback(async (userId: string, productId: string) => {
    try {
      await recommendationApi.trackBrowse(userId, productId);
    } catch (err) {
      console.error('Failed to track browse', err);
    }
  }, []);

  return {
    data,
    loading,
    error,
    fetchFrequentlyBoughtTogether,
    fetchCustomersAlsoBought,
    fetchRecommendedForYou,
    fetchRecentlyViewed,
    fetchTrendingProducts,
    fetchPopularProducts,
    trackBrowse,
  };
};
