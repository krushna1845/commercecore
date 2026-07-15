import { useEffect, useState } from 'react';
import { AdminLayout } from '@/layouts/AdminLayout';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { adminApi } from '@/services/adminApi';
import { formatINR } from '@/utils/format';
import { Loader2, Save } from 'lucide-react';
import { toast } from 'sonner';

interface Product {
  id: number;
  name: string;
  price: number;
  imageUrl?: string;
  stockQuantity: number;
  description?: string;
  category?: { id: number; name: string };
}

export default function AdminInventory() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [updatingId, setUpdatingId] = useState<number | null>(null);
  const [tempStocks, setTempStocks] = useState<Record<number, number>>({});

  const load = () => {
    setLoading(true);
    adminApi.products()
      .then(res => {
        setProducts(res as Product[]);
        // Initialize temp stocks state
        const stocks: Record<number, number> = {};
        res.forEach((p: any) => {
          stocks[p.id] = p.stockQuantity;
        });
        setTempStocks(stocks);
      })
      .catch(err => toast.error(err.message || 'Failed to load inventory'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleStockChange = (id: number, val: string) => {
    const num = parseInt(val);
    setTempStocks(prev => ({
      ...prev,
      [id]: isNaN(num) ? 0 : Math.max(0, num),
    }));
  };

  const saveStock = async (p: Product) => {
    const targetStock = tempStocks[p.id];
    if (targetStock === p.stockQuantity) return; // No change

    setUpdatingId(p.id);
    try {
      const payload = {
        name: p.name,
        description: p.description || '',
        price: p.price,
        imageUrl: p.imageUrl || '',
        stockQuantity: targetStock,
        categoryId: p.category?.id || null,
      };
      await adminApi.updateProduct(p.id, payload);
      toast.success(`Stock level updated for ${p.name}`);
      load();
    } catch (err: any) {
      toast.error(err.message || 'Failed to update stock level');
    } finally {
      setUpdatingId(null);
    }
  };

  return (
    <AdminLayout title="Inventory">
      <div className="rounded-2xl border bg-card overflow-hidden">
        {loading ? (
          <div className="flex items-center justify-center py-20">
            <Loader2 className="animate-spin text-primary" size={32} />
          </div>
        ) : products.length === 0 ? (
          <div className="text-center py-12 text-muted-foreground text-sm">
            No products found.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-muted/40 text-xs uppercase tracking-wider text-muted-foreground">
                <tr>
                  <th className="text-left p-3 px-5">Product</th>
                  <th className="text-left p-3">SKU</th>
                  <th className="text-left p-3">Stock Level</th>
                  <th className="text-left p-3">Status</th>
                  <th className="text-right p-3 px-5">Actions / Value</th>
                </tr>
              </thead>
              <tbody className="divide-y">
                {products.map(p => {
                  const currentTempStock = tempStocks[p.id] ?? p.stockQuantity;
                  const hasChanged = currentTempStock !== p.stockQuantity;
                  const isUpdating = updatingId === p.id;

                  return (
                    <tr key={p.id} className="hover:bg-muted/30">
                      <td className="p-3 px-5">
                        <div className="flex items-center gap-3">
                          {p.imageUrl ? (
                            <img src={p.imageUrl} className="h-10 w-10 rounded-lg object-cover bg-muted" alt="" />
                          ) : (
                            <div className="h-10 w-10 rounded-lg bg-muted" />
                          )}
                          <div className="font-medium line-clamp-1">{p.name}</div>
                        </div>
                      </td>
                      <td className="p-3 text-muted-foreground font-mono text-xs">SKU-{p.id}</td>
                      <td className="p-3">
                        <div className="flex items-center gap-2">
                          <Input 
                            type="number" 
                            value={currentTempStock} 
                            onChange={e => handleStockChange(p.id, e.target.value)}
                            onBlur={() => saveStock(p)}
                            onKeyDown={e => e.key === 'Enter' && saveStock(p)}
                            className="h-8 w-20 text-center" 
                            disabled={isUpdating}
                          />
                          {hasChanged && !isUpdating && (
                            <button onClick={() => saveStock(p)} className="text-primary hover:text-primary/80" title="Save changes">
                              <Save size={16} />
                            </button>
                          )}
                          {isUpdating && <Loader2 className="animate-spin text-primary" size={14} />}
                        </div>
                      </td>
                      <td className="p-3">
                        {p.stockQuantity > 10 ? (
                          <Badge className="bg-success/15 text-success border-0">Healthy</Badge>
                        ) : p.stockQuantity > 0 ? (
                          <Badge className="bg-warning/15 text-warning border-0">Restock soon</Badge>
                        ) : (
                          <Badge className="bg-destructive/15 text-destructive border-0">Out of stock</Badge>
                        )}
                      </td>
                      <td className="p-3 px-5 text-right font-semibold">
                        {formatINR(p.stockQuantity * p.price)}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </AdminLayout>
  );
}
