import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight, Sparkles, Truck, ShieldCheck, Package, Tag, Clock, TrendingUp, Star } from 'lucide-react';
import { motion } from 'framer-motion';
import { MainLayout } from '@/layouts/MainLayout';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { CategoryCard } from '@/components/home/CategoryCard';
import { FloatingActions } from '@/components/home/FloatingActions';
import { LoadingSkeleton } from '@/components/home/LoadingSkeleton';
import { ProductTile } from '@/components/home/ProductTile';
import { SectionHeading } from '@/components/home/SectionHeading';
import { TestimonialCard } from '@/components/home/TestimonialCard';
import { productApi } from '@/services/productApi';
import { categoryApi } from '@/services/categoryApi';
import type { Product, Category } from '@/types';
import { testimonials } from '@/data/products';
import { useAuth } from '@/context/AuthContext';

const HomePage = () => {
  const { user } = useAuth();
  const [loaded, setLoaded] = useState(false);
  const [loading, setLoading] = useState(true);
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);

  useEffect(() => {
    const timeout = window.setTimeout(() => setLoaded(true), 650);
    return () => window.clearTimeout(timeout);
  }, []);

  useEffect(() => {
    setLoading(true);
    Promise.all([productApi.list(), categoryApi.list()])
      .then(([productsResponse, categoriesResponse]) => {
        setProducts(productsResponse.map((product) => ({
          id: String((product as any).id),
          name: product.name,
          description: product.description || '',
          price: product.price,
          originalPrice: (product as any).originalPrice || product.price * 1.2,
          rating: (product as any).rating || 4.5,
          reviewCount: (product as any).reviewCount || 12,
          image: (product as any).imageUrl || (product as any).image || '',
          images: (product as any).images || [(product as any).imageUrl || (product as any).image || ''],
          category: (product as any).category?.name || (typeof (product as any).category === 'string' ? (product as any).category : 'General'),
          brand: product.brand || 'Premium',
          stock: (product as any).stockQuantity !== undefined ? (product as any).stockQuantity : ((product as any).stock || 0),
          badge: (product as any).badge,
          specs: (product as any).specs,
        })));
        setCategories(categoriesResponse.map((category) => ({
          id: String(category.id),
          name: category.name,
          icon: 'list',
          color: 'bg-slate-500',
          count: 0,
        })));
      })
      .catch((err) => {
        console.error(err);
      })
      .finally(() => setLoading(false));
  }, []);

  const flashSale = useMemo(
    () => products.filter((product) => product.badge === 'deal').slice(0, 4),
    [products]
  );

  const trending = useMemo(
    () => products.filter((product) => product.badge === 'trending' || product.badge === 'best-seller').slice(0, 4),
    [products]
  );

  const newArrivals = useMemo(
    () => [...products.filter((product) => product.badge === 'new'), ...products].slice(0, 4),
    [products]
  );

  const heroMainProduct = products[0] || null;
  const heroSmallProducts = [products[1], products[2]].filter(Boolean);
  const recentlyViewed = useMemo(() => products.slice(0, 5), [products]);
  const recommended = useMemo(() => products.slice(0, 4), [products]);

  if (!loaded || loading) {
    return (
      <MainLayout>
        <div className="container-x py-20">
          <LoadingSkeleton />
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      {/* Hero Banner */}
      <section className="relative overflow-hidden bg-gradient-to-r from-blue-600 via-blue-700 to-indigo-800 text-white">
        <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxnIGZpbGw9IiNmZmYiIGZpbGwtb3BhY2l0eT0iMC4wNSI+PHBhdGggZD0iTTM2IDM0djRoLTR2LTRoLTJ2NGgtNHYyaDR2NGgydi00aDR2LTJoLTR2LTR6Ii8+PC9nPjwvZz48L3N2Zz4=')] opacity-10"></div>
        <div className="container-x relative py-16 lg:py-24">
          <div className="grid gap-8 lg:grid-cols-2 lg:items-center">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6 }}
              className="space-y-6"
            >
              <div className="inline-flex items-center gap-2 rounded-full bg-white/20 px-4 py-2 text-sm font-medium backdrop-blur-sm">
                <Tag size={16} />
                <span>Free Shipping on Orders Over ₹999</span>
              </div>
              <h1 className="text-4xl font-bold lg:text-6xl">
                {user ? `Welcome, ${user.username}!` : 'Shop Everything You Need'}
              </h1>
              <p className="text-lg text-blue-100 max-w-xl">
                Discover amazing products from electronics to fashion. Fast delivery, secure payments, and 24/7 support.
              </p>
              <div className="flex flex-wrap gap-4">
                <Link to="/products" className="inline-flex items-center gap-2 rounded-full bg-white px-8 py-3 font-semibold text-blue-700 hover:bg-blue-50 transition-colors">
                  Shop Now <ArrowRight size={18} />
                </Link>
                <Link to="/products?badge=deal" className="inline-flex items-center gap-2 rounded-full border-2 border-white px-8 py-3 font-semibold hover:bg-white/10 transition-colors">
                  View Deals <Sparkles size={18} />
                </Link>
              </div>
              <div className="flex gap-8 pt-4">
                <div>
                  <p className="text-3xl font-bold">40K+</p>
                  <p className="text-sm text-blue-200">Products</p>
                </div>
                <div>
                  <p className="text-3xl font-bold">1.2M+</p>
                  <p className="text-sm text-blue-200">Customers</p>
                </div>
                <div>
                  <p className="text-3xl font-bold">4.8★</p>
                  <p className="text-sm text-blue-200">Rating</p>
                </div>
              </div>
            </motion.div>
            <motion.div
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ duration: 0.6, delay: 0.2 }}
              className="relative hidden lg:block"
            >
              {heroMainProduct && (
                <div className="relative">
                  <div className="absolute -inset-4 bg-gradient-to-r from-yellow-400 to-orange-500 rounded-3xl blur-2xl opacity-30"></div>
                  <img 
                    src={heroMainProduct.image} 
                    alt={heroMainProduct.name}
                    className="relative rounded-3xl shadow-2xl w-full h-[400px] object-cover"
                  />
                  <div className="absolute bottom-4 left-4 right-4 rounded-2xl bg-white/95 backdrop-blur-sm p-4 shadow-lg">
                    <p className="text-xs font-semibold text-blue-600 uppercase tracking-wider">Featured Product</p>
                    <h3 className="text-lg font-bold text-gray-900">{heroMainProduct.name}</h3>
                    <div className="flex items-center gap-2 mt-1">
                      <span className="text-xl font-bold text-gray-900">₹{heroMainProduct.price.toLocaleString()}</span>
                      {heroMainProduct.originalPrice && heroMainProduct.originalPrice > heroMainProduct.price && (
                        <span className="text-sm text-gray-500 line-through">₹{heroMainProduct.originalPrice.toLocaleString()}</span>
                      )}
                    </div>
                  </div>
                </div>
              )}
            </motion.div>
          </div>
        </div>
      </section>

      {/* Sale Banner */}
      <section className="bg-gradient-to-r from-red-600 to-orange-500 text-white py-8">
        <div className="container-x">
          <div className="flex flex-col md:flex-row items-center justify-between gap-4">
            <div className="flex items-center gap-4">
              <div className="rounded-full bg-white/20 p-3">
                <Clock size={24} />
              </div>
              <div>
                <p className="text-sm font-medium text-red-100">Limited Time Offer</p>
                <p className="text-2xl font-bold">Flash Sale - Up to 70% Off</p>
              </div>
            </div>
            <Link to="/products?badge=deal" className="inline-flex items-center gap-2 rounded-full bg-white px-6 py-3 font-semibold text-red-600 hover:bg-red-50 transition-colors">
              Shop Now <ArrowRight size={18} />
            </Link>
          </div>
        </div>
      </section>

      {/* Trust Badges */}
      <section className="border-b bg-gray-50 py-8">
        <div className="container-x">
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
            {[
              { icon: Truck, title: 'Free Shipping', desc: 'On orders over ₹999' },
              { icon: ShieldCheck, title: 'Secure Payment', desc: '100% secure checkout' },
              { icon: Package, title: 'Easy Returns', desc: '30-day return policy' },
              { icon: TrendingUp, title: 'Best Quality', desc: 'Curated products only' },
            ].map((item) => (
              <div key={item.title} className="flex items-center gap-4">
                <div className="rounded-full bg-blue-100 p-3 text-blue-600">
                  <item.icon size={24} />
                </div>
                <div>
                  <p className="font-semibold text-gray-900">{item.title}</p>
                  <p className="text-sm text-gray-600">{item.desc}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="container-x py-16">
        <SectionHeading
          eyebrow="Featured categories"
          title="Shop by Category"
          description="Browse our wide selection of products across different categories."
        />
        <div className="mt-10 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {categories.slice(0, 8).map((category) => (
            <CategoryCard key={category.id} category={category} />
          ))}
        </div>
      </section>

      {products.length > 0 && (
        <>
          <section className="bg-gray-50 py-16">
            <div className="container-x">
              <div className="flex flex-col gap-6 sm:flex-row sm:items-end sm:justify-between">
                <SectionHeading
                  eyebrow="Flash sale"
                  title="Limited-time premium offers"
                  description="Showcase urgency with high-converting deals and premium product placements."
                />
                <Link to="/products?badge=deal" className="inline-flex items-center gap-2 text-sm font-semibold text-primary hover:text-foreground">
                  View all deals <ArrowRight size={16} />
                </Link>
              </div>
              <div className="mt-10 grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
                {flashSale.length > 0 ? flashSale.map((product) => (
                  <ProductTile key={product.id} product={product} />
                )) : products.slice(0, 4).map((product) => (
                  <ProductTile key={product.id} product={product} />
                ))}
              </div>
            </div>
          </section>

          <section className="container-x py-16">
            <SectionHeading
              eyebrow="Trending now"
              title="Products that are resonating right now."
              description="A curated feed of the most-loved, highest-engagement products across our storefront."
            >
              <span className="rounded-full bg-blue-100 px-3 py-1 text-xs text-blue-700">Live today</span>
            </SectionHeading>
            <div className="mt-10 grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
              {trending.length > 0 ? trending.map((product) => (
                <ProductTile key={product.id} product={product} />
              )) : products.slice(0, 4).map((product) => (
                <ProductTile key={product.id} product={product} />
              ))}
            </div>
          </section>

          <section className="bg-gray-50 py-16">
            <div className="container-x">
              <SectionHeading
                eyebrow="Recommended"
                title="Tailored picks for every shopper journey."
                description="Smart curation designed to increase average order value and inspire repeat purchases."
              />
              <div className="mt-10 grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
                {recommended.map((product) => (
                  <ProductTile key={product.id} product={product} />
                ))}
              </div>
            </div>
          </section>

          <section className="container-x py-16">
            <SectionHeading
              eyebrow="New arrivals"
              title="Fresh launches for the modern premium shopper."
              description="Feature the newest drops with strong visuals, pricing clarity, and fast browse paths."
            />
            <div className="mt-10 grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
              {newArrivals.length > 0 ? newArrivals.map((product) => (
                <ProductTile key={product.id} product={product} />
              )) : products.slice(0, 4).map((product) => (
                <ProductTile key={product.id} product={product} />
              ))}
            </div>
          </section>

          <section className="bg-gray-50 py-16">
            <div className="container-x">
              <SectionHeading
                eyebrow="All Products"
                title="Browse our complete collection"
                description="Discover everything we have to offer across all categories."
              />
              <div className="mt-10 grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
                {recentlyViewed.map((product) => (
                  <ProductTile key={product.id} product={product} />
                ))}
              </div>
              <div className="mt-10 text-center">
                <Link to="/products" className="inline-flex items-center gap-2 rounded-full bg-primary px-8 py-3 font-semibold text-white hover:bg-primary/90 transition-colors">
                  View All Products <ArrowRight size={18} />
                </Link>
              </div>
            </div>
          </section>
        </>
      )}

      <section className="bg-gray-50 py-16">
        <div className="container-x">
          <SectionHeading
            eyebrow="Testimonials"
            title="Trusted by industry-leading customers."
            description="Real feedback from buyers who experience the brand, the product, and the premium purchase journey."
          />
          <div className="mt-10 grid gap-6 lg:grid-cols-3">
            {testimonials.map((testimonial) => (
              <TestimonialCard key={testimonial.name} {...testimonial} />
            ))}
          </div>
        </div>
      </section>

      <section className="container-x pb-24">
        <div className="relative overflow-hidden rounded-3xl bg-gradient-to-r from-blue-600 to-indigo-700 p-8 text-white shadow-xl">
          <div className="relative grid gap-8 lg:grid-cols-[1.2fr_0.8fr] lg:items-center">
            <div>
              <p className="text-sm font-semibold uppercase tracking-wider text-blue-200">Stay ahead</p>
              <h2 className="mt-4 max-w-2xl text-3xl font-bold sm:text-4xl">Subscribe for launch alerts, enterprise insights, and exclusive offers.</h2>
              <p className="mt-4 max-w-xl text-sm text-blue-100">Be the first to hear about new collections, curated drops, and premium sales.</p>
            </div>
            <form className="relative flex flex-col gap-3 sm:flex-row" onSubmit={(event) => event.preventDefault()}>
              <Input placeholder="Enter your email" className="min-w-0 bg-white/90 text-gray-900 placeholder:text-gray-500" />
              <Button type="submit" size="lg" className="rounded-full bg-white text-blue-700 hover:bg-blue-50">Subscribe</Button>
            </form>
          </div>
        </div>
      </section>

      <FloatingActions />
    </MainLayout>
  );
};

export default HomePage;
