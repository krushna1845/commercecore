import { useCallback, useEffect, useRef, useState } from 'react';
import { searchApi, SearchParams } from '../services/searchApi';

export type Product = any;

export function useSearch(initial: Partial<SearchParams> = {}) {
  const [query, setQuery] = useState(initial.q ?? '');
  const [results, setResults] = useState<Product[]>([]);
  const [page, setPage] = useState(initial.page ?? 0);
  const [size] = useState(initial.size ?? 20);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [suggestions, setSuggestions] = useState<any>(null);
  const [filters, setFilters] = useState<Partial<SearchParams>>(initial);
  const [error, setError] = useState<string | null>(null);

  const debounceMs = 250;
  const debounceRef = useRef<number | null>(null);
  const abortRef = useRef<AbortController | null>(null);

  const fetchPage = useCallback(async (p = 0, append = false) => {
    setLoading(true);
    setError(null);
    abortRef.current?.abort();
    abortRef.current = new AbortController();
    try {
      const params: SearchParams = {
        q: query,
        category: filters.category ?? null,
        brand: filters.brand ?? null,
        minPrice: filters.minPrice ?? null,
        maxPrice: filters.maxPrice ?? null,
        page: p,
        size,
        sort: filters.sort ?? undefined,
      };

      const res = await searchApi.search(params as any);
      // res: { products, total, page, size }
      if (append) setResults(prev => [...prev, ...(res.products ?? [])]);
      else setResults(res.products ?? []);
      setTotal(res.total ?? 0);
      setPage(res.page ?? p);
    } catch (e: any) {
      if (e.name !== 'AbortError') setError(e.message || String(e));
    } finally {
      setLoading(false);
    }
  }, [query, filters, size]);

  const searchDebounced = useCallback(() => {
    window.clearTimeout(debounceRef.current ?? 0);
    debounceRef.current = window.setTimeout(() => fetchPage(0, false), debounceMs);
  }, [fetchPage]);

  useEffect(() => {
    searchDebounced();
    // record suggestion fetch
    let mounted = true;
    (async () => {
      try {
        const s = await searchApi.suggestions(query);
        if (mounted) setSuggestions(s);
      } catch (e) {}
    })();
    return () => { mounted = false; };
  }, [query, filters, searchDebounced]);

  const loadMore = useCallback(async () => {
    const next = page + 1;
    const pages = Math.ceil((total ?? 0) / size);
    if (next >= pages) return;
    await fetchPage(next, true);
  }, [page, total, size, fetchPage]);

  const applyFilters = useCallback((f: Partial<SearchParams>) => {
    setFilters(prev => ({...prev, ...f}));
    setPage(0);
  }, []);

  const clear = useCallback(() => {
    setQuery('');
    setFilters({});
    setResults([]);
    setTotal(0);
    setPage(0);
  }, []);

  return {
    query, setQuery,
    results, loading, error, total, page, size,
    suggestions, fetchPage, loadMore, applyFilters, clear
  } as const;
}
