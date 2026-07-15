import { api } from './client';
import type { Product } from '@/types';

export const productApi = {
  list:    () => api.get<Product[]>('/products/all'),
  get:     (id: string) => api.get<Product>(`/products/${id}`),
  search:  (q: string) => api.get<Product[]>(`/products/search?name=${encodeURIComponent(q)}`),
  compare: (ids: string[]) => api.get<Product[]>(`/products/compare?ids=${ids.join(',')}`),
};
