import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { FiHeart, FiShoppingCart, FiStar } from 'react-icons/fi';
import { Product } from '@/types';
import { formatINR } from '@/utils/format';
import { useCart } from '@/context/CartContext';
import { useWishlist } from '@/context/WishlistContext';

const badgeStyles: Record<string, string> = {
  'best-seller': 'bg-warning text-warning-foreground',
  trending: 'bg-gradient-hero text-primary-foreground',
  new: 'bg-success text-success-foreground',
  deal: 'bg-discount text-white',
};

export function ProductTile({ product }: { product: Product }) {
  const { add } = useCart();
  const { toggle, has } = useWishlist();
  const wished = has(product.id);

  return (
    <motion.article
      whileHover={{ y: -6 }}
      className="group overflow-hidden rounded-[2rem] border border-white/10 bg-white/10 shadow-[0_28px_70px_-40px_rgba(15,23,42,0.35)] backdrop-blur-xl transition-all duration-300"
    >
      <Link to={`/products/${product.id}`} className="relative block overflow-hidden rounded-[2rem] bg-muted">
        <img
          loading="lazy"
          src={product.image}
          alt={product.name}
          className="h-64 w-full object-cover transition-transform duration-500 group-hover:scale-105"
        />
        {product.badge && (
          <span className={`absolute left-4 top-4 inline-flex rounded-full px-3 py-1 text-xs uppercase tracking-[0.25em] ${badgeStyles[product.badge]}`}>
            {product.badge.replace('-', ' ')}
          </span>
        )}
      </Link>
      <div className="space-y-4 p-5">
        <div className="flex items-center justify-between gap-4">
          <div>
            <h3 className="text-base font-semibold leading-tight text-foreground">{product.name}</h3>
            <p className="text-xs uppercase tracking-[0.25em] text-muted-foreground">{product.brand}</p>
          </div>
          <button
            type="button"
            onClick={() => toggle(product)}
            aria-label="Add to wishlist"
            className="grid h-10 w-10 place-items-center rounded-2xl border border-white/10 bg-white/20 text-muted-foreground transition hover:border-white/20 hover:text-foreground"
          >
            <FiHeart className={`${wished ? 'fill-discount text-discount' : ''} h-5 w-5`} />
          </button>
        </div>
        <div className="flex items-center gap-2 text-sm text-muted-foreground">
          <FiStar className="h-4 w-4 text-warning" />
          <span>{product.rating.toFixed(1)}</span>
          <span>·</span>
          <span>{product.reviewCount} reviews</span>
        </div>
        <div className="flex items-end justify-between gap-4">
          <div>
            <p className="text-xl font-semibold text-foreground">{formatINR(product.price)}</p>
            {product.originalPrice && <p className="text-sm line-through text-muted-foreground">{formatINR(product.originalPrice)}</p>}
          </div>
          <button
            type="button"
            onClick={() => add(product)}
            className="inline-flex items-center gap-2 rounded-2xl bg-foreground px-4 py-2 text-sm font-semibold text-white transition hover:bg-primary"
          >
            <FiShoppingCart className="h-4 w-4" /> Add
          </button>
        </div>
      </div>
    </motion.article>
  );
}
