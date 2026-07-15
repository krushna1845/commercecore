import { useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { Heart, LogIn, LogOut, Menu, Moon, Search, Settings, ShieldCheck, ShoppingBag, Store, Sun, User, X } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { useCart } from '@/context/CartContext';
import { useWishlist } from '@/context/WishlistContext';
import { useTheme } from '@/context/ThemeContext';
import { useAuth } from '@/context/AuthContext';
import { CartDrawer } from './CartDrawer';
import { categories } from '@/data/categories';
import { cn } from '@/lib/utils';
import { toast } from 'sonner';

export function Navbar() {
  const [cartOpen, setCartOpen] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);
  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const [q, setQ] = useState('');
  const { count } = useCart();
  const { count: wishCount } = useWishlist();
  const { theme, toggle } = useTheme();
  const { user, isAuthenticated, isAdmin, isSeller, becomeSeller, logout } = useAuth();
  const navigate = useNavigate();

  const onSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (q.trim()) navigate(`/products?q=${encodeURIComponent(q.trim())}`);
  };

  const handleLogout = () => {
    logout();
    setUserMenuOpen(false);
    setMobileOpen(false);
    toast.success('Signed out successfully');
    navigate('/');
  };

  const handleBecomeSeller = async () => {
    try {
      await becomeSeller();
      toast.success('You are now a seller!');
      setUserMenuOpen(false);
      navigate('/seller');
    } catch (e) {
      toast.error(e instanceof Error ? e.message : 'Failed to upgrade account');
    }
  };

  const nav = [
    { to: '/', label: 'Home' },
    { to: '/products', label: 'Shop' },
    { to: '/assistant', label: 'Assistant' },
    { to: '/orders', label: 'Orders' },
    ...(isSeller ? [{ to: '/seller', label: 'Seller' }] : []),
    ...(isAdmin ? [{ to: '/admin', label: 'Admin' }] : []),
  ];

  return (
    <>
      <header className="sticky top-0 z-40 w-full border-b bg-background backdrop-blur" >
        <div className="container-x flex h-16 items-center gap-3">
          <button className="lg:hidden p-2 -ml-2" onClick={() => setMobileOpen(true)} aria-label="Open menu">
            <Menu size={20} />
          </button>

          <Link to="/" className="flex items-center gap-2 shrink-0">
            <div className="grid h-9 w-9 place-items-center rounded-xl bg-gradient-hero text-primary-foreground font-display font-bold">C</div>
            <span className="font-display text-lg font-semibold tracking-tight hidden sm:block">commercecore</span>
          </Link>

          <nav className="ml-4 hidden lg:flex items-center gap-1">
            {nav.map(n => (
              <NavLink key={n.to} to={n.to} end={n.to === '/'}
                className={({ isActive }) => cn(
                  'px-3 py-2 text-sm font-medium rounded-md transition-colors',
                  isActive ? 'text-foreground bg-accent' : 'text-muted-foreground hover:text-foreground'
                )}>
                {n.label}
              </NavLink>
            ))}
          </nav>

          <form onSubmit={onSearch} className="ml-auto flex-1 max-w-xl hidden md:block">
            <div className="relative">
              <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
              <Input value={q} onChange={(e) => setQ(e.target.value)}
                placeholder="Search products, brands, categories..."
                className="pl-9 bg-muted/50 border-transparent focus-visible:bg-background" />
            </div>
          </form>

          <div className="ml-auto md:ml-3 flex items-center gap-1">
            <Button variant="ghost" size="icon" onClick={toggle} aria-label="Toggle theme">
              {theme === 'light' ? <Moon size={18} /> : <Sun size={18} />}
            </Button>

            {isAuthenticated ? (
              <>
                <Link to="/wishlist" className="relative">
                  <Button variant="ghost" size="icon" aria-label="Wishlist"><Heart size={18} /></Button>
                  {wishCount > 0 && <Badge className="absolute -top-1 -right-1 h-4 min-w-4 px-1 text-[10px] bg-discount text-white border-0">{wishCount}</Badge>}
                </Link>

                {/* User menu */}
                <div className="relative">
                  <button
                    onClick={() => setUserMenuOpen(o => !o)}
                    className="flex items-center gap-2 px-2 py-1.5 rounded-lg hover:bg-accent transition-colors"
                  >
                    <div className={cn(
                      'grid h-8 w-8 place-items-center rounded-full text-xs font-bold text-white',
                      isAdmin ? 'bg-gradient-to-br from-violet-500 to-purple-700' : 'bg-gradient-hero'
                    )}>
                      {user?.username?.charAt(0).toUpperCase()}
                    </div>
                    <span className="hidden lg:block text-sm font-medium max-w-[90px] truncate">{user?.username}</span>
                    {isAdmin && <ShieldCheck size={14} className="text-violet-500 hidden lg:block" />}
                  </button>

                  {userMenuOpen && (
                    <>
                      <div className="fixed inset-0 z-40" onClick={() => setUserMenuOpen(false)} />
                      <div className="absolute right-0 top-full mt-2 w-52 rounded-xl border bg-card shadow-xl z-50 py-1 animate-in fade-in-0 zoom-in-95">
                        <div className="px-3 py-2 border-b mb-1">
                          <div className="text-sm font-medium truncate">{user?.username}</div>
                          <div className={cn('text-xs mt-0.5', isAdmin ? 'text-violet-500' : isSeller ? 'text-emerald-600' : 'text-muted-foreground')}>
                            {isAdmin ? '🔑 Administrator' : isSeller ? '🏪 Seller' : '👤 Customer'}
                          </div>
                        </div>
                        {isSeller && (
                          <Link to="/seller" onClick={() => setUserMenuOpen(false)}
                            className="flex items-center gap-2 px-3 py-2 text-sm hover:bg-accent">
                            <Store size={14} className="text-emerald-600" /> Seller Dashboard
                          </Link>
                        )}
                        {!isSeller && !isAdmin && (
                          <button onClick={handleBecomeSeller}
                            className="flex w-full items-center gap-2 px-3 py-2 text-sm hover:bg-accent text-emerald-600">
                            <Store size={14} /> Become a Seller
                          </button>
                        )}
                        {isAdmin && (
                          <Link to="/admin" onClick={() => setUserMenuOpen(false)}
                            className="flex items-center gap-2 px-3 py-2 text-sm hover:bg-accent">
                            <ShieldCheck size={14} className="text-violet-500" /> Admin Panel
                          </Link>
                        )}
                        <Link to="/profile" onClick={() => setUserMenuOpen(false)}
                          className="flex items-center gap-2 px-3 py-2 text-sm hover:bg-accent">
                          <User size={14} /> My Profile
                        </Link>
                        <Link to="/orders" onClick={() => setUserMenuOpen(false)}
                          className="flex items-center gap-2 px-3 py-2 text-sm hover:bg-accent">
                          <ShoppingBag size={14} /> My Orders
                        </Link>
                        <Link to="/settings" onClick={() => setUserMenuOpen(false)}
                          className="flex items-center gap-2 px-3 py-2 text-sm hover:bg-accent">
                          <Settings size={14} /> Settings
                        </Link>
                        <div className="border-t mt-1 pt-1">
                          <button onClick={handleLogout}
                            className="flex w-full items-center gap-2 px-3 py-2 text-sm text-destructive hover:bg-destructive/10">
                            <LogOut size={14} /> Sign out
                          </button>
                        </div>
                      </div>
                    </>
                  )}
                </div>
              </>
            ) : (
              <Link to="/login">
                <Button size="sm" className="gap-2">
                  <LogIn size={15} /> Sign in
                </Button>
              </Link>
            )}

            <button onClick={() => setCartOpen(true)} className="relative">
              <Button variant="ghost" size="icon" aria-label="Cart" asChild><span><ShoppingBag size={18} /></span></Button>
              {count > 0 && <Badge className="absolute -top-1 -right-1 h-4 min-w-4 px-1 text-[10px] border-0">{count}</Badge>}
            </button>
          </div>
        </div>

        {/* Category strip */}
        <div className="border-t hidden lg:block">
          <div className="container-x flex items-center gap-1 h-11 overflow-x-auto no-scrollbar">
            {categories.map(c => (
              <Link key={c.id} to={`/products?category=${encodeURIComponent(c.name)}`}
                className="text-xs font-medium px-3 py-1.5 rounded-full text-muted-foreground hover:text-foreground hover:bg-accent transition-colors whitespace-nowrap">
                <span className="mr-1.5">{c.icon}</span>{c.name}
              </Link>
            ))}
          </div>
        </div>

        {/* Mobile search */}
        <form onSubmit={onSearch} className="md:hidden border-t px-4 py-2">
          <div className="relative">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
            <Input value={q} onChange={(e) => setQ(e.target.value)} placeholder="Search products..." className="pl-9 bg-muted/50 border-transparent" />
          </div>
        </form>
      </header>

      {/* Mobile drawer */}
      {mobileOpen && (
        <div className="fixed inset-0 z-50 lg:hidden">
          <div className="absolute inset-0 bg-foreground/40" onClick={() => setMobileOpen(false)} />
          <div className="absolute left-0 top-0 h-full w-72 bg-background shadow-xl p-5 animate-slide-in-right overflow-y-auto">
            <div className="flex items-center justify-between mb-6">
              <span className="font-display text-lg font-semibold">Menu</span>
              <button onClick={() => setMobileOpen(false)}><X size={20} /></button>
            </div>

            {isAuthenticated ? (
              <div className="flex items-center gap-3 p-3 rounded-xl bg-accent mb-4">
                <div className={cn(
                  'grid h-10 w-10 place-items-center rounded-full text-sm font-bold text-white shrink-0',
                  isAdmin ? 'bg-gradient-to-br from-violet-500 to-purple-700' : 'bg-gradient-hero'
                )}>
                  {user?.username?.charAt(0).toUpperCase()}
                </div>
                <div className="min-w-0">
                  <div className="font-medium text-sm truncate">{user?.username}</div>
                  <div className={cn('text-xs', isAdmin ? 'text-violet-600' : 'text-muted-foreground')}>
                    {isAdmin ? '🔑 Administrator' : '👤 Customer'}
                  </div>
                </div>
              </div>
            ) : (
              <Link to="/login" onClick={() => setMobileOpen(false)}>
                <Button className="w-full mb-4 gap-2"><LogIn size={15} /> Sign in</Button>
              </Link>
            )}

            <nav className="flex flex-col gap-1">
              {nav.map(n => (
                <NavLink key={n.to} to={n.to} end={n.to === '/'} onClick={() => setMobileOpen(false)}
                  className={({ isActive }) => cn('px-3 py-2.5 rounded-md text-sm font-medium', isActive ? 'bg-accent text-foreground' : 'text-muted-foreground')}>
                  {n.label}
                </NavLink>
              ))}
            </nav>

            {isAuthenticated && (
              <>
                <div className="mt-4 pt-4 border-t">
                  <div className="text-xs uppercase tracking-wide text-muted-foreground mb-2">Account</div>
                  {[
                    { to: '/profile', label: 'My Profile' },
                    { to: '/orders', label: 'My Orders' },
                    { to: '/wishlist', label: 'Wishlist' },
                    { to: '/settings', label: 'Settings' },
                  ].map(l => (
                    <Link key={l.to} to={l.to} onClick={() => setMobileOpen(false)}
                      className="flex items-center px-3 py-2 rounded-md text-sm hover:bg-accent">
                      {l.label}
                    </Link>
                  ))}
                </div>
                <div className="mt-4 pt-4 border-t">
                  <button onClick={handleLogout} className="flex items-center gap-2 px-3 py-2 rounded-md text-sm text-destructive w-full hover:bg-destructive/10">
                    <LogOut size={14} /> Sign out
                  </button>
                </div>
              </>
            )}

            <div className="mt-6 pt-4 border-t text-xs uppercase tracking-wide text-muted-foreground mb-2">Categories</div>
            <div className="flex flex-col gap-1">
              {categories.map(c => (
                <Link key={c.id} to={`/products?category=${encodeURIComponent(c.name)}`} onClick={() => setMobileOpen(false)}
                  className="flex items-center gap-2 px-3 py-2 rounded-md text-sm hover:bg-accent">
                  <span>{c.icon}</span>{c.name}
                </Link>
              ))}
            </div>
          </div>
        </div>
      )}

      <CartDrawer open={cartOpen} onOpenChange={setCartOpen} />
    </>
  );
}
