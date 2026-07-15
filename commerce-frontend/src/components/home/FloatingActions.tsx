import { Link } from 'react-router-dom';
import { FiHeart, FiShoppingCart } from 'react-icons/fi';
import { useCart } from '@/context/CartContext';
import { useWishlist } from '@/context/WishlistContext';

export function FloatingActions() {
  const { count } = useCart();
  const { count: wishCount } = useWishlist();

  return (
    <div className="pointer-events-none fixed bottom-6 right-6 z-50 flex flex-col items-end gap-3 sm:bottom-8 sm:right-8">
      <Link to="/wishlist" className="pointer-events-auto inline-flex items-center gap-3 rounded-full border border-white/10 bg-white/10 px-4 py-3 text-sm font-semibold text-foreground shadow-[0_24px_60px_-40px_rgba(15,23,42,0.35)] backdrop-blur-xl transition hover:bg-white/20">
        <FiHeart className="h-5 w-5 text-foreground" />
        Wishlist
        {wishCount > 0 && <span className="rounded-full bg-primary px-2 py-0.5 text-[11px] font-semibold text-primary-foreground">{wishCount}</span>}
      </Link>
      <Link to="/cart" className="pointer-events-auto inline-flex items-center gap-3 rounded-full bg-foreground px-4 py-3 text-sm font-semibold text-white shadow-lg shadow-foreground/20 transition hover:bg-primary">
        <FiShoppingCart className="h-5 w-5" />
        Cart
        {count > 0 && <span className="rounded-full bg-white px-2 py-0.5 text-[11px] font-semibold text-foreground">{count}</span>}
      </Link>
    </div>
  );
}
