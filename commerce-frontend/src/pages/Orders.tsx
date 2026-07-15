import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ChevronRight, Loader2, Package } from 'lucide-react';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { Badge } from '@/components/ui/badge';
import { EmptyState } from '@/components/EmptyState';
import { api } from '@/services/client';
import { formatINR } from '@/utils/format';

interface OrderItem {
  productId: number;
  productName: string;
  quantity: number;
  price: number;
  imageUrl?: string;
}

interface Order {
  id: number;
  status: string;
  totalAmount: number;
  createdAt: string;
  items: OrderItem[];
}

const statusColor: Record<string, string> = {
  PLACED: 'bg-blue-100 text-blue-700',
  PACKED: 'bg-purple-100 text-purple-700',
  SHIPPED: 'bg-amber-100 text-amber-700',
  OUT_FOR_DELIVERY: 'bg-orange-100 text-orange-700',
  DELIVERED: 'bg-green-100 text-green-700',
  CANCELLED: 'bg-red-100 text-red-700',
};

function formatDate(dateStr: string) {
  try {
    return new Date(dateStr).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
  } catch { return dateStr; }
}

export default function Orders() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get<Order[]>('/orders/my')
      .then(data => setOrders(data))
      .catch(err => setError(err.message || 'Failed to load orders'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <DashboardLayout>
      <h1 className="font-display text-2xl font-semibold">My Orders</h1>
      <p className="text-sm text-muted-foreground mt-1">Track and manage your orders.</p>

      {loading ? (
        <div className="flex items-center justify-center py-20">
          <Loader2 className="animate-spin text-primary" size={32} />
        </div>
      ) : error ? (
        <div className="mt-8 text-center text-sm text-muted-foreground bg-muted/40 rounded-xl p-8">
          <Package size={32} className="mx-auto mb-3 opacity-40" />
          <p>{error}</p>
          <p className="mt-1 text-xs">Make sure the backend is running on port 8081.</p>
        </div>
      ) : orders.length === 0 ? (
        <EmptyState
          icon={<Package size={28} />}
          title="No orders yet"
          description="Your orders will appear here once you place them."
        />
      ) : (
        <div className="mt-6 space-y-4">
          {orders.map(o => (
            <div key={o.id} className="block rounded-2xl border bg-card p-5 hover:shadow-md transition">
              <div className="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <div className="text-xs text-muted-foreground">Order #{o.id}</div>
                  <div className="font-medium mt-0.5">Placed on {formatDate(o.createdAt)}</div>
                </div>
                <Badge className={`${statusColor[o.status] ?? 'bg-gray-100 text-gray-700'} border-0 capitalize`}>
                  {o.status.replace(/_/g, ' ')}
                </Badge>
              </div>
              <div className="mt-4 flex items-center gap-3">
                <div className="flex gap-2 overflow-x-auto no-scrollbar flex-1">
                  {o.items.map((item, idx) => (
                    <div key={idx} className="shrink-0">
                      {item.imageUrl ? (
                        <img src={item.imageUrl} className="h-14 w-14 rounded-lg object-cover bg-muted" alt={item.productName} />
                      ) : (
                        <div className="h-14 w-14 rounded-lg bg-muted flex items-center justify-center">
                          <Package size={18} className="text-muted-foreground" />
                        </div>
                      )}
                    </div>
                  ))}
                </div>
                <div className="text-right shrink-0">
                  <div className="text-xs text-muted-foreground">{o.items.length} item{o.items.length !== 1 ? 's' : ''}</div>
                  <div className="font-semibold">{formatINR(o.totalAmount)}</div>
                </div>
                <ChevronRight size={18} className="text-muted-foreground shrink-0" />
              </div>
            </div>
          ))}
        </div>
      )}
    </DashboardLayout>
  );
}
