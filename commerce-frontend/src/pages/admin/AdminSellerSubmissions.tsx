import { useEffect, useState } from 'react';
import { Check, FileCheck, Image as ImageIcon, Loader2, Package, X } from 'lucide-react';
import { AdminLayout } from '@/layouts/AdminLayout';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { adminApi } from '@/services/adminApi';
import { formatINR } from '@/utils/format';
import { toast } from 'sonner';

interface PendingProduct {
  id: number;
  name: string;
  description: string;
  price: number;
  imageUrl: string;
  stockQuantity: number;
  seller: {
    id: number;
    username: string;
  };
  approvalStatus: string;
}

export default function AdminSellerSubmissions() {
  const [products, setProducts] = useState<PendingProduct[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    adminApi.pendingProducts()
      .then((data: PendingProduct[]) => setProducts(data))
      .catch(err => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  const handleApprove = async (id: number) => {
    try {
      await adminApi.approveProduct(id);
      setProducts(products.filter(p => p.id !== id));
      toast.success('Product approved successfully');
    } catch (err: any) {
      toast.error(err.message || 'Failed to approve product');
    }
  };

  const handleReject = async (id: number) => {
    try {
      await adminApi.rejectProduct(id);
      setProducts(products.filter(p => p.id !== id));
      toast.success('Product rejected');
    } catch (err: any) {
      toast.error(err.message || 'Failed to reject product');
    }
  };

  if (loading) return (
    <AdminLayout title="Seller Submissions">
      <div className="flex items-center justify-center h-64"><Loader2 className="animate-spin text-primary" size={32} /></div>
    </AdminLayout>
  );

  if (error) return (
    <AdminLayout title="Seller Submissions">
      <div className="rounded-xl bg-destructive/10 text-destructive p-6 text-sm">{error}</div>
    </AdminLayout>
  );

  return (
    <AdminLayout title="Seller Submissions">
      <div className="rounded-2xl border bg-card overflow-hidden">
        <div className="p-5 flex items-center justify-between">
          <div>
            <h3 className="font-semibold">Pending Product Approvals</h3>
            <p className="text-xs text-muted-foreground mt-1">Review and approve seller-submitted products</p>
          </div>
          <Badge variant="outline" className="bg-primary/10 text-primary border-primary/20">
            {products.length} pending
          </Badge>
        </div>

        {products.length === 0 ? (
          <div className="p-12 text-center">
            <FileCheck className="mx-auto h-12 w-12 text-muted-foreground/50 mb-3" />
            <p className="text-sm text-muted-foreground">No pending submissions</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-muted/50 text-xs uppercase tracking-wider text-muted-foreground">
                <tr>
                  <th className="text-left p-3 px-5">Product</th>
                  <th className="text-left p-3">Seller</th>
                  <th className="text-left p-3">Price</th>
                  <th className="text-left p-3">Stock</th>
                  <th className="text-center p-3">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y">
                {products.map(product => (
                  <tr key={product.id} className="hover:bg-muted/30">
                    <td className="p-3 px-5">
                      <div className="flex items-center gap-3">
                        {product.imageUrl ? (
                          <img 
                            src={product.imageUrl} 
                            alt={product.name}
                            className="h-10 w-10 rounded-lg object-cover bg-muted"
                          />
                        ) : (
                          <div className="h-10 w-10 rounded-lg bg-muted flex items-center justify-center">
                            <ImageIcon size={16} className="text-muted-foreground" />
                          </div>
                        )}
                        <div className="min-w-0">
                          <div className="font-medium truncate">{product.name}</div>
                          <div className="text-xs text-muted-foreground truncate">{product.description}</div>
                        </div>
                      </div>
                    </td>
                    <td className="p-3">
                      <div className="flex items-center gap-2">
                        <div className="h-6 w-6 rounded-full bg-violet-500/10 flex items-center justify-center text-xs font-bold text-violet-600">
                          {product.seller.username.charAt(0).toUpperCase()}
                        </div>
                        <span className="text-xs">{product.seller.username}</span>
                      </div>
                    </td>
                    <td className="p-3 font-semibold">{formatINR(product.price)}</td>
                    <td className="p-3">
                      <Badge variant={product.stockQuantity > 0 ? "outline" : "destructive"} className="text-xs">
                        {product.stockQuantity}
                      </Badge>
                    </td>
                    <td className="p-3">
                      <div className="flex items-center justify-center gap-2">
                        <Button
                          size="sm"
                          variant="ghost"
                          className="h-8 w-8 p-0 text-green-600 hover:text-green-700 hover:bg-green-50"
                          onClick={() => handleApprove(product.id)}
                        >
                          <Check size={16} />
                        </Button>
                        <Button
                          size="sm"
                          variant="ghost"
                          className="h-8 w-8 p-0 text-red-600 hover:text-red-700 hover:bg-red-50"
                          onClick={() => handleReject(product.id)}
                        >
                          <X size={16} />
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </AdminLayout>
  );
}
