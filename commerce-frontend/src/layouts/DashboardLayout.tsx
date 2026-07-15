import { ReactNode } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { Heart, KeyRound, LogOut, MapPin, Package, Settings, User } from 'lucide-react';
import { Navbar } from '@/components/Navbar';
import { Footer } from '@/components/Footer';
import { MobileBottomNav } from '@/components/MobileBottomNav';
import { cn } from '@/lib/utils';
import { useAuth } from '@/context/AuthContext';
import { toast } from 'sonner';

const links = [
  { to: '/profile', icon: User, label: 'Profile' },
  { to: '/orders', icon: Package, label: 'My Orders' },
  { to: '/wishlist', icon: Heart, label: 'Wishlist' },
  { to: '/addresses', icon: MapPin, label: 'Addresses' },
  { to: '/change-password', icon: KeyRound, label: 'Password' },
  { to: '/settings', icon: Settings, label: 'Settings' },
];

export function DashboardLayout({ children }: { children: ReactNode }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    toast.success('Signed out successfully');
    navigate('/');
  };

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Navbar />
      <div className="container-x py-8 flex-1 grid lg:grid-cols-[260px_1fr] gap-8">
        <aside className="hidden lg:block">
          <div className="rounded-2xl border bg-card p-5 sticky top-24">
            <div className="flex items-center gap-3 pb-5 border-b">
              <div className="grid h-12 w-12 place-items-center rounded-full bg-gradient-hero text-primary-foreground font-bold text-lg shrink-0">
                {user?.username?.charAt(0).toUpperCase() ?? '?'}
              </div>
              <div className="min-w-0">
                <div className="font-medium truncate">{user?.username ?? 'Guest'}</div>
                <div className="text-xs text-muted-foreground truncate">
                  {user?.role === 'ROLE_ADMIN' ? '🔑 Administrator' : '👤 Customer'}
                </div>
              </div>
            </div>
            <nav className="mt-4 flex flex-col gap-1">
              {links.map(l => (
                <NavLink key={l.to} to={l.to} end
                  className={({ isActive }) => cn(
                    'flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors',
                    isActive ? 'bg-accent text-accent-foreground' : 'text-muted-foreground hover:bg-accent/50 hover:text-foreground'
                  )}>
                  <l.icon size={16} />{l.label}
                </NavLink>
              ))}
            </nav>
            <div className="mt-4 pt-4 border-t">
              <button
                onClick={handleLogout}
                className="flex w-full items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium text-destructive hover:bg-destructive/10 transition-colors"
              >
                <LogOut size={16} /> Sign out
              </button>
            </div>
          </div>
        </aside>
        <div className="min-w-0 pb-20 lg:pb-0">{children}</div>
      </div>
      <Footer />
      <MobileBottomNav />
    </div>
  );
}
