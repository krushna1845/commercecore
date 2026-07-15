import { Link, useParams } from 'react-router-dom';
import { CheckCircle2, Package, Truck, Home } from 'lucide-react';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { Button } from '@/components/ui/button';
import { orders } from '@/data/users';
import { formatDate, formatINR } from '@/utils/format';
import { cn } from '@/lib/utils';

const timeline = [
  { id: 'placed', label: 'Order Placed', icon: CheckCircle2 },
  { id: 'packed', label: 'Packed', icon: Package },
  { id: 'shipped', label: 'Shipped', icon: Truck },
  { id: 'out-for-delivery', label: 'Out for Delivery', icon: Truck },
  { id: 'delivered', label: 'Delivered', icon: Home },
];

export default function OrderDetails() {
  const { id } = useParams();
  const order = orders.find(o => o.id === id) ?? orders[0];
  const idx = timeline.findIndex(t => t.id === order.status);
  const reached = idx === -1 ? timeline.length : idx + 1;

  return (
    <DashboardLayout>
      <Link to="/orders" className="text-sm text-muted-foreground hover:text-foreground">← Back to orders</Link>
      <div className="mt-3 flex items-end justify-between flex-wrap gap-3">
        <div>
          <h1 className="font-display text-2xl font-semibold">Order #{order.id}</h1>
          <p className="text-sm text-muted-foreground mt-1">Placed on {formatDate(order.date)} · {order.paymentMethod}</p>
        </div>
        <Button variant="outline" size="sm">Download invoice</Button>
      </div>

      {/* Timeline */}
      <div className="mt-6 rounded-2xl border bg-card p-6">
        <div className="grid grid-cols-5 gap-2">
          {timeline.map((t, i) => {
            const done = i < reached;
            const Icon = t.icon;
            return (
              <div key={t.id} className="relative flex flex-col items-center text-center">
                {i > 0 && (
                  <div className={cn('absolute right-1/2 top-5 h-0.5 w-full -translate-y-1/2', done ? 'bg-success' : 'bg-border')} />
                )}
                <div className={cn('relative z-10 grid h-10 w-10 place-items-center rounded-full',
                  done ? 'bg-success text-success-foreground' : 'bg-muted text-muted-foreground')}>
                  <Icon size={16} />
                </div>
                <div className="mt-2 text-xs font-medium">{t.label}</div>
              </div>
            );
          })}
        </div>
        <p className="mt-6 text-sm text-center text-muted-foreground">
          Estimated delivery by <span className="font-medium text-foreground">{formatDate(order.estimatedDelivery)}</span>
        </p>
      </div>

      <div className="mt-6 grid lg:grid-cols-[1fr_320px] gap-6">
        <div className="rounded-2xl border bg-card divide-y">
          {order.items.map(({ product, quantity }) => (
            <div key={product.id} className="p-4 flex gap-4">
              <img src={product.image} className="h-20 w-20 rounded-lg object-cover bg-muted" alt="" />
              <div className="flex-1 min-w-0">
                <div className="text-xs text-muted-foreground">{product.brand}</div>
                <div className="text-sm font-medium line-clamp-2">{product.name}</div>
                <div className="text-xs text-muted-foreground mt-1">Qty: {quantity}</div>
              </div>
              <div className="text-sm font-semibold">{formatINR(product.price * quantity)}</div>
            </div>
          ))}
        </div>

        <aside className="space-y-4">
          <div className="rounded-2xl border bg-card p-5">
            <h3 className="font-semibold mb-3">Delivery Address</h3>
            <div className="text-sm text-muted-foreground">
              <div className="font-medium text-foreground">{order.address.name}</div>
              {order.address.line1}<br />{order.address.city}, {order.address.state} - {order.address.pincode}<br />
              {order.address.phone}
            </div>
          </div>
          <div className="rounded-2xl border bg-card p-5">
            <h3 className="font-semibold mb-3">Total</h3>
            <div className="font-display text-2xl font-semibold">{formatINR(order.total)}</div>
            <div className="text-xs text-muted-foreground mt-1">Paid via {order.paymentMethod}</div>
          </div>
        </aside>
      </div>
    </DashboardLayout>
  );
}
