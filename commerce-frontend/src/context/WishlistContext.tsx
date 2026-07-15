import { createContext, useContext, useEffect, useMemo, useState, ReactNode } from 'react';
import { Product } from '@/types';
import { toast } from 'sonner';

interface WishCtx {
  ids: string[];
  toggle: (p: Product) => void;
  has: (id: string) => boolean;
  remove: (id: string) => void;
  count: number;
}
const Ctx = createContext<WishCtx | null>(null);
const KEY = 'shop_wish_v1';

export function WishlistProvider({ children }: { children: ReactNode }) {
  const [ids, setIds] = useState<string[]>(() => {
    try { return JSON.parse(localStorage.getItem(KEY) || '[]'); } catch { return []; }
  });
  useEffect(() => { localStorage.setItem(KEY, JSON.stringify(ids)); }, [ids]);

  const toggle = (p: Product) => {
    setIds(prev => {
      if (prev.includes(p.id)) { toast('Removed from wishlist'); return prev.filter(i => i !== p.id); }
      toast.success('Added to wishlist', { description: p.name });
      return [...prev, p.id];
    });
  };
  const remove = (id: string) => setIds(prev => prev.filter(i => i !== id));
  const value = useMemo(() => ({ ids, toggle, has: (id: string) => ids.includes(id), remove, count: ids.length }), [ids]);
  return <Ctx.Provider value={value}>{children}</Ctx.Provider>;
}

export const useWishlist = () => {
  const c = useContext(Ctx);
  if (!c) throw new Error('useWishlist inside provider');
  return c;
};
