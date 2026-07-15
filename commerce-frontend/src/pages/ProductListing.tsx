import { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { ChevronDown, SlidersHorizontal, X } from 'lucide-react';
import { MainLayout } from '@/layouts/MainLayout';
import { ProductCard } from '@/components/ProductCard';
import { ProductCardSkeleton } from '@/components/ProductCardSkeleton';
import { Breadcrumbs } from '@/components/Breadcrumbs';
import { EmptyState } from '@/components/EmptyState';
import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import { Slider } from '@/components/ui/slider';
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetTrigger } from '@/components/ui/sheet';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Rating } from '@/components/Rating';
import { productApi } from '@/services/productApi';
import { categoryApi } from '@/services/categoryApi';
import type { Product, Category } from '@/types';

function FiltersPanel({ state, set, categories, brands }: { state: any; set: any; categories: Category[]; brands: string[] }) {
  return (
    <div className="space-y-7">
      <div>
        <h4 className="text-sm font-semibold mb-3">Category</h4>
        <div className="space-y-2">
          {categories.map(c => (
            <label key={c.id} className="flex items-center gap-2 text-sm cursor-pointer">
              <Checkbox checked={state.cats.includes(c.id)} onCheckedChange={() => set.toggleCat(c.id)} />
              <span className="capitalize">{c.name}</span>
              <span className="ml-auto text-xs text-muted-foreground">{c.count}</span>
            </label>
          ))}
        </div>
      </div>
      <div>
        <h4 className="text-sm font-semibold mb-3">Price range</h4>
        <Slider value={state.price} onValueChange={set.setPrice} min={0} max={20000} step={500} />
        <div className="mt-3 flex justify-between text-xs text-muted-foreground">
          <span>₹{state.price[0]}</span><span>₹{state.price[1]}</span>
        </div>
      </div>
      <div>
        <h4 className="text-sm font-semibold mb-3">Brand</h4>
        <div className="space-y-2 max-h-44 overflow-y-auto pr-2">
          {brands.map(b => (
            <label key={b} className="flex items-center gap-2 text-sm cursor-pointer">
              <Checkbox checked={state.brands.includes(b)} onCheckedChange={() => set.toggleBrand(b)} />
              <span>{b}</span>
            </label>
          ))}
        </div>
      </div>
      <div>
        <h4 className="text-sm font-semibold mb-3">Rating</h4>
        <div className="space-y-2">
          {[4, 3, 2].map(r => (
            <label key={r} className="flex items-center gap-2 text-sm cursor-pointer">
              <Checkbox checked={state.minRating === r} onCheckedChange={() => set.setMinRating(state.minRating === r ? 0 : r)} />
              <Rating value={r} /> <span className="text-xs text-muted-foreground">& up</span>
            </label>
          ))}
        </div>
      </div>
      <Button variant="outline" className="w-full" onClick={set.clear}>Clear all</Button>
    </div>
  );
}

export default function ProductListing() {
  const [params] = useSearchParams();
  const q = params.get('q') ?? '';
  const initialCat = params.get('category');

  const [cats, setCats] = useState<string[]>(initialCat ? [initialCat] : []);
  const [brandsSel, setBrandsSel] = useState<string[]>([]);
  const [price, setPrice] = useState<number[]>([0, 20000]);
  const [minRating, setMinRating] = useState(0);
  const [sort, setSort] = useState('popular');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [page, setPage] = useState(1);
  const PAGE_SIZE = 8;

  const brands = useMemo(() => Array.from(new Set(products.map(p => p.brand))), [products]);

  const filtered = useMemo(() => {
    let list = products.slice();
    if (q) list = list.filter(p => (p.name + p.brand + p.category).toLowerCase().includes(q.toLowerCase()));
    if (cats.length) list = list.filter(p =>
      cats.some(category => category.toLowerCase() === p.category.toLowerCase())
    );
    if (brandsSel.length) list = list.filter(p => brandsSel.includes(p.brand));
    list = list.filter(p => p.price >= price[0] && p.price <= price[1]);
    if (minRating) list = list.filter(p => p.rating >= minRating);
    if (sort === 'price-asc') list.sort((a, b) => a.price - b.price);
    if (sort === 'price-desc') list.sort((a, b) => b.price - a.price);
    if (sort === 'newest') list.sort((a, b) => (a.badge === 'new' ? -1 : 1));
    if (sort === 'popular') list.sort((a, b) => b.reviewCount - a.reviewCount);
    return list;
  }, [q, cats, brandsSel, price, minRating, sort, products]);

  const paged = filtered.slice(0, page * PAGE_SIZE);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError(null);

    Promise.all([
      q ? productApi.search(q) : productApi.list(),
      categoryApi.list(),
    ])
      .then(([productsResponse, categoriesResponse]) => {
        if (!active) return;
        setProducts(
          productsResponse.map(product => ({
            ...product,
            id: String((product as any).id),
            image: (product as any).imageUrl || (product as any).image || '',
            category: (product as any).category?.name || (product as any).category || 'Electronics',
            brand: (product as any).brand || 'Premium',
            rating: (product as any).rating || 4.5,
            reviewCount: (product as any).reviewCount || 12,
            stock: (product as any).stockQuantity !== undefined ? (product as any).stockQuantity : ((product as any).stock || 0),
          })) as Product[]
        );
        setCategories(
          categoriesResponse.map(category => ({
            id: category.name,
            name: category.name,
            icon: 'list',
            color: 'bg-slate-500',
            count: 0,
          }))
        );
      })
      .catch(err => {
        if (!active) return;
        setError(err instanceof Error ? err.message : 'Failed to load products');
      })
      .finally(() => {
        if (!active) return;
        setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [q]);

  const filterState = { cats, brands: brandsSel, price, minRating };
  const setters = {
    toggleCat: (id: string) => setCats(p => p.includes(id) ? p.filter(x => x !== id) : [...p, id]),
    toggleBrand: (b: string) => setBrandsSel(p => p.includes(b) ? p.filter(x => x !== b) : [...p, b]),
    setPrice, setMinRating,
    clear: () => { setCats([]); setBrandsSel([]); setPrice([0, 20000]); setMinRating(0); },
  };

  const activeCount = cats.length + brandsSel.length + (minRating ? 1 : 0);

  return (
    <MainLayout>
      <div className="container-x py-6">
        <Breadcrumbs items={[{ label: 'Home', to: '/' }, { label: 'Products' }]} />
        <div className="mt-4 flex items-end justify-between flex-wrap gap-3">
          <div>
            <h1 className="font-display text-2xl sm:text-3xl font-semibold">{q ? `Results for "${q}"` : 'All Products'}</h1>
            <p className="text-sm text-muted-foreground mt-1">{filtered.length} items</p>
          </div>
          <div className="flex items-center gap-2">
            <Sheet>
              <SheetTrigger asChild>
                <Button variant="outline" size="sm" className="lg:hidden">
                  <SlidersHorizontal size={14} className="mr-1.5" /> Filters
                  {activeCount > 0 && <span className="ml-1.5 grid h-4 min-w-4 place-items-center rounded-full bg-primary text-primary-foreground text-[10px] px-1">{activeCount}</span>}
                </Button>
              </SheetTrigger>
              <SheetContent side="left" className="w-80">
                <SheetHeader><SheetTitle>Filters</SheetTitle></SheetHeader>
                <div className="mt-6"><FiltersPanel state={filterState} set={setters} categories={categories} brands={brands} /></div>
              </SheetContent>
            </Sheet>
            <Select value={sort} onValueChange={setSort}>
              <SelectTrigger className="w-[180px]"><SelectValue /></SelectTrigger>
              <SelectContent>
                <SelectItem value="popular">Popularity</SelectItem>
                <SelectItem value="newest">Newest</SelectItem>
                <SelectItem value="price-asc">Price: Low to High</SelectItem>
                <SelectItem value="price-desc">Price: High to Low</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        <div className="mt-6 grid lg:grid-cols-[260px_1fr] gap-8">
          <aside className="hidden lg:block">
            <div className="rounded-2xl border bg-card p-5 sticky top-32"><FiltersPanel state={filterState} set={setters} categories={categories} brands={brands} /></div>
          </aside>

          <div>
            {loading ? (
              <div className="grid grid-cols-2 md:grid-cols-3 gap-4 lg:gap-6">
                {Array.from({ length: 6 }).map((_, i) => <ProductCardSkeleton key={i} />)}
              </div>
            ) : error ? (
              <EmptyState title="Unable to load products" description={error} action={{ label: 'Retry', onClick: () => window.location.reload() }} />
            ) : filtered.length === 0 ? (
              <EmptyState title="No products found" description="Try adjusting your filters or search term." action={{ label: 'Clear filters', onClick: setters.clear }} />
            ) : (
              <>
                <div className="grid grid-cols-2 md:grid-cols-3 gap-4 lg:gap-6">
                  {paged.map(p => <ProductCard key={p.id} product={p} />)}
                </div>
                {paged.length < filtered.length && (
                  <div className="mt-10 flex justify-center">
                    <Button variant="outline" onClick={() => setPage(p => p + 1)}>Load more <ChevronDown size={16} className="ml-1" /></Button>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </div>
    </MainLayout>
  );
}
