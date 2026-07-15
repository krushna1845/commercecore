import { useState } from 'react';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useAuth } from '@/context/AuthContext';
import { toast } from 'sonner';
import { ShieldCheck, Store, User } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function Profile() {
  const { user, isSeller, becomeSeller } = useAuth();
  const navigate = useNavigate();
  const [saved, setSaved] = useState(false);

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    setSaved(true);
    toast.success('Profile updated');
    setTimeout(() => setSaved(false), 2000);
  };

  const initials = user?.username?.slice(0, 2).toUpperCase() ?? '??';
  const isAdmin = user?.role === 'ROLE_ADMIN';

  const handleBecomeSeller = async () => {
    try {
      await becomeSeller();
      toast.success('Welcome to the Seller Program!');
      navigate('/seller');
    } catch (e) {
      toast.error(e instanceof Error ? e.message : 'Failed to upgrade');
    }
  };

  return (
    <DashboardLayout>
      <h1 className="font-display text-2xl font-semibold">Profile</h1>
      <p className="text-sm text-muted-foreground mt-1">Manage your account information.</p>

      <div className="mt-6 rounded-2xl border bg-card p-6">
        {/* Avatar */}
        <div className="flex items-center gap-4 pb-6 border-b">
          <div className={`grid h-16 w-16 place-items-center rounded-full text-2xl font-bold text-white shrink-0 ${isAdmin ? 'bg-gradient-to-br from-violet-500 to-purple-700' : 'bg-gradient-hero'}`}>
            {initials}
          </div>
          <div>
            <div className="font-medium text-lg">{user?.username}</div>
            <div className={`text-sm flex items-center gap-1 mt-0.5 ${isAdmin ? 'text-violet-600' : isSeller ? 'text-emerald-600' : 'text-muted-foreground'}`}>
              {isAdmin ? <><ShieldCheck size={14} /> Administrator</> : isSeller ? <><Store size={14} /> Seller</> : <><User size={14} /> Customer</>}
            </div>
          </div>
        </div>

        {/* Account Info */}
        <form onSubmit={handleSave} className="mt-6 space-y-4">
          <div className="grid sm:grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <Label>Username</Label>
              <Input defaultValue={user?.username} disabled className="bg-muted/50" />
              <p className="text-xs text-muted-foreground">Username cannot be changed.</p>
            </div>
            <div className="space-y-1.5">
              <Label>Role</Label>
              <Input value={isAdmin ? 'Administrator' : isSeller ? 'Seller' : 'Customer'} disabled className="bg-muted/50" />
            </div>
          </div>
          <div className="flex justify-end gap-2 pt-2">
            <Button type="submit" disabled={saved}>
              {saved ? 'Saved ✓' : 'Save changes'}
            </Button>
          </div>
        </form>

        {!isSeller && !isAdmin && (
          <div className="mt-6 p-4 rounded-xl border border-emerald-200 dark:border-emerald-800 bg-emerald-50 dark:bg-emerald-950/20">
            <div className="flex items-center justify-between gap-4">
              <div>
                <div className="font-medium flex items-center gap-2"><Store size={16} className="text-emerald-600" /> Start Selling</div>
                <p className="text-sm text-muted-foreground mt-1">List products and manage orders on your own seller dashboard.</p>
              </div>
              <Button onClick={handleBecomeSeller} className="shrink-0">Become a Seller</Button>
            </div>
          </div>
        )}
      </div>

      {/* Quick links */}
      <div className="mt-6 grid sm:grid-cols-3 gap-4">
        {[
          { label: 'My Orders', desc: 'Track and manage orders', href: '/orders' },
          { label: 'Addresses', desc: 'Manage delivery addresses', href: '/addresses' },
          { label: 'Change Password', desc: 'Keep your account secure', href: '/change-password' },
        ].map(item => (
          <a key={item.href} href={item.href} className="rounded-xl border bg-card p-4 hover:shadow-md transition-shadow group">
            <div className="font-medium group-hover:text-primary transition-colors">{item.label}</div>
            <div className="text-xs text-muted-foreground mt-1">{item.desc}</div>
          </a>
        ))}
      </div>
    </DashboardLayout>
  );
}
