import { useEffect, useState } from 'react';
import { ArrowDown, ArrowUp, DollarSign, Loader2, Package, ShoppingCart, Users } from 'lucide-react';
import { AdminLayout } from '@/layouts/AdminLayout';
import { Badge } from '@/components/ui/badge';
import { adminApi } from '@/services/adminApi';
import { formatINR } from '@/utils/format';
import { cn } from '@/lib/utils';

interface DashboardData {
  totalRevenue: number;
  totalOrders: number;
  totalProducts: number;
  totalUsers: number;
  recentOrders: Array<{
    id: number;
    username: string;
    status: string;
    totalAmount: number;
    createdAt: string;
    itemCount: number;
  }>;
}

const statusColor: Record<string, string> = {
  PENDING: 'bg-yellow-100 text-yellow-700',
  PAID: 'bg-green-100 text-green-700',
  SHIPPED: 'bg-blue-100 text-blue-700',
  CANCELLED: 'bg-red-100 text-red-700',
  FAILED: 'bg-red-100 text-red-700',
};

const chart = [42, 58, 51, 73, 65, 80, 92, 78, 85, 96, 110, 102];

export default function AdminDashboard() {
  const [data, setData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    adminApi.stats()
      .then((d: any) => setData(d))
      .catch(err => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return (
    <AdminLayout title="Overview">
      <div className="flex items-center justify-center h-64"><Loader2 className="animate-spin text-primary" size={32} /></div>
    </AdminLayout>
  );

  if (error) return (
    <AdminLayout title="Overview">
      <div className="rounded-xl bg-destructive/10 text-destructive p-6 text-sm">{error} — make sure backend is running on port 8081.</div>
    </AdminLayout>
  );

  const stats = [
    { label: 'Total Revenue', value: formatINR(data?.totalRevenue ?? 0), delta: '+12.4%', up: true, icon: DollarSign, accent: 'bg-success/10 text-success' },
    { label: 'Total Orders', value: String(data?.totalOrders ?? 0), delta: '+8.1%', up: true, icon: ShoppingCart, accent: 'bg-primary/10 text-primary' },
    { label: 'Total Products', value: String(data?.totalProducts ?? 0), delta: '+3.0%', up: true, icon: Package, accent: 'bg-warning/10 text-warning' },
    { label: 'Total Users', value: String(data?.totalUsers ?? 0), delta: '+5.2%', up: true, icon: Users, accent: 'bg-discount/10 text-discount' },
  ];

  return (
    <AdminLayout title="Overview">
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {stats.map(s => (
          <div key={s.label} className="rounded-2xl border bg-card p-5">
            <div className="flex items-start justify-between">
              <div className={cn('grid h-10 w-10 place-items-center rounded-xl', s.accent)}><s.icon size={18} /></div>
              <span className={cn('inline-flex items-center gap-0.5 text-xs font-medium px-2 py-0.5 rounded-full',
                s.up ? 'text-success bg-success/10' : 'text-destructive bg-destructive/10')}>
                {s.up ? <ArrowUp size={10} /> : <ArrowDown size={10} />}{s.delta}
              </span>
            </div>
            <div className="mt-4 font-display text-2xl font-semibold">{s.value}</div>
            <div className="text-xs text-muted-foreground mt-0.5">{s.label}</div>
          </div>
        ))}
      </div>

      {/* Sales chart */}
      <div className="mt-6 grid lg:grid-cols-3 gap-4">
        <div className="lg:col-span-2 rounded-2xl border bg-card p-5">
          <div className="flex items-center justify-between mb-5">
            <div>
              <h3 className="font-semibold">Sales Overview</h3>
              <p className="text-xs text-muted-foreground">Last 12 months</p>
            </div>
          </div>
          <div className="h-56 flex items-end gap-2">
            {chart.map((h, i) => (
              <div key={i} className="flex-1 flex flex-col items-center gap-1.5">
                <div className="w-full rounded-t-md bg-gradient-to-t from-primary/80 to-primary/30 transition-all hover:from-primary"
                  style={{ height: `${h}%` }} />
                <span className="text-[10px] text-muted-foreground">{['J','F','M','A','M','J','J','A','S','O','N','D'][i]}</span>
              </div>
            ))}
          </div>
        </div>

        <div className="rounded-2xl border bg-card p-5">
          <h3 className="font-semibold mb-4">Recent Orders</h3>
          <div className="space-y-3">
            {(data?.recentOrders ?? []).map(o => (
              <div key={o.id} className="flex items-center justify-between gap-2">
                <div className="min-w-0">
                  <div className="text-sm font-medium truncate">#{o.id} · {o.username}</div>
                  <div className="text-xs text-muted-foreground">{o.itemCount} items</div>
                </div>
                <div className="text-right shrink-0">
                  <div className="text-sm font-semibold">{formatINR(o.totalAmount)}</div>
                  <Badge className={cn('text-[10px] border-0', statusColor[o.status] ?? 'bg-gray-100 text-gray-700')}>
                    {o.status}
                  </Badge>
                </div>
              </div>
            ))}
            {(data?.recentOrders?.length ?? 0) === 0 && (
              <p className="text-sm text-muted-foreground text-center py-4">No orders yet</p>
            )}
          </div>
        </div>
      </div>

      {/* Recent orders table */}
      <div className="mt-6 rounded-2xl border bg-card overflow-hidden">
        <div className="p-5 flex items-center justify-between">
          <h3 className="font-semibold">All Recent Orders</h3>
          <a href="/admin/orders" className="text-xs text-primary hover:underline">View all</a>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-muted/50 text-xs uppercase tracking-wider text-muted-foreground">
              <tr>
                <th className="text-left p-3 px-5">Order</th>
                <th className="text-left p-3">Customer</th>
                <th className="text-left p-3">Status</th>
                <th className="text-left p-3">Date</th>
                <th className="text-right p-3 px-5">Amount</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {(data?.recentOrders ?? []).map(o => (
                <tr key={o.id} className="hover:bg-muted/30">
                  <td className="p-3 px-5 font-medium">#{o.id}</td>
                  <td className="p-3">{o.username}</td>
                  <td className="p-3">
                    <Badge className={cn('border-0 capitalize text-xs', statusColor[o.status] ?? 'bg-gray-100 text-gray-700')}>
                      {o.status}
                    </Badge>
                  </td>
                  <td className="p-3 text-muted-foreground">{new Date(o.createdAt).toLocaleDateString('en-IN')}</td>
                  <td className="p-3 px-5 text-right font-semibold">{formatINR(o.totalAmount)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </AdminLayout>
  );
}
