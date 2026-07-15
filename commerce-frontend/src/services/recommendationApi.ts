import { api } from './client';
import type { RecommendationResponse, MultipleRecommendationsResponse } from '@/types';

export const recommendationApi = {
  // Product-based recommendations
  getFrequentlyBoughtTogether: (productId: string, limit: number = 10) =>
    api.get<RecommendationResponse>(`/recommendations/product/${productId}/frequently-bought-together?limit=${limit}`),
  
  getCustomersAlsoBought: (productId: string, limit: number = 10) =>
    api.get<RecommendationResponse>(`/recommendations/product/${productId}/customers-also-bought?limit=${limit}`),
  
  getAllProductRecommendations: (productId: string, limit: number = 5) =>
    api.get<MultipleRecommendationsResponse>(`/recommendations/product/${productId}/all?limit=${limit}`),
  
  // User-based recommendations
  getRecommendedForYou: (userId: string, limit: number = 10) =>
    api.get<RecommendationResponse>(`/recommendations/user/${userId}/recommended-for-you?limit=${limit}`),
  
  getRecentlyViewed: (userId: string, limit: number = 10) =>
    api.get<RecommendationResponse>(`/recommendations/user/${userId}/recently-viewed?limit=${limit}`),
  
  // Global trending and popular
  getTrendingProducts: (limit: number = 10) =>
    api.get<RecommendationResponse>(`/recommendations/trending?limit=${limit}`),
  
  getPopularProducts: (limit: number = 10) =>
    api.get<RecommendationResponse>(`/recommendations/popular?limit=${limit}`),
  
  // Track user actions
  trackBrowse: (userId: string, productId: string) =>
    api.post(`/recommendations/user/${userId}/track-browse/${productId}`),
};
