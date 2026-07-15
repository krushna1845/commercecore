import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Heart } from 'lucide-react';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { ProductCard } from '@/components/ProductCard';
import { EmptyState } from '@/components/EmptyState';
import { productApi } from '@/services/productApi';
import { useWishlist } from '@/context/WishlistContext';
import { Button } from '@/components/ui/button';
import type { Product } from '@/types';

export default function Wishlist() {
  const { ids } = useWishlist();
  const [productsList, setProductsList] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (ids.length === 0) {
      setProductsList([]);
      setLoading(false);
      return;
    }

    setLoading(true);
    productApi.list()
      .then((items) => {
        setProductsList(
          items.map((p: any) => ({
            id: String(p.id),
            name: p.name,
            brand: p.brand || 'Premium',
            category: p.category?.name || p.category || 'General',
            price: p.price,
            originalPrice: p.originalPrice || p.price * 1.2,
            rating: p.rating || 4.5,
            reviewCount: p.reviewCount || 12,
            image: p.imageUrl || p.image || '',
            images: p.images || [p.imageUrl || p.image || ''],
            description: p.description || '',
            specs: p.specs,
            stock: p.stockQuantity !== undefined ? p.stockQuantity : (p.stock || 0),
          }))
        );
      })
      .catch((err) => {
        console.error('Failed to load wishlist products', err);
        setProductsList([]);
      })
      .finally(() => setLoading(false));
  }, [ids]);

  const list = productsList.filter(p => ids.includes(p.id));

  return (
    <DashboardLayout>
      <div className="flex items-end justify-between mb-6">
        <div>
          <h1 className="font-display text-2xl font-semibold">My Wishlist</h1>
          <p className="text-sm text-muted-foreground mt-1">{list.length} saved items</p>
        </div>
      </div>

      {list.length === 0 ? (
        <EmptyState
          icon={<Heart size={28} />}
          title="Your wishlist is empty"
          description="Tap the heart on any product to save it for later."
          action={{ label: 'Browse products', onClick: () => window.location.assign('/products') }}
        />
      ) : (
        <div className="grid grid-cols-2 md:grid-cols-3 gap-4 lg:gap-6">
          {list.map(p => <ProductCard key={p.id} product={p} />)}
        </div>
      )}
    </DashboardLayout>
  );
}
