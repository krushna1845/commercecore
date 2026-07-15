import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetFooter } from '@/components/ui/sheet';
import { useCart } from '@/context/CartContext';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Minus, Plus, ShoppingBag, Trash2 } from 'lucide-react';
import { formatINR } from '@/utils/format';
import { EmptyState } from './EmptyState';

export function CartDrawer({ open, onOpenChange }: { open: boolean; onOpenChange: (b: boolean) => void }) {
  const { items, update, remove, subtotal, count } = useCart();

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="flex w-full flex-col sm:max-w-md">
        <SheetHeader>
          <SheetTitle>Your Cart ({count})</SheetTitle>
        </SheetHeader>

        {items.length === 0 ? (
          <EmptyState
            icon={<ShoppingBag size={28} />}
            title="Your cart is empty"
            description="Discover thousands of products curated just for you."
            action={{ label: 'Start shopping', onClick: () => onOpenChange(false) }}
          />
        ) : (
          <>
            <div className="flex-1 overflow-y-auto -mx-6 px-6 divide-y">
              {items.map(({ product, quantity }) => (
                <div key={product.id} className="py-4 flex gap-3">
                  <img src={product.image} className="h-20 w-20 rounded-lg object-cover bg-muted" alt="" />
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium line-clamp-2">{product.name}</p>
                    <p className="text-xs text-muted-foreground mt-1">{product.brand}</p>
                    <div className="mt-2 flex items-center justify-between">
                      <div className="inline-flex items-center rounded-full border">
                        <button onClick={() => update(product.id, quantity - 1)} className="grid h-7 w-7 place-items-center hover:bg-muted rounded-l-full"><Minus size={12} /></button>
                        <span className="w-8 text-center text-sm">{quantity}</span>
                        <button onClick={() => update(product.id, quantity + 1)} className="grid h-7 w-7 place-items-center hover:bg-muted rounded-r-full"><Plus size={12} /></button>
                      </div>
                      <span className="text-sm font-semibold">{formatINR(product.price * quantity)}</span>
                    </div>
                  </div>
                  <button onClick={() => remove(product.id)} className="self-start p-1 text-muted-foreground hover:text-destructive">
                    <Trash2 size={16} />
                  </button>
                </div>
              ))}
            </div>
            <SheetFooter className="flex-col gap-3 sm:flex-col">
              <div className="flex items-center justify-between text-base font-semibold">
                <span>Subtotal</span>
                <span>{formatINR(subtotal)}</span>
              </div>
              <Link to="/cart" onClick={() => onOpenChange(false)}><Button variant="outline" className="w-full">View Cart</Button></Link>
              <Link to="/checkout" onClick={() => onOpenChange(false)}><Button className="w-full">Checkout</Button></Link>
            </SheetFooter>
          </>
        )}
      </SheetContent>
    </Sheet>
  );
}
