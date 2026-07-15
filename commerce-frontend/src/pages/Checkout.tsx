import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Banknote, CheckCircle2, ChevronRight, CreditCard, Loader2, LockKeyhole, MapPin, PackageCheck, Plus, ShieldCheck, Smartphone, Truck } from 'lucide-react';
import { Elements, PaymentElement, useElements, useStripe } from '@stripe/react-stripe-js';
import { loadStripe, StripeElementsOptions } from '@stripe/stripe-js';
import { MainLayout } from '@/layouts/MainLayout';
import { Breadcrumbs } from '@/components/Breadcrumbs';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { useCart } from '@/context/CartContext';
import { formatINR } from '@/utils/format';
import { cn } from '@/lib/utils';
import { userApi, UserAddress } from '@/services/userApi';
import { api } from '@/services/client';
import { toast } from 'sonner';

type CheckoutResponse = { 
  order: { id: number; totalAmount: number };
  clientSecret: string | null; 
  paymentIntentId: string; 
};
type Receipt = { orderId: number; total: number; paymentMethod: string };

const methods = [
  { id: 'card', label: 'Credit or debit card', desc: 'Secure payment with Stripe', icon: CreditCard },
  { id: 'upi', label: 'UPI', desc: 'Pay with your preferred UPI app', icon: Smartphone },
  { id: 'cod', label: 'Cash on Delivery', desc: 'Pay when your order arrives', icon: Banknote },
];

const emptyForm = { name: '', phone: '', line1: '', city: '', state: '', pincode: '', type: 'home' };
const stripePromise = loadStripe(import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY || '');

function parseStreet(street: string) {
  const [name = '', line1 = '', state = '', type = 'home'] = (street || '').split('|');
  return { name, line1, state, type };
}

function stringifyStreet(name: string, line1: string, state: string, type: string) {
  return [name, line1, state, type].map(value => value.replace(/\|/g, '')).join('|');
}

function saveReceipt(receipt: Receipt) {
  sessionStorage.setItem('checkout_receipt', JSON.stringify(receipt));
}

function StripePaymentSection({ receipt, onPaid }: { receipt: Receipt; onPaid: () => void }) {
  const stripe = useStripe();
  const elements = useElements();
  const [submitting, setSubmitting] = useState(false);

  const confirm = async () => {
    if (!stripe || !elements) return;
    setSubmitting(true);
    try {
      // Keep the receipt available when a payment method redirects to its app.
      saveReceipt(receipt);
      const { error, paymentIntent } = await stripe.confirmPayment({
        elements,
        confirmParams: { return_url: `${window.location.origin}/success` },
        redirect: 'if_required',
      });
      if (error) {
        toast.error(error.message || 'Your payment could not be completed.');
      } else if (paymentIntent?.status === 'succeeded') {
        onPaid();
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <section className="rounded-2xl border bg-card p-5 sm:p-6 shadow-sm">
      <div className="flex items-start gap-3"><LockKeyhole className="mt-0.5 text-primary" size={19} /><div><h2 className="font-semibold">Secure payment</h2><p className="text-sm text-muted-foreground">Your card details are encrypted and handled by Stripe.</p></div></div>
      <div className="mt-5 rounded-xl border bg-muted/10 p-4"><PaymentElement /></div>
      <Button className="mt-5 w-full" size="lg" onClick={confirm} disabled={submitting || !stripe || !elements}>
        {submitting ? <><Loader2 className="mr-2 animate-spin" size={16} />Processing secure payment</> : <>Pay {formatINR(receipt.total)} <LockKeyhole className="ml-2" size={15} /></>}
      </Button>
    </section>
  );
}

export default function Checkout() {
  const navigate = useNavigate();
  const { items, subtotal, clear } = useCart();
  const [addresses, setAddresses] = useState<UserAddress[]>([]);
  const [addressId, setAddressId] = useState('');
  const [loadingAddresses, setLoadingAddresses] = useState(true);
  const [method, setMethod] = useState('card');
  const [showAddressForm, setShowAddressForm] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [savingAddress, setSavingAddress] = useState(false);
  const [startingPayment, setStartingPayment] = useState(false);
  const [payment, setPayment] = useState<CheckoutResponse | null>(null);

  const loadAddresses = async () => {
    setLoadingAddresses(true);
    try {
      const list = await userApi.addresses();
      setAddresses(list);
      const preferred = list.find(address => address.isDefault) || list[0];
      if (preferred) setAddressId(String(preferred.id));
    } catch {
      toast.error('We could not load your saved addresses.');
    } finally {
      setLoadingAddresses(false);
    }
  };

  useEffect(() => { loadAddresses(); }, []);
  useEffect(() => { setPayment(null); }, [method, addressId]);

  const saveAddress = async () => {
    if (!form.name.trim() || !form.phone.trim() || !form.line1.trim() || !form.city.trim() || !form.pincode.trim()) {
      toast.error('Complete the required delivery address fields.');
      return;
    }
    const phone = form.phone.replace(/\s/g, '');
    if (!/^[+]?[1-9][0-9]{9,14}$/.test(phone) || !/^\d{5,6}$/.test(form.pincode.replace(/\s/g, ''))) {
      toast.error('Enter a valid phone number and 5 or 6 digit pincode.');
      return;
    }
    setSavingAddress(true);
    try {
      const saved = await userApi.createAddress({
        street: stringifyStreet(form.name, form.line1, form.state, form.type), city: form.city,
        zipCode: form.pincode.replace(/\s/g, ''), phone, isDefault: addresses.length === 0,
      });
      setAddresses(await userApi.addresses());
      setAddressId(String(saved.id));
      setShowAddressForm(false);
      setForm(emptyForm);
      toast.success('Delivery address saved');
    } catch (error: any) {
      toast.error(error.message || 'Unable to save this address.');
    } finally { setSavingAddress(false); }
  };

  // The payment service calculates from the server-side cart, so display the same total here.
  const delivery = 0;
  const total = subtotal + delivery;
  const receipt = payment ? { orderId: payment.order.id, total: Number(payment.order.totalAmount), paymentMethod: method } : null;

  const startCheckout = async () => {
    if (!addressId) return toast.error('Choose a delivery address to continue.');
    if (!items.length) return navigate('/cart');
    setStartingPayment(true);
    try {
      const payload = {
        paymentMethod: method, 
        shippingAddressId: Number(addressId), 
        billingAddressId: Number(addressId),
        items: items.map(item => ({ productId: item.product.id, quantity: item.quantity }))
      };
      const response = await api.post<CheckoutResponse>('/api/checkout/place-order', payload);
      if (method === 'cod') {
        const codReceipt = { orderId: response.order.id, total: Number(response.order.totalAmount), paymentMethod: 'cod' };
        saveReceipt(codReceipt);
        clear();
        navigate('/success', { state: codReceipt });
        return;
      }
      if (!response.clientSecret) throw new Error('Payment session could not be created.');
      setPayment(response);
      setTimeout(() => document.getElementById('payment-section')?.scrollIntoView({ behavior: 'smooth', block: 'start' }), 50);
    } catch (error: any) {
      toast.error(error.message || 'Unable to start checkout. Please try again.');
    } finally { setStartingPayment(false); }
  };

  const paymentCompleted = () => {
    if (!receipt) return;
    clear();
    navigate('/success', { state: receipt });
  };

  return (
    <MainLayout>
      <div className="container-x py-6 pb-14">
        <Breadcrumbs items={[{ label: 'Home', to: '/' }, { label: 'Cart', to: '/cart' }, { label: 'Checkout' }]} />
        <div className="mt-5 flex flex-col gap-3 border-b pb-5 sm:flex-row sm:items-end sm:justify-between"><div><p className="text-sm font-medium text-primary">Checkout</p><h1 className="font-display text-3xl font-semibold tracking-tight">Review and place your order</h1></div><div className="flex items-center gap-2 text-sm text-muted-foreground"><ShieldCheck size={17} className="text-success" /> Secure checkout</div></div>
        <div className="mt-5 grid grid-cols-3 gap-2 text-xs sm:text-sm"><div className="flex items-center gap-2 font-medium text-primary"><CheckCircle2 size={18} /> Cart</div><div className="flex items-center gap-2 font-medium text-primary"><span className="grid h-5 w-5 place-items-center rounded-full bg-primary text-[11px] text-primary-foreground">2</span> Delivery</div><div className="flex items-center gap-2 text-muted-foreground"><span className="grid h-5 w-5 place-items-center rounded-full border text-[11px]">3</span> Payment</div></div>

        <div className="mt-7 grid gap-7 lg:grid-cols-[minmax(0,1fr)_380px]">
          <div className="space-y-5">
            <section className="rounded-2xl border bg-card p-5 sm:p-6 shadow-sm">
              <div className="flex items-center justify-between"><div className="flex items-center gap-3"><span className="grid h-8 w-8 place-items-center rounded-full bg-primary text-sm font-bold text-primary-foreground">1</span><div><h2 className="font-semibold">Delivery address</h2><p className="text-xs text-muted-foreground">Where should we deliver your order?</p></div></div><Button size="sm" variant="outline" onClick={() => setShowAddressForm(value => !value)}><Plus size={14} className="mr-1" /> Add new</Button></div>
              {loadingAddresses ? <div className="grid place-items-center py-10"><Loader2 className="animate-spin text-primary" /></div> : addresses.length ? <RadioGroup value={addressId} onValueChange={setAddressId} className="mt-5 grid gap-3 sm:grid-cols-2">{addresses.map(address => { const details = parseStreet(address.street); const selected = String(address.id) === addressId; return <Label key={address.id} htmlFor={`address-${address.id}`} className={cn('cursor-pointer rounded-xl border p-4 transition hover:border-primary/50', selected && 'border-primary bg-primary/[0.04] ring-1 ring-primary')}><div className="flex gap-3"><RadioGroupItem id={`address-${address.id}`} value={String(address.id)} className="mt-1" /><div className="min-w-0 text-sm"><div className="flex items-center gap-2 font-semibold">{details.name}<span className="rounded bg-muted px-1.5 py-0.5 text-[10px] font-medium uppercase text-muted-foreground">{details.type}</span></div><p className="mt-1 leading-5 text-muted-foreground">{details.line1}, {address.city}{details.state ? `, ${details.state}` : ''} - {address.zipCode}</p><p className="mt-1 text-muted-foreground">{address.phone}</p></div></div></Label>; })}</RadioGroup> : !showAddressForm && <div className="mt-5 rounded-xl border border-dashed bg-muted/20 p-5 text-center text-sm text-muted-foreground">Add a delivery address to continue.</div>}
              {showAddressForm && <div className="mt-5 grid gap-3 rounded-xl border bg-muted/20 p-4 sm:grid-cols-2"><div className="sm:col-span-2 font-medium">New delivery address</div><Input value={form.name} onChange={event => setForm(value => ({ ...value, name: event.target.value }))} placeholder="Full name *" /><Input value={form.phone} onChange={event => setForm(value => ({ ...value, phone: event.target.value }))} placeholder="Phone number *" /><Input className="sm:col-span-2" value={form.line1} onChange={event => setForm(value => ({ ...value, line1: event.target.value }))} placeholder="House no., street, area *" /><Input value={form.city} onChange={event => setForm(value => ({ ...value, city: event.target.value }))} placeholder="City *" /><Input value={form.state} onChange={event => setForm(value => ({ ...value, state: event.target.value }))} placeholder="State" /><Input value={form.pincode} onChange={event => setForm(value => ({ ...value, pincode: event.target.value }))} placeholder="Pincode *" /><select value={form.type} onChange={event => setForm(value => ({ ...value, type: event.target.value }))} className="h-10 rounded-md border bg-background px-3 text-sm"><option value="home">Home</option><option value="work">Work</option><option value="other">Other</option></select><div className="flex justify-end gap-2 sm:col-span-2"><Button variant="ghost" onClick={() => setShowAddressForm(false)}>Cancel</Button><Button onClick={saveAddress} disabled={savingAddress}>{savingAddress && <Loader2 size={15} className="mr-2 animate-spin" />}Save address</Button></div></div>}
            </section>

            <section className="rounded-2xl border bg-card p-5 sm:p-6 shadow-sm"><div className="flex items-center gap-3"><span className="grid h-8 w-8 place-items-center rounded-full bg-primary text-sm font-bold text-primary-foreground">2</span><div><h2 className="font-semibold">Choose a payment method</h2><p className="text-xs text-muted-foreground">Select how you would like to pay.</p></div></div><RadioGroup value={method} onValueChange={setMethod} className="mt-5 grid gap-3">{methods.map(item => { const Icon = item.icon; const selected = method === item.id; return <Label key={item.id} htmlFor={item.id} className={cn('flex cursor-pointer items-center gap-3 rounded-xl border p-4 transition hover:border-primary/50', selected && 'border-primary bg-primary/[0.04] ring-1 ring-primary')}><RadioGroupItem id={item.id} value={item.id} /><span className="grid h-10 w-10 place-items-center rounded-lg bg-muted"><Icon size={19} /></span><span className="flex-1"><span className="block text-sm font-semibold">{item.label}</span><span className="block text-xs text-muted-foreground">{item.desc}</span></span>{selected && <CheckCircle2 size={18} className="text-primary" />}</Label>; })}</RadioGroup></section>

            <section className="rounded-2xl border bg-card p-5 sm:p-6 shadow-sm"><div className="flex items-center gap-3"><span className="grid h-8 w-8 place-items-center rounded-full bg-muted text-sm font-bold">3</span><div><h2 className="font-semibold">Items and delivery</h2><p className="text-xs text-muted-foreground">Free standard delivery on this order.</p></div></div><div className="mt-4 divide-y">{items.map(({ product, quantity }) => <div key={product.id} className="flex gap-3 py-4 first:pt-0"><img src={product.image} alt={product.name} className="h-16 w-16 rounded-lg border bg-muted object-cover" /><div className="min-w-0 flex-1"><p className="line-clamp-1 text-sm font-medium">{product.name}</p><p className="mt-1 text-xs text-muted-foreground">Quantity: {quantity}</p><p className="mt-2 text-xs text-success">In stock · Free delivery</p></div><p className="text-sm font-semibold">{formatINR(product.price * quantity)}</p></div>)}</div></section>
            {payment?.clientSecret && receipt && <div id="payment-section"><Elements stripe={stripePromise} options={{ clientSecret: payment.clientSecret } as StripeElementsOptions}><StripePaymentSection receipt={receipt} onPaid={paymentCompleted} /></Elements></div>}
          </div>

          <aside className="self-start lg:sticky lg:top-24"><div className="rounded-2xl border bg-card p-5 shadow-sm"><h2 className="font-semibold">Order summary</h2><dl className="mt-5 space-y-3 text-sm"><div className="flex justify-between"><dt className="text-muted-foreground">Items ({items.reduce((count, item) => count + item.quantity, 0)})</dt><dd>{formatINR(subtotal)}</dd></div><div className="flex justify-between"><dt className="text-muted-foreground">Delivery</dt><dd className="font-medium text-success">FREE</dd></div><div className="flex justify-between border-t pt-4 text-lg font-semibold"><dt>Order total</dt><dd>{formatINR(total)}</dd></div></dl>{subtotal > 0 && <div className="mt-4 flex gap-2 rounded-lg bg-success/10 p-3 text-xs text-success"><PackageCheck size={16} className="shrink-0" /> You qualify for free standard delivery.</div>}<Button className="mt-5 w-full" size="lg" disabled={!items.length || !addressId || startingPayment || !!payment} onClick={startCheckout}>{startingPayment ? <><Loader2 className="mr-2 animate-spin" size={16} />Preparing your order</> : payment ? 'Payment ready below' : method === 'cod' ? <>Place COD order <ChevronRight className="ml-1" size={17} /></> : <>Continue to secure payment <ChevronRight className="ml-1" size={17} /></>}</Button><div className="mt-5 space-y-3 border-t pt-4 text-xs text-muted-foreground"><p className="flex gap-2"><LockKeyhole size={15} className="shrink-0 text-primary" /> Payments are processed securely. We never see your full card details.</p><p className="flex gap-2"><Truck size={15} className="shrink-0 text-primary" /> Delivery details are shared only to fulfil this order.</p></div></div></aside>
        </div>
      </div>
    </MainLayout>
  );
}
