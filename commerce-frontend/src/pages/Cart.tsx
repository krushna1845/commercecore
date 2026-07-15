import { Link } from 'react-router-dom';
import { Minus, Plus, ShoppingBag, Tag, Trash2 } from 'lucide-react';
import { MainLayout } from '@/layouts/MainLayout';
import { Breadcrumbs } from '@/components/Breadcrumbs';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { EmptyState } from '@/components/EmptyState';
import { useCart } from '@/context/CartContext';
import { formatINR } from '@/utils/format';
import { toast } from 'sonner';
import { useState } from 'react';

export default function CartPage() {
  const { items, update, remove, subtotal } = useCart();
  const [coupon, setCoupon] = useState('');
  const [discount, setDiscount] = useState(0);

  const delivery = subtotal > 499 || subtotal === 0 ? 0 : 49;
  const total = subtotal + delivery - discount;

  const apply = () => {
    if (coupon.trim().toUpperCase() === 'COMMERCECORE200') {
      setDiscount(200); toast.success('Coupon applied · ₹200 off');
    } else { toast.error('Invalid coupon code'); }
  };

  return (
    <MainLayout>
      <div className="container-x py-6">
        <Breadcrumbs items={[{ label: 'Home', to: '/' }, { label: 'Cart' }]} />
        <h1 className="mt-4 font-display text-2xl sm:text-3xl font-semibold">Shopping Cart</h1>

        {items.length === 0 ? (
          <EmptyState icon={<ShoppingBag size={28} />} title="Your cart is empty" description="Add some products to get started." />
        ) : (
          <div className="mt-6 grid lg:grid-cols-[1fr_360px] gap-8">
            <div className="rounded-2xl border bg-card divide-y">
              {items.map(({ product, quantity }) => (
                <div key={product.id} className="p-4 sm:p-5 flex gap-4">
                  <Link to={`/products/${product.id}`}>
                    <img src={product.image} className="h-24 w-24 sm:h-28 sm:w-28 rounded-lg object-cover bg-muted" alt="" />
                  </Link>
                  <div className="flex-1 min-w-0">
                    <div className="text-xs text-muted-foreground">{product.brand}</div>
                    <Link to={`/products/${product.id}`} className="text-sm sm:text-base font-medium hover:text-primary line-clamp-2">{product.name}</Link>
                    <div className="mt-1 text-xs text-success font-medium">In stock · ships in 2 days</div>
                    <div className="mt-3 flex flex-wrap items-center gap-3 justify-between">
                      <div className="inline-flex items-center rounded-full border">
                        <button onClick={() => update(product.id, quantity - 1)} className="grid h-8 w-8 place-items-center hover:bg-muted rounded-l-full"><Minus size={12} /></button>
                        <span className="w-10 text-center text-sm">{quantity}</span>
                        <button onClick={() => update(product.id, quantity + 1)} className="grid h-8 w-8 place-items-center hover:bg-muted rounded-r-full"><Plus size={12} /></button>
                      </div>
                      <div className="font-semibold">{formatINR(product.price * quantity)}</div>
                    </div>
                  </div>
                  <button onClick={() => remove(product.id)} className="self-start p-2 text-muted-foreground hover:text-destructive">
                    <Trash2 size={16} />
                  </button>
                </div>
              ))}
            </div>

            <aside className="space-y-4">
              <div className="rounded-2xl border bg-card p-5">
                <h3 className="font-semibold">Apply Coupon</h3>
                <div className="mt-3 flex gap-2">
                  <Input value={coupon} onChange={(e) => setCoupon(e.target.value)} placeholder="COMMERCECORE200" />
                  <Button onClick={apply} variant="outline"><Tag size={14} className="mr-1" />Apply</Button>
                </div>
                {discount > 0 && <p className="mt-2 text-xs text-success">✓ Coupon applied — saving {formatINR(discount)}</p>}
              </div>

              <div className="rounded-2xl border bg-card p-5">
                <h3 className="font-semibold">Order Summary</h3>
                <dl className="mt-4 space-y-2.5 text-sm">
                  <div className="flex justify-between"><dt className="text-muted-foreground">Subtotal</dt><dd>{formatINR(subtotal)}</dd></div>
                  <div className="flex justify-between"><dt className="text-muted-foreground">Delivery</dt><dd className={delivery === 0 ? 'text-success' : ''}>{delivery === 0 ? 'Free' : formatINR(delivery)}</dd></div>
                  {discount > 0 && <div className="flex justify-between text-success"><dt>Discount</dt><dd>-{formatINR(discount)}</dd></div>}
                  <div className="border-t pt-3 flex justify-between font-display text-lg font-semibold"><dt>Total</dt><dd>{formatINR(total)}</dd></div>
                </dl>
                <Link to="/checkout"><Button className="w-full mt-5" size="lg">Proceed to Checkout</Button></Link>
                <p className="mt-3 text-xs text-center text-muted-foreground">Secure checkout · Free returns within 7 days</p>
              </div>
            </aside>
          </div>
        )}
      </div>
    </MainLayout>
  );
}
