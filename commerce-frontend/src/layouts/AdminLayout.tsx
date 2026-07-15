import { ReactNode } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { BarChart3, Box, LayoutDashboard, LogOut, Package, ShieldCheck, ShoppingCart, Tags, Users, Warehouse, FileCheck } from 'lucide-react';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { useAuth } from '@/context/AuthContext';
import { toast } from 'sonner';

const links = [
  { to: '/admin', icon: LayoutDashboard, label: 'Overview', end: true },
  { to: '/admin/products', icon: Package, label: 'Products' },
  { to: '/admin/seller-submissions', icon: FileCheck, label: 'Seller Submissions' },
  { to: '/admin/orders', icon: ShoppingCart, label: 'Orders' },
  { to: '/admin/users', icon: Users, label: 'Users' },
  { to: '/admin/categories', icon: Tags, label: 'Categories' },
  { to: '/admin/inventory', icon: Warehouse, label: 'Inventory' },
  { to: '/admin/analytics', icon: BarChart3, label: 'Analytics' },
];

export function AdminLayout({ children, title, action }: { children: ReactNode; title: string; action?: ReactNode }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    toast.success('Signed out from admin panel');
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-surface flex">
      <aside className="hidden lg:flex w-64 shrink-0 flex-col bg-card border-r">
        <Link to="/admin" className="flex items-center gap-2 px-6 h-16 border-b">
          <div className="grid h-9 w-9 place-items-center rounded-xl bg-gradient-hero text-primary-foreground font-display font-bold">C</div>
          <div>
            <div className="font-display font-semibold">commercecore</div>
            <div className="text-[10px] uppercase tracking-wider text-muted-foreground">Admin Panel</div>
          </div>
        </Link>

        {/* Admin user info */}
        {user && (
          <div className="mx-3 mt-3 mb-1 flex items-center gap-2 rounded-lg bg-violet-500/10 px-3 py-2.5">
            <div className="grid h-8 w-8 place-items-center rounded-full bg-gradient-to-br from-violet-500 to-purple-700 text-white text-xs font-bold shrink-0">
              {user.username.charAt(0).toUpperCase()}
            </div>
            <div className="min-w-0 flex-1">
              <div className="text-sm font-medium truncate">{user.username}</div>
              <div className="text-[10px] text-violet-600 flex items-center gap-1"><ShieldCheck size={10} /> Administrator</div>
            </div>
          </div>
        )}

        <nav className="flex-1 p-3 space-y-1">
          {links.map(l => (
            <NavLink key={l.to} to={l.to} end={l.end}
              className={({ isActive }) => cn(
                'flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium',
                isActive ? 'bg-accent text-accent-foreground' : 'text-muted-foreground hover:bg-accent/50 hover:text-foreground'
              )}>
              <l.icon size={16} />{l.label}
            </NavLink>
          ))}
        </nav>
        <div className="p-3 border-t space-y-2">
          <Link to="/"><Button variant="outline" className="w-full" size="sm">← Back to Store</Button></Link>
          <button
            onClick={handleLogout}
            className="flex w-full items-center justify-center gap-2 px-3 py-2 rounded-lg text-sm font-medium text-destructive hover:bg-destructive/10 transition-colors"
          >
            <LogOut size={14} /> Sign out
          </button>
        </div>
      </aside>

      <div className="flex-1 min-w-0 flex flex-col">
        <header className="h-16 bg-card border-b flex items-center px-4 lg:px-8 gap-4">
          <div className="flex items-center gap-2 lg:hidden">
            <div className="grid h-8 w-8 place-items-center rounded-lg bg-gradient-hero text-primary-foreground font-display font-bold text-sm">C</div>
            <span className="font-display font-semibold">Admin</span>
          </div>
          <h1 className="font-display text-lg font-semibold ml-2">{title}</h1>
          <div className="ml-auto flex items-center gap-2">
            {action}
            {user && (
              <div className="hidden lg:flex items-center gap-1.5 text-xs text-muted-foreground">
                <ShieldCheck size={13} className="text-violet-500" />
                <span>{user.username}</span>
              </div>
            )}
          </div>
        </header>
        <main className="flex-1 p-4 lg:p-8 overflow-x-hidden">{children}</main>

        {/* Mobile admin nav */}
        <nav className="lg:hidden border-t bg-card flex overflow-x-auto no-scrollbar">
          {links.map(l => (
            <NavLink key={l.to} to={l.to} end={l.end}
              className={({ isActive }) => cn(
                'flex flex-col items-center gap-1 px-4 py-2 text-[10px] font-medium shrink-0',
                isActive ? 'text-primary' : 'text-muted-foreground'
              )}>
              <l.icon size={18} />{l.label}
            </NavLink>
          ))}
        </nav>
      </div>
    </div>
  );
}
