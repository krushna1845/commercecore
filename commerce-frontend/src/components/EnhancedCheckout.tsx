import { useState, useEffect } from 'react';
import { 
  MapPin, CreditCard, Gift, Truck, Clock, Check, ChevronRight, 
  ChevronLeft, AlertCircle, Plus, Trash2, FileText, Printer
} from 'lucide-react';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { Card } from './ui/card';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Textarea } from './ui/textarea';
import { Switch } from './ui/switch';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { RadioGroup, RadioGroupItem } from './ui/radio-group';
import { Separator } from './ui/separator';
import { ScrollArea } from './ui/scroll-area';
import { Progress } from './ui/progress';

interface CartItem {
  productId: number;
  quantity: number;
  name: string;
  price: number;
  image: string;
}

interface Address {
  id: number;
  addressLine1: string;
  addressLine2: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  isDefault: boolean;
  addressType: string;
}

interface ShippingMethod {
  id: number;
  name: string;
  description: string;
  basePrice: number;
  estimatedDaysMin: number;
  estimatedDaysMax: number;
}

interface DeliverySlot {
  id: number;
  slotDate: string;
  startTime: string;
  endTime: string;
  available: boolean;
  price: number;
}

interface GiftWrap {
  id: number;
  name: string;
  description: string;
  price: number;
  imageUrl: string;
}

interface OrderSummary {
  subtotal: number;
  shippingCost: number;
  giftWrapCost: number;
  deliverySlotCost: number;
  discount: number;
  gst: number;
  total: number;
  items: any[];
  appliedCouponCode: string;
  couponDiscount: number;
}

const CHECKOUT_STEPS = [
  { id: 1, name: 'Address', icon: MapPin },
  { id: 2, name: 'Delivery', icon: Truck },
  { id: 3, name: 'Payment', icon: CreditCard },
  { id: 4, name: 'Review', icon: Check },
];

export function EnhancedCheckout({ cartItems }: { cartItems: CartItem[] }) {
  const [currentStep, setCurrentStep] = useState(1);
  const [addresses, setAddresses] = useState<Address[]>([]);
  const [selectedShippingAddress, setSelectedShippingAddress] = useState<number | null>(null);
  const [selectedBillingAddress, setSelectedBillingAddress] = useState<number | null>(null);
  const [sameAsShipping, setSameAsShipping] = useState(true);
  const [shippingMethods, setShippingMethods] = useState<ShippingMethod[]>([]);
  const [selectedShippingMethod, setSelectedShippingMethod] = useState<number | null>(null);
  const [deliverySlots, setDeliverySlots] = useState<DeliverySlot[]>([]);
  const [selectedDeliverySlot, setSelectedDeliverySlot] = useState<number | null>(null);
  const [giftWraps, setGiftWraps] = useState<GiftWrap[]>([]);
  const [selectedGiftWrap, setSelectedGiftWrap] = useState<number | null>(null);
  const [giftMessage, setGiftMessage] = useState('');
  const [couponCode, setCouponCode] = useState('');
  const [couponApplied, setCouponApplied] = useState(false);
  const [gstInvoice, setGstInvoice] = useState(false);
  const [gstNumber, setGstNumber] = useState('');
  const [paymentMethod, setPaymentMethod] = useState('CREDIT_CARD');
  const [orderSummary, setOrderSummary] = useState<OrderSummary | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [newAddressOpen, setNewAddressOpen] = useState(false);

  useEffect(() => {
    initializeCheckout();
  }, []);

  const initializeCheckout = async () => {
    setIsLoading(true);
    try {
      const response = await fetch('/api/checkout/initialize', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({ items: cartItems }),
      });
      if (response.ok) {
        const data = await response.json();
        setShippingMethods(data.shippingMethods);
        setDeliverySlots(data.deliverySlots);
        setGiftWraps(data.giftWraps);
        setOrderSummary(data.orderSummary);
      }
    } catch (error) {
      console.error('Failed to initialize checkout:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const calculateSummary = async () => {
    setIsLoading(true);
    try {
      const response = await fetch('/api/checkout/calculate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({
          items: cartItems,
          shippingMethodId: selectedShippingMethod,
          deliverySlotId: selectedDeliverySlot,
          giftWrapId: selectedGiftWrap,
          couponCode: couponApplied ? couponCode : null,
          gstInvoice,
        }),
      });
      if (response.ok) {
        const data = await response.json();
        setOrderSummary(data);
      }
    } catch (error) {
      console.error('Failed to calculate summary:', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (selectedShippingMethod !== null || selectedDeliverySlot !== null || 
        selectedGiftWrap !== null || couponApplied) {
      calculateSummary();
    }
  }, [selectedShippingMethod, selectedDeliverySlot, selectedGiftWrap, couponApplied, gstInvoice]);

  const applyCoupon = async () => {
    if (!couponCode.trim()) return;
    setIsLoading(true);
    try {
      const response = await fetch('/api/checkout/calculate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({
          items: cartItems,
          shippingMethodId: selectedShippingMethod,
          deliverySlotId: selectedDeliverySlot,
          giftWrapId: selectedGiftWrap,
          couponCode,
          gstInvoice,
        }),
      });
      if (response.ok) {
        const data = await response.json();
        setOrderSummary(data);
        setCouponApplied(true);
      } else {
        alert('Invalid coupon code');
      }
    } catch (error) {
      console.error('Failed to apply coupon:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const placeOrder = async () => {
    if (!selectedShippingAddress || !selectedBillingAddress) {
      alert('Please select shipping and billing addresses');
      return;
    }
    if (!selectedShippingMethod) {
      alert('Please select a shipping method');
      return;
    }

    setIsLoading(true);
    try {
      const response = await fetch('/api/checkout/place-order', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({
          items: cartItems,
          shippingAddressId: selectedShippingAddress,
          billingAddressId: selectedBillingAddress,
          shippingMethodId: selectedShippingMethod,
          deliverySlotId: selectedDeliverySlot,
          giftWrapId: selectedGiftWrap,
          couponCode: couponApplied ? couponCode : null,
          gstInvoice,
          gstNumber,
          giftMessage,
          paymentMethod,
        }),
      });
      if (response.ok) {
        const order = await response.json();
        alert('Order placed successfully! Order ID: ' + order.id);
        // Redirect to order confirmation page
        window.location.href = `/order-confirmation/${order.id}`;
      } else {
        alert('Failed to place order');
      }
    } catch (error) {
      console.error('Failed to place order:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const nextStep = () => {
    if (currentStep === 1 && !selectedShippingAddress) {
      alert('Please select a shipping address');
      return;
    }
    if (currentStep === 2 && !selectedShippingMethod) {
      alert('Please select a shipping method');
      return;
    }
    if (currentStep < 4) setCurrentStep(currentStep + 1);
  };

  const prevStep = () => {
    if (currentStep > 1) setCurrentStep(currentStep - 1);
  };

  const StepProgress = () => (
    <div className="mb-8">
      <div className="flex items-center justify-between mb-4">
        {CHECKOUT_STEPS.map((step, index) => {
          const Icon = step.icon;
          const isCompleted = currentStep > step.id;
          const isCurrent = currentStep === step.id;
          return (
            <div key={step.id} className="flex items-center flex-1">
              <div className={`flex items-center justify-center w-10 h-10 rounded-full ${
                isCompleted ? 'bg-green-500 text-white' : 
                isCurrent ? 'bg-primary text-primary-foreground' : 
                'bg-muted text-muted-foreground'
              }`}>
                {isCompleted ? <Check className="w-5 h-5" /> : <Icon className="w-5 h-5" />}
              </div>
              <span className={`ml-2 text-sm font-medium ${
                isCurrent ? 'text-foreground' : 'text-muted-foreground'
              }`}>{step.name}</span>
              {index < CHECKOUT_STEPS.length - 1 && (
                <div className={`flex-1 h-1 mx-4 ${isCompleted ? 'bg-green-500' : 'bg-muted'}`} />
              )}
            </div>
          );
        })}
      </div>
      <Progress value={(currentStep / 4) * 100} className="h-2" />
    </div>
  );

  const AddressStep = () => (
    <div className="space-y-6">
      <h3 className="text-xl font-semibold">Select Delivery Address</h3>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {addresses.map((address) => (
          <Card 
            key={address.id} 
            className={`p-4 cursor-pointer transition-colors ${
              selectedShippingAddress === address.id ? 'border-primary border-2' : 'hover:border-primary'
            }`}
            onClick={() => setSelectedShippingAddress(address.id)}
          >
            <div className="flex items-start justify-between">
              <div>
                <div className="font-medium">{address.addressType}</div>
                <div className="text-sm text-muted-foreground mt-1">
                  {address.addressLine1}
                  {address.addressLine2 && <>, {address.addressLine2}</>}
                </div>
                <div className="text-sm text-muted-foreground">
                  {address.city}, {address.state} {address.postalCode}
                </div>
                <div className="text-sm text-muted-foreground">{address.country}</div>
              </div>
              {address.isDefault && <Badge variant="secondary">Default</Badge>}
            </div>
          </Card>
        ))}
        <Card className="p-4 cursor-pointer hover:border-primary flex items-center justify-center border-dashed" onClick={() => setNewAddressOpen(true)}>
          <div className="text-center">
            <Plus className="w-8 h-8 mx-auto mb-2 text-muted-foreground" />
            <div className="text-sm font-medium">Add New Address</div>
          </div>
        </Card>
      </div>

      <div className="flex items-center space-x-2">
        <Switch
          id="same-as-shipping"
          checked={sameAsShipping}
          onCheckedChange={setSameAsShipping}
        />
        <Label htmlFor="same-as-shipping">Billing address same as shipping</Label>
      </div>

      {!sameAsShipping && (
        <div>
          <Label>Select Billing Address</Label>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-2">
            {addresses.map((address) => (
              <Card 
                key={address.id} 
                className={`p-4 cursor-pointer transition-colors ${
                  selectedBillingAddress === address.id ? 'border-primary border-2' : 'hover:border-primary'
                }`}
                onClick={() => setSelectedBillingAddress(address.id)}
              >
                <div className="text-sm">
                  <div className="font-medium">{address.addressType}</div>
                  <div className="text-muted-foreground">{address.addressLine1}, {address.city}</div>
                </div>
              </Card>
            ))}
          </div>
        </div>
      )}
    </div>
  );

  const DeliveryStep = () => (
    <div className="space-y-6">
      <h3 className="text-xl font-semibold">Select Delivery Method</h3>
      
      <RadioGroup value={selectedShippingMethod?.toString()} onValueChange={(v) => setSelectedShippingMethod(Number(v))}>
        {shippingMethods.map((method) => (
          <Card key={method.id} className="p-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-3">
                <RadioGroupItem value={method.id.toString()} id={`method-${method.id}`} />
                <Label htmlFor={`method-${method.id}`} className="cursor-pointer">
                  <div className="font-medium">{method.name}</div>
                  <div className="text-sm text-muted-foreground">{method.description}</div>
                  <div className="text-sm text-muted-foreground">
                    Estimated: {method.estimatedDaysMin}-{method.estimatedDaysMax} days
                  </div>
                </Label>
              </div>
              <div className="font-semibold">${method.basePrice.toFixed(2)}</div>
            </div>
          </Card>
        ))}
      </RadioGroup>

      <h3 className="text-xl font-semibold">Select Delivery Slot</h3>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
        {deliverySlots.map((slot) => (
          <Card 
            key={slot.id}
            className={`p-3 cursor-pointer transition-colors ${
              !slot.available ? 'opacity-50 cursor-not-allowed' : 
              selectedDeliverySlot === slot.id ? 'border-primary border-2' : 'hover:border-primary'
            }`}
            onClick={() => slot.available && setSelectedDeliverySlot(slot.id)}
          >
            <div className="text-center">
              <div className="font-medium text-sm">{slot.slotDate}</div>
              <div className="text-xs text-muted-foreground">{slot.startTime} - {slot.endTime}</div>
              {slot.price > 0 && <div className="text-sm font-semibold mt-1">${slot.price.toFixed(2)}</div>}
              {!slot.available && <Badge variant="destructive" className="mt-2">Full</Badge>}
            </div>
          </Card>
        ))}
      </div>

      <h3 className="text-xl font-semibold">Gift Wrapping (Optional)</h3>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
        <Card 
          className={`p-3 cursor-pointer transition-colors ${
            selectedGiftWrap === null ? 'border-primary border-2' : 'hover:border-primary'
          }`}
          onClick={() => setSelectedGiftWrap(null)}
        >
          <div className="text-center">
            <div className="font-medium text-sm">No Gift Wrap</div>
            <div className="text-sm text-muted-foreground">Free</div>
          </div>
        </Card>
        {giftWraps.map((wrap) => (
          <Card 
            key={wrap.id}
            className={`p-3 cursor-pointer transition-colors ${
              selectedGiftWrap === wrap.id ? 'border-primary border-2' : 'hover:border-primary'
            }`}
            onClick={() => setSelectedGiftWrap(wrap.id)}
          >
            <div className="text-center">
              <div className="font-medium text-sm">{wrap.name}</div>
              <div className="text-sm text-muted-foreground">{wrap.description}</div>
              <div className="text-sm font-semibold mt-1">${wrap.price.toFixed(2)}</div>
            </div>
          </Card>
        ))}
      </div>

      {selectedGiftWrap && (
        <div>
          <Label htmlFor="gift-message">Gift Message</Label>
          <Textarea
            id="gift-message"
            placeholder="Add a personal message..."
            value={giftMessage}
            onChange={(e) => setGiftMessage(e.target.value)}
            rows={3}
          />
        </div>
      )}
    </div>
  );

  const PaymentStep = () => (
    <div className="space-y-6">
      <h3 className="text-xl font-semibold">Apply Coupon</h3>
      <div className="flex gap-2">
        <Input
          placeholder="Enter coupon code"
          value={couponCode}
          onChange={(e) => setCouponCode(e.target.value.toUpperCase())}
          disabled={couponApplied}
        />
        <Button onClick={applyCoupon} disabled={couponApplied || isLoading}>
          {couponApplied ? 'Applied' : 'Apply'}
        </Button>
        {couponApplied && (
          <Button variant="outline" onClick={() => { setCouponApplied(false); setCouponCode(''); }}>
            <Trash2 className="w-4 h-4" />
          </Button>
        )}
      </div>

      <Separator />

      <h3 className="text-xl font-semibold">GST Invoice</h3>
      <div className="flex items-center space-x-2">
        <Switch
          id="gst-invoice"
          checked={gstInvoice}
          onCheckedChange={setGstInvoice}
        />
        <Label htmlFor="gst-invoice">I need a GST invoice</Label>
      </div>

      {gstInvoice && (
        <div>
          <Label htmlFor="gst-number">GST Number</Label>
          <Input
            id="gst-number"
            placeholder="Enter your GST number"
            value={gstNumber}
            onChange={(e) => setGstNumber(e.target.value.toUpperCase())}
            maxLength={15}
          />
        </div>
      )}

      <Separator />

      <h3 className="text-xl font-semibold">Payment Method</h3>
      <RadioGroup value={paymentMethod} onValueChange={setPaymentMethod}>
        <Card className="p-4">
          <div className="flex items-center space-x-3">
            <RadioGroupItem value="CREDIT_CARD" id="credit-card" />
            <Label htmlFor="credit-card" className="cursor-pointer flex items-center">
              <CreditCard className="w-5 h-5 mr-2" />
              Credit/Debit Card
            </Label>
          </div>
        </Card>
        <Card className="p-4">
          <div className="flex items-center space-x-3">
            <RadioGroupItem value="UPI" id="upi" />
            <Label htmlFor="upi" className="cursor-pointer">UPI</Label>
          </div>
        </Card>
        <Card className="p-4">
          <div className="flex items-center space-x-3">
            <RadioGroupItem value="NET_BANKING" id="net-banking" />
            <Label htmlFor="net-banking" className="cursor-pointer">Net Banking</Label>
          </div>
        </Card>
        <Card className="p-4">
          <div className="flex items-center space-x-3">
            <RadioGroupItem value="PAY_ON_DELIVERY" id="cod" />
            <Label htmlFor="cod" className="cursor-pointer">Cash on Delivery</Label>
          </div>
        </Card>
      </RadioGroup>
    </div>
  );

  const ReviewStep = () => (
    <div className="space-y-6">
      <h3 className="text-xl font-semibold">Review Your Order</h3>
      
      <Card className="p-6">
        <div className="space-y-4">
          <div>
            <h4 className="font-medium mb-2">Shipping Address</h4>
            <div className="text-sm text-muted-foreground">
              {addresses.find(a => a.id === selectedShippingAddress)?.addressLine1},<br />
              {addresses.find(a => a.id === selectedShippingAddress)?.city}, {addresses.find(a => a.id === selectedShippingAddress)?.state} {addresses.find(a => a.id === selectedShippingAddress)?.postalCode}
            </div>
          </div>
          
          <Separator />
          
          <div>
            <h4 className="font-medium mb-2">Delivery Method</h4>
            <div className="text-sm text-muted-foreground">
              {shippingMethods.find(m => m.id === selectedShippingMethod)?.name}<br />
              Estimated: {shippingMethods.find(m => m.id === selectedShippingMethod)?.estimatedDaysMin}-{shippingMethods.find(m => m.id === selectedShippingMethod)?.estimatedDaysMax} days
            </div>
          </div>

          {selectedDeliverySlot && (
            <>
              <Separator />
              <div>
                <h4 className="font-medium mb-2">Delivery Slot</h4>
                <div className="text-sm text-muted-foreground">
                  {deliverySlots.find(s => s.id === selectedDeliverySlot)?.slotDate}<br />
                  {deliverySlots.find(s => s.id === selectedDeliverySlot)?.startTime} - {deliverySlots.find(s => s.id === selectedDeliverySlot)?.endTime}
                </div>
              </div>
            </>
          )}

          {selectedGiftWrap && (
            <>
              <Separator />
              <div>
                <h4 className="font-medium mb-2">Gift Wrap</h4>
                <div className="text-sm text-muted-foreground">
                  {giftWraps.find(w => w.id === selectedGiftWrap)?.name}
                </div>
              </div>
            </>
          )}

          {couponApplied && (
            <>
              <Separator />
              <div>
                <h4 className="font-medium mb-2">Coupon Applied</h4>
                <div className="text-sm text-green-600">{couponCode}</div>
              </div>
            </>
          )}

          {gstInvoice && (
            <>
              <Separator />
              <div>
                <h4 className="font-medium mb-2">GST Invoice</h4>
                <div className="text-sm text-muted-foreground">GST Number: {gstNumber}</div>
              </div>
            </>
          )}
        </div>
      </Card>

      <Card className="p-6">
        <h4 className="font-medium mb-4">Order Summary</h4>
        <div className="space-y-2">
          <div className="flex justify-between">
            <span>Subtotal</span>
            <span>${orderSummary?.subtotal.toFixed(2)}</span>
          </div>
          <div className="flex justify-between">
            <span>Shipping</span>
            <span>${orderSummary?.shippingCost.toFixed(2)}</span>
          </div>
          {orderSummary?.giftWrapCost > 0 && (
            <div className="flex justify-between">
              <span>Gift Wrap</span>
              <span>${orderSummary?.giftWrapCost.toFixed(2)}</span>
            </div>
          )}
          {orderSummary?.deliverySlotCost > 0 && (
            <div className="flex justify-between">
              <span>Delivery Slot</span>
              <span>${orderSummary?.deliverySlotCost.toFixed(2)}</span>
            </div>
          )}
          {orderSummary?.discount > 0 && (
            <div className="flex justify-between text-green-600">
              <span>Discount</span>
              <span>-${orderSummary?.discount.toFixed(2)}</span>
            </div>
          )}
          {orderSummary?.gst > 0 && (
            <div className="flex justify-between">
              <span>GST (18%)</span>
              <span>${orderSummary?.gst.toFixed(2)}</span>
            </div>
          )}
          <Separator />
          <div className="flex justify-between font-bold text-lg">
            <span>Total</span>
            <span>${orderSummary?.total.toFixed(2)}</span>
          </div>
        </div>
      </Card>

      <div className="flex gap-2">
        <Button variant="outline" onClick={() => calculateSummary()}>
          <FileText className="w-4 h-4 mr-2" />
          Download Invoice
        </Button>
        <Button variant="outline" onClick={() => window.print()}>
          <Printer className="w-4 h-4 mr-2" />
          Print
        </Button>
      </div>
    </div>
  );

  return (
    <div className="container mx-auto p-6 max-w-6xl">
      <h1 className="text-3xl font-bold mb-8">Checkout</h1>
      
      <StepProgress />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2">
          <Card className="p-6">
            {currentStep === 1 && <AddressStep />}
            {currentStep === 2 && <DeliveryStep />}
            {currentStep === 3 && <PaymentStep />}
            {currentStep === 4 && <ReviewStep />}

            <div className="flex justify-between mt-8">
              <Button variant="outline" onClick={prevStep} disabled={currentStep === 1}>
                <ChevronLeft className="w-4 h-4 mr-2" />
                Back
              </Button>
              {currentStep < 4 ? (
                <Button onClick={nextStep} disabled={isLoading}>
                  Next
                  <ChevronRight className="w-4 h-4 ml-2" />
                </Button>
              ) : (
                <Button onClick={placeOrder} disabled={isLoading} size="lg">
                  {isLoading ? 'Processing...' : 'Place Order'}
                </Button>
              )}
            </div>
          </Card>
        </div>

        <div className="lg:col-span-1">
          <Card className="p-6 sticky top-4">
            <h3 className="font-semibold mb-4">Order Summary</h3>
            <ScrollArea className="h-[400px]">
              <div className="space-y-4">
                {cartItems.map((item) => (
                  <div key={item.productId} className="flex gap-4">
                    <img src={item.image} alt={item.name} className="w-16 h-16 object-cover rounded" />
                    <div className="flex-1">
                      <div className="font-medium text-sm">{item.name}</div>
                      <div className="text-sm text-muted-foreground">Qty: {item.quantity}</div>
                      <div className="font-semibold">${(item.price * item.quantity).toFixed(2)}</div>
                    </div>
                  </div>
                ))}
              </div>
            </ScrollArea>
            
            <Separator className="my-4" />
            
            <div className="space-y-2">
              <div className="flex justify-between text-sm">
                <span>Subtotal</span>
                <span>${orderSummary?.subtotal.toFixed(2)}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span>Shipping</span>
                <span>${orderSummary?.shippingCost.toFixed(2)}</span>
              </div>
              {orderSummary?.discount > 0 && (
                <div className="flex justify-between text-sm text-green-600">
                  <span>Discount</span>
                  <span>-${orderSummary?.discount.toFixed(2)}</span>
                </div>
              )}
              <Separator />
              <div className="flex justify-between font-bold">
                <span>Total</span>
                <span>${orderSummary?.total.toFixed(2)}</span>
              </div>
            </div>

            {orderSummary?.couponDiscount > 0 && (
              <div className="mt-4 p-3 bg-green-50 dark:bg-green-900/20 rounded-lg">
                <div className="flex items-center gap-2 text-green-700 dark:text-green-400">
                  <Check className="w-4 h-4" />
                  <span className="text-sm font-medium">Coupon Applied: {orderSummary.appliedCouponCode}</span>
                </div>
                <div className="text-sm text-green-600 dark:text-green-500">
                  You saved ${orderSummary.couponDiscount.toFixed(2)}
                </div>
              </div>
            )}
          </Card>
        </div>
      </div>
    </div>
  );
}
