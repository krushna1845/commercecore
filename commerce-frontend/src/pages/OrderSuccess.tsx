import { Link, useLocation } from 'react-router-dom';
import { CheckCircle2, Clock3, Package } from 'lucide-react';
import { MainLayout } from '@/layouts/MainLayout';
import { Button } from '@/components/ui/button';
import { formatDate, formatINR } from '@/utils/format';

type Receipt = { orderId?: number; total?: number; paymentMethod?: string };

export default function OrderSuccess() {
  const { state } = useLocation() as { state?: Receipt };
  let storedReceipt: Receipt | null = null;
  try { storedReceipt = JSON.parse(sessionStorage.getItem('checkout_receipt') || 'null'); } catch { /* ignore invalid stored receipt */ }
  const receipt = state || storedReceipt;
  const eta = new Date();
  eta.setDate(eta.getDate() + 5);
  const isCod = receipt?.paymentMethod === 'cod';

  return (
    <MainLayout>
      <div className="container-x mx-auto max-w-xl py-16 text-center">
        <div className="relative mb-6 inline-grid place-items-center">
          <div className="absolute inset-0 -m-4 rounded-full bg-success/20 animate-ping" />
          <div className="relative grid h-20 w-20 place-items-center rounded-full bg-success text-success-foreground"><CheckCircle2 size={36} /></div>
        </div>
        <h1 className="font-display text-3xl font-semibold">Order placed!</h1>
        <p className="mt-2 text-muted-foreground">Thank you for shopping with commercecore. We’re preparing your order.</p>

        <div className="mt-8 rounded-2xl border bg-card p-6 text-left">
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div><div className="text-xs text-muted-foreground">Order ID</div><div className="mt-0.5 font-medium">{receipt?.orderId ? `#${receipt.orderId}` : 'Available in My Orders'}</div></div>
            <div><div className="text-xs text-muted-foreground">Total</div><div className="mt-0.5 font-medium">{typeof receipt?.total === 'number' ? formatINR(receipt.total) : 'See order details'}</div></div>
            <div><div className="text-xs text-muted-foreground">Estimated delivery</div><div className="mt-0.5 font-medium">{formatDate(eta)}</div></div>
            <div><div className="text-xs text-muted-foreground">Payment</div><div className="mt-0.5 font-medium">{isCod ? 'Cash on Delivery' : 'Online payment'}</div></div>
          </div>
          <div className="mt-6 flex items-center gap-4 border-t pt-6 text-sm"><Package size={18} className="text-success" /> Order received<div className="h-px flex-1 bg-success/40" /><Clock3 size={18} className="text-muted-foreground" /><span className="text-muted-foreground">Preparing to ship</span></div>
        </div>

        <div className="mt-8 flex flex-col justify-center gap-3 sm:flex-row"><Link to="/orders"><Button size="lg">View Order</Button></Link><Link to="/products"><Button size="lg" variant="outline">Continue Shopping</Button></Link></div>
      </div>
    </MainLayout>
  );
}
