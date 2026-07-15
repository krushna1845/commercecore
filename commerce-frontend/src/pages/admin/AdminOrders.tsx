import { useEffect, useState } from 'react';
import { AdminLayout } from '@/layouts/AdminLayout';
import { Badge } from '@/components/ui/badge';
import { adminApi, AdminOrder } from '@/services/adminApi';
import { formatDate, formatINR } from '@/utils/format';
import { Loader2 } from 'lucide-react';
import { toast } from 'sonner';

const statusColor: Record<string, string> = {
  PENDING: 'bg-yellow-100 text-yellow-800 border-0',
  PAID: 'bg-green-100 text-green-800 border-0',
  FAILED: 'bg-red-100 text-red-800 border-0',
  SHIPPED: 'bg-blue-100 text-blue-800 border-0',
  CANCELLED: 'bg-gray-100 text-gray-800 border-0',
};

export default function AdminOrders() {
  const [orders, setOrders] = useState<AdminOrder[]>([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    setLoading(true);
    adminApi.orders()
      .then(res => setOrders(res))
      .catch(err => toast.error(err.message || 'Failed to load admin orders'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleStatusChange = async (id: number, status: string) => {
    try {
      await adminApi.updateOrderStatus(id, status);
      toast.success('Order status updated');
      load();
    } catch (err: any) {
      toast.error(err.message || 'Failed to update order status');
    }
  };

  return (
    <AdminLayout title="Orders">
      <div className="rounded-2xl border bg-card overflow-hidden">
        {loading ? (
          <div className="flex items-center justify-center py-20">
            <Loader2 className="animate-spin text-primary" size={32} />
          </div>
        ) : orders.length === 0 ? (
          <div className="text-center py-12 text-muted-foreground text-sm">
            No orders found.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-muted/40 text-xs uppercase tracking-wider text-muted-foreground">
                <tr>
                  <th className="text-left p-3 px-5">Order ID</th>
                  <th className="text-left p-3">Customer</th>
                  <th className="text-left p-3">Items Count</th>
                  <th className="text-left p-3">Date</th>
                  <th className="text-left p-3">Status</th>
                  <th className="text-right p-3 px-5">Total</th>
                </tr>
              </thead>
              <tbody className="divide-y">
                {orders.map(o => (
                  <tr key={o.id} className="hover:bg-muted/30">
                    <td className="p-3 px-5 font-medium">#{o.id}</td>
                    <td className="p-3 capitalize">{o.username}</td>
                    <td className="p-3">{o.itemCount} item{o.itemCount !== 1 ? 's' : ''}</td>
                    <td className="p-3 text-muted-foreground">{formatDate(o.createdAt)}</td>
                    <td className="p-3">
                      <select 
                        value={o.status} 
                        onChange={e => handleStatusChange(o.id, e.target.value)}
                        className={cn("h-8 px-2 text-xs rounded-md border bg-background font-semibold capitalize")}
                      >
                        <option value="PENDING">Pending</option>
                        <option value="PAID">Paid</option>
                        <option value="FAILED">Failed</option>
                        <option value="SHIPPED">Shipped</option>
                        <option value="CANCELLED">Cancelled</option>
                      </select>
                    </td>
                    <td className="p-3 px-5 text-right font-semibold">{formatINR(o.totalAmount)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </AdminLayout>
  );
}

// Simple helper for cn class names if not defined in formatting
function cn(...classes: any[]) {
  return classes.filter(Boolean).join(' ');
}
