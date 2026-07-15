import { Link } from 'react-router-dom';
import { Heart } from 'lucide-react';
import { Product } from '@/types';
import { Rating } from './Rating';
import { discountPct, formatINR } from '@/utils/format';
import { useCart } from '@/context/CartContext';
import { useWishlist } from '@/context/WishlistContext';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';

const badgeStyles: Record<string, string> = {
  'best-seller': 'bg-warning text-warning-foreground',
  'trending':    'bg-gradient-hero text-primary-foreground',
  'new':         'bg-success text-success-foreground',
  'deal':        'bg-discount text-white',
};

export function ProductCard({ product }: { product: Product }) {
  const { add } = useCart();
  const { toggle, has } = useWishlist();
  const off = discountPct(product.price, product.originalPrice);
  const wished = has(product.id);

  // Helper to get seller name from seller object or string
  const getSellerName = () => {
    if (!product.seller) return null;
    if (typeof product.seller === 'string') return product.seller;
    return (product.seller as any)?.username || null;
  };

  const sellerName = getSellerName();

  return (
    <div className="group relative flex flex-col rounded-2xl border bg-card overflow-hidden card-hover">
      <Link to={`/products/${product.id}`} className="relative aspect-square overflow-hidden bg-muted">
        <img
          src={product.image}
          alt={product.name}
          loading="lazy"
          className="h-full w-full object-cover transition-transform duration-500 group-hover:scale-105"
        />
        {product.badge && (
          <Badge className={cn('absolute left-3 top-3 capitalize border-0', badgeStyles[product.badge])}>
            {product.badge.replace('-', ' ')}
          </Badge>
        )}
        {off > 0 && (
          <Badge className="absolute right-3 top-3 border-0 bg-discount text-white">-{off}%</Badge>
        )}
        {sellerName && (
          <Badge className="absolute left-3 bottom-3 border-0 bg-primary/90 text-primary-foreground text-xs">
            👤 {sellerName}
          </Badge>
        )}
        <button
          onClick={(e) => { e.preventDefault(); toggle(product); }}
          className="absolute bottom-3 right-3 grid h-9 w-9 place-items-center rounded-full bg-card/90 backdrop-blur shadow-md transition-all hover:scale-110"
          aria-label="Add to wishlist"
        >
          <Heart size={16} className={cn('transition-colors', wished ? 'fill-discount text-discount' : 'text-muted-foreground')} />
        </button>
      </Link>

      <div className="flex flex-1 flex-col gap-2 p-4">
        <div className="text-xs uppercase tracking-wide text-muted-foreground">{product.brand}</div>
        <Link to={`/products/${product.id}`} className="line-clamp-2 text-sm font-medium leading-snug hover:text-primary">
          {product.name}
        </Link>
        {sellerName && (
          <div className="text-xs text-muted-foreground">
            Sold by: <span className="font-medium text-primary">{sellerName}</span>
          </div>
        )}
        <Rating value={product.rating} showValue />
        <div className="mt-auto pt-2 flex items-baseline gap-2">
          <span className="text-lg font-semibold">{formatINR(product.price)}</span>
          {product.originalPrice && (
            <span className="text-xs text-muted-foreground line-through">{formatINR(product.originalPrice)}</span>
          )}
        </div>
        <Button size="sm" className="mt-2 w-full" onClick={() => add(product)}>Add to Cart</Button>
      </div>
    </div>
  );
}
