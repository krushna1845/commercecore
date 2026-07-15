import { createContext, useContext, useEffect, useMemo, useState, ReactNode } from 'react';
import { CartItem, Product } from '@/types';
import { toast } from 'sonner';
import { useAuth } from '@/context/AuthContext';
import { api } from '@/services/client';
import { productApi } from '@/services/productApi';

interface CartCtx {
  items: CartItem[];
  add: (p: Product, qty?: number) => void;
  remove: (id: string) => void;
  update: (id: string, qty: number) => void;
  clear: () => void;
  count: number;
  subtotal: number;
  loading: boolean;
}

const Ctx = createContext<CartCtx | null>(null);
const KEY = 'shop_cart_v1';

export function CartProvider({ children }: { children: ReactNode }) {
  const { isAuthenticated } = useAuth();
  const [items, setItems] = useState<CartItem[]>(() => {
    try { return JSON.parse(localStorage.getItem(KEY) || '[]'); } catch { return []; }
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => { localStorage.setItem(KEY, JSON.stringify(items)); }, [items]);

  // Sync / Load cart from backend
  useEffect(() => {
    if (!isAuthenticated) return;

    const syncCart = async () => {
      setLoading(true);
      try {
        // 1. Get all products first for mapping
        const catalog = await productApi.list();
        const catalogMap = new Map(catalog.map(p => [String((p as any).id), p]));

        // 2. Fetch backend cart
        const backendCart = await api.get<{ items: any[] }>('/cart');

        if (backendCart.items.length === 0 && items.length > 0) {
          // Push local items to backend if backend cart is empty
          await Promise.all(
            items.map(item =>
              api.post('/cart/add', {
                productId: parseInt(item.product.id),
                quantity: item.quantity,
              })
            )
          );
          // Fetch updated cart
          const updatedCart = await api.get<{ items: any[] }>('/cart');
          const mappedItems = updatedCart.items.map(item => {
            const p = catalogMap.get(String(item.productId));
            return {
              product: p || { id: String(item.productId), name: item.productName, price: item.price } as Product,
              quantity: item.quantity,
            };
          });
          setItems(mappedItems);
        } else {
          // Backend cart takes precedence or replaces
          const mappedItems = backendCart.items.map(item => {
            const p = catalogMap.get(String(item.productId));
            return {
              product: p || { id: String(item.productId), name: item.productName, price: item.price } as Product,
              quantity: item.quantity,
            };
          });
          setItems(mappedItems);
        }
      } catch (err) {
        console.error('Failed to sync cart with backend:', err);
      } finally {
        setLoading(false);
      }
    };

    syncCart();
  }, [isAuthenticated]);

  const add = async (p: Product, qty = 1) => {
    setItems(prev => {
      const i = prev.find(x => x.product.id === p.id);
      if (i) return prev.map(x => x.product.id === p.id ? { ...x, quantity: x.quantity + qty } : x);
      return [...prev, { product: p, quantity: qty }];
    });
    toast.success('Added to cart', { description: p.name });

    if (isAuthenticated) {
      try {
        await api.post('/cart/add', { productId: parseInt(p.id), quantity: qty });
      } catch (err: any) {
        console.error('Failed to add to backend cart:', err);
      }
    }
  };

  const remove = async (id: string) => {
    setItems(prev => prev.filter(x => x.product.id !== id));

    if (isAuthenticated) {
      try {
        await api.del(`/cart/remove/${id}`);
      } catch (err: any) {
        console.error('Failed to remove from backend cart:', err);
      }
    }
  };

  const update = async (id: string, qty: number) => {
    const targetQty = Math.max(1, qty);
    setItems(prev => prev.map(x => x.product.id === id ? { ...x, quantity: targetQty } : x));

    if (isAuthenticated) {
      try {
        await api.put('/cart/update', { productId: parseInt(id), quantity: targetQty });
      } catch (err: any) {
        console.error('Failed to update backend cart:', err);
      }
    }
  };

  const clear = () => setItems([]);

  const value = useMemo(() => ({
    items, add, remove, update, clear, loading,
    count: items.reduce((s, i) => s + i.quantity, 0),
    subtotal: items.reduce((s, i) => s + i.quantity * i.product.price, 0),
  }), [items, loading]);

  return <Ctx.Provider value={value}>{children}</Ctx.Provider>;
}

export const useCart = () => {
  const c = useContext(Ctx);
  if (!c) throw new Error('useCart must be inside CartProvider');
  return c;
};
