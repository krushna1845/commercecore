import { NavLink } from 'react-router-dom';
import { Home, Search, ShoppingBag, Heart, User } from 'lucide-react';
import { useCart } from '@/context/CartContext';
import { cn } from '@/lib/utils';

const items = [
  { to: '/', icon: Home, label: 'Home' },
  { to: '/products', icon: Search, label: 'Shop' },
  { to: '/cart', icon: ShoppingBag, label: 'Cart' },
  { to: '/wishlist', icon: Heart, label: 'Wishlist' },
  { to: '/profile', icon: User, label: 'Account' },
];

export function MobileBottomNav() {
  const { count } = useCart();
  return (
    <nav className="fixed bottom-0 inset-x-0 z-30 lg:hidden border-t bg-background/95 backdrop-blur-lg">
      <ul className="grid grid-cols-5 h-16">
        {items.map(({ to, icon: Icon, label }) => (
          <li key={to}>
            <NavLink to={to} end={to === '/'}
              className={({ isActive }) => cn(
                'relative flex flex-col items-center justify-center gap-0.5 h-full text-[10px] font-medium transition-colors',
                isActive ? 'text-primary' : 'text-muted-foreground'
              )}>
              <Icon size={20} />
              {label === 'Cart' && count > 0 && (
                <span className="absolute top-2 right-1/2 translate-x-3 -translate-y-1 grid place-items-center min-w-4 h-4 px-1 text-[9px] rounded-full bg-primary text-primary-foreground">{count}</span>
              )}
              <span>{label}</span>
            </NavLink>
          </li>
        ))}
      </ul>
    </nav>
  );
}
