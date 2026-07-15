import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Heart, Minus, Plus, RotateCcw, Shield, ShoppingBag, Truck, Loader2 } from 'lucide-react';
import { MainLayout } from '@/layouts/MainLayout';
import { Breadcrumbs } from '@/components/Breadcrumbs';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Rating } from '@/components/Rating';
import { ProductCard } from '@/components/ProductCard';
import { reviews } from '@/data/products';
import { discountPct, formatINR } from '@/utils/format';
import { useCart } from '@/context/CartContext';
import { useWishlist } from '@/context/WishlistContext';
import { cn } from '@/lib/utils';
import { productApi } from '@/services/productApi';
import type { Product } from '@/types';

const mapProduct = (p: any): Product => ({
  id: String(p.id),
  name: p.name,
  description: p.description || '',
  price: p.price,
  originalPrice: p.originalPrice || p.price * 1.2,
  rating: p.rating || 4.5,
  reviewCount: p.reviewCount || 12,
  image: p.imageUrl || p.image || '',
  images: p.images || [p.imageUrl || p.image || ''],
  category: p.category?.name || p.category || 'General',
  brand: p.brand || 'Premium',
  stock: p.stockQuantity !== undefined ? p.stockQuantity : (p.stock || 0),
  badge: p.badge,
  specs: p.specs || {
    "Brand": p.brand || "Premium",
    "Category": p.category?.name || p.category || "General",
    "Model": "Model X",
    "Warranty": "1 Year"
  },
  seller: p.seller?.username || p.seller
});

export default function ProductDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { add } = useCart();
  const { toggle, has } = useWishlist();

  const [product, setProduct] = useState<Product | null>(null);
  const [related, setRelated] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [qty, setQty] = useState(1);
  const [activeImg, setActiveImg] = useState(0);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    setError('');
    Promise.all([productApi.get(id), productApi.list()])
      .then(([productResponse, productsResponse]) => {
        const currentProduct = mapProduct(productResponse);
        setProduct(currentProduct);
        setRelated(
          productsResponse
            .map(mapProduct)
            .filter((item) => item.category === currentProduct.category && item.id !== currentProduct.id)
            .slice(0, 4)
        );
      })
      .catch((err) => {
        console.error(err);
        setError(err?.response?.data?.message || err.message || 'Failed to load product details');
      })
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) {
    return (
      <MainLayout>
        <div className="flex items-center justify-center min-h-[50vh]">
          <Loader2 className="animate-spin text-primary" size={36} />
        </div>
      </MainLayout>
    );
  }

  if (error || !product) {
    return (
      <MainLayout>
        <div className="container-x py-12 text-center">
          <h2 className="text-xl font-semibold text-destructive">Error</h2>
          <p className="mt-2 text-muted-foreground">{error || 'Product not found.'}</p>
          <Link to="/products"><Button className="mt-4">Back to Products</Button></Link>
        </div>
      </MainLayout>
    );
  }

  const off = discountPct(product.price, product.originalPrice);
  const gallery = product.images?.length ? product.images : [product.image, product.image, product.image, product.image];

  return (
    <MainLayout>
      <div className="container-x py-6">
        <Breadcrumbs items={[
          { label: 'Home', to: '/' },
          { label: 'Products', to: '/products' },
          { label: product.category, to: `/products?category=${product.category}` },
          { label: product.name },
        ]} />

        <div className="mt-6 grid lg:grid-cols-2 gap-10">
          {/* Gallery */}
          <div className="grid grid-cols-[80px_1fr] gap-3">
            <div className="flex flex-col gap-2">
              {gallery.slice(0, 4).map((src, i) => (
                <button key={i} onClick={() => setActiveImg(i)}
                  className={cn('aspect-square rounded-lg overflow-hidden border-2', activeImg === i ? 'border-primary' : 'border-transparent')}>
                  <img src={src} alt="" className="h-full w-full object-cover bg-muted" />
                </button>
              ))}
            </div>
            <div className="aspect-square rounded-2xl overflow-hidden bg-muted relative">
              <img src={gallery[activeImg] || product.image} alt={product.name} className="h-full w-full object-cover" />
              {off > 0 && <Badge className="absolute left-4 top-4 bg-discount text-white border-0">-{off}%</Badge>}
            </div>
          </div>

          {/* Info */}
          <div>
            <div className="text-xs uppercase tracking-wider text-primary font-semibold">{product.brand}</div>
            <h1 className="mt-2 font-display text-2xl sm:text-3xl font-semibold leading-tight">{product.name}</h1>
            {product.seller && (
              <div className="mt-2 text-sm text-muted-foreground">
                Sold by: <span className="font-medium text-primary">{product.seller}</span>
              </div>
            )}
            <div className="mt-3 flex items-center gap-3">
              <Rating value={product.rating} showValue />
              <span className="text-xs text-muted-foreground">({product.reviewCount} reviews)</span>
              <span className="text-muted-foreground">·</span>
              {product.stock > 0 ? (
                <span className="text-xs font-medium text-success">In Stock · {product.stock} left</span>
              ) : (
                <span className="text-xs font-medium text-destructive">Out of stock</span>
              )}
            </div>

            <div className="mt-5 flex items-baseline gap-3">
              <span className="font-display text-3xl font-semibold">{formatINR(product.price)}</span>
              {product.originalPrice && product.originalPrice > product.price && (
                <>
                  <span className="text-base text-muted-foreground line-through">{formatINR(product.originalPrice)}</span>
                  <span className="text-sm font-semibold text-success">{off}% off</span>
                </>
              )}
            </div>
            <p className="mt-1 text-xs text-muted-foreground">Inclusive of all taxes · Free delivery above ₹499</p>

            <p className="mt-5 text-sm leading-relaxed text-muted-foreground">{product.description}</p>

            <div className="mt-6 flex items-center gap-4">
              <span className="text-sm font-medium">Quantity</span>
              <div className="inline-flex items-center rounded-full border">
                <button onClick={() => setQty(Math.max(1, qty - 1))} className="grid h-9 w-9 place-items-center hover:bg-muted rounded-l-full"><Minus size={14} /></button>
                <span className="w-10 text-center text-sm font-medium">{qty}</span>
                <button onClick={() => setQty(qty + 1)} className="grid h-9 w-9 place-items-center hover:bg-muted rounded-r-full"><Plus size={14} /></button>
              </div>
            </div>

            <div className="mt-6 flex flex-wrap gap-3">
              <Button size="lg" onClick={() => add(product, qty)} className="flex-1 min-w-[180px]" disabled={product.stock <= 0}>
                <ShoppingBag size={16} className="mr-1.5" /> Add to Cart
              </Button>
              <Button size="lg" variant="default" className="flex-1 min-w-[180px] bg-discount hover:bg-discount/90 text-white"
                disabled={product.stock <= 0}
                onClick={() => { add(product, qty); navigate('/checkout'); }}>
                Buy Now
              </Button>
              <Button size="lg" variant="outline" onClick={() => toggle(product)}>
                <Heart size={16} className={cn(has(product.id) && 'fill-discount text-discount')} />
              </Button>
              <Button size="lg" variant="outline" onClick={() => navigate(`/compare?ids=${product.id}`)}>
                Compare
              </Button>
            </div>

            <div className="mt-6 grid grid-cols-3 gap-2 pt-6 border-t">
              {[
                { icon: Truck, l: 'Free delivery' },
                { icon: RotateCcw, l: '7-day returns' },
                { icon: Shield, l: '1Y warranty' },
              ].map(({ icon: Icon, l }) => (
                <div key={l} className="flex flex-col items-center text-center text-xs gap-1.5 text-muted-foreground">
                  <Icon size={18} />{l}
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Tabs */}
        <Tabs defaultValue="desc" className="mt-14">
          <TabsList>
            <TabsTrigger value="desc">Description</TabsTrigger>
            <TabsTrigger value="specs">Specifications</TabsTrigger>
            <TabsTrigger value="reviews">Reviews ({product.reviewCount})</TabsTrigger>
          </TabsList>
          <TabsContent value="desc" className="mt-6 prose prose-sm max-w-none text-muted-foreground">
            <p>{product.description}</p>
            <p>Crafted with attention to detail and built to last. Backed by our hassle-free returns and dedicated support team.</p>
          </TabsContent>
          <TabsContent value="specs" className="mt-6">
            {product.specs ? (
              <table className="w-full max-w-2xl text-sm">
                <tbody>
                  {Object.entries(product.specs).map(([k, v]) => (
                    <tr key={k} className="border-b">
                      <td className="py-3 text-muted-foreground w-1/3">{k}</td>
                      <td className="py-3 font-medium">{v}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : <p className="text-sm text-muted-foreground">No specifications available.</p>}
          </TabsContent>
          <TabsContent value="reviews" className="mt-6">
            <div className="grid md:grid-cols-[300px_1fr] gap-8">
              <div className="rounded-2xl border bg-card p-6 h-fit">
                <div className="font-display text-4xl font-semibold">{product.rating.toFixed(1)}</div>
                <Rating value={product.rating} size={16} />
                <div className="text-xs text-muted-foreground mt-1">{product.reviewCount} ratings</div>
                <div className="mt-4 space-y-2">
                  {[5, 4, 3, 2, 1].map(s => (
                    <div key={s} className="flex items-center gap-2 text-xs">
                      <span className="w-3">{s}</span>
                      <div className="flex-1 h-1.5 bg-muted rounded-full overflow-hidden">
                        <div className="h-full bg-rating" style={{ width: `${[70, 22, 5, 2, 1][5 - s]}%` }} />
                      </div>
                      <span className="w-8 text-muted-foreground text-right">{[70, 22, 5, 2, 1][5 - s]}%</span>
                    </div>
                  ))}
                </div>
              </div>
              <div className="space-y-5">
                {reviews.map(r => (
                  <div key={r.id} className="rounded-2xl border bg-card p-5">
                    <div className="flex items-center gap-3">
                      <div className="grid h-10 w-10 place-items-center rounded-full bg-accent text-accent-foreground font-medium">{r.user[0]}</div>
                      <div>
                        <div className="text-sm font-medium">{r.user} {r.verified && <span className="ml-1 text-[10px] text-success">✓ Verified</span>}</div>
                        <div className="text-xs text-muted-foreground">{r.date}</div>
                      </div>
                      <Rating value={r.rating} className="ml-auto" />
                    </div>
                    <h4 className="mt-3 font-medium text-sm">{r.title}</h4>
                    <p className="mt-1 text-sm text-muted-foreground">{r.comment}</p>
                  </div>
                ))}
              </div>
            </div>
          </TabsContent>
        </Tabs>

        {/* Related */}
        {related.length > 0 && (
          <section className="mt-16">
            <div className="flex items-end justify-between mb-6">
              <h2 className="font-display text-2xl font-semibold">You might also like</h2>
              <Link to="/products" className="text-sm text-primary hover:underline">View all</Link>
            </div>
            <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 lg:gap-6">
              {related.map(p => <ProductCard key={p.id} product={p} />)}
            </div>
          </section>
        )}
      </div>
    </MainLayout>
  );
}
