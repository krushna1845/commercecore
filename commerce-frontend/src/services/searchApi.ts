import { api } from './client';

export type SearchParams = {
  q?: string;
  category?: number | null;
  brand?: string | null;
  minPrice?: number | null;
  maxPrice?: number | null;
  page?: number;
  size?: number;
  sort?: string;
}

export const searchApi = {
  search: async (p: SearchParams) => {
    const qs = new URLSearchParams();
    if (p.q) qs.set('q', p.q);
    if (p.category) qs.set('category', String(p.category));
    if (p.brand) qs.set('brand', String(p.brand));
    if (p.minPrice != null) qs.set('minPrice', String(p.minPrice));
    if (p.maxPrice != null) qs.set('maxPrice', String(p.maxPrice));
    qs.set('page', String(p.page ?? 0));
    qs.set('size', String(p.size ?? 20));
    if (p.sort) qs.set('sort', p.sort);
    return api.get(`/api/search?${qs.toString()}`);
  },
  suggestions: async (q?: string) => api.get(`/api/search/suggestions${q ? `?q=${encodeURIComponent(q)}` : ''}`),
  record: async (q: string) => api.post('/api/search/record', { q }),
  recent: async (limit = 10) => api.get(`/api/search/recent?limit=${limit}`),
  popular: async (limit = 10) => api.get(`/api/search/popular?limit=${limit}`),
}
