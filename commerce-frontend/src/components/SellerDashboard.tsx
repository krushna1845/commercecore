import { useState, useEffect, useCallback } from 'react';
import {
  DollarSign, ShoppingCart, Package, TrendingUp, AlertTriangle,
  Plus, Edit, Trash2, Store
} from 'lucide-react';
import { Button } from './ui/button';
import { Card } from './ui/card';
import { Input } from './ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Badge } from './ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from './ui/table';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './ui/tabs';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from './ui/dialog';
import { ImageUploader } from './ImageUploader';
import { sellerApi, stockStatus, type SellerAnalytics, type SellerProduct, type SellerOrderItem } from '@/services/sellerApi';
import { categoryApi } from '@/services/categoryApi';
import type { Category } from '@/types';
import { toast } from 'sonner';

const emptyForm = {
  name: '', description: '', price: '', originalPrice: '', imageUrl: '',
  stockQuantity: '', brand: '', warranty: '', returnPolicy: '', categoryId: '',
};

type FormData = typeof emptyForm;

interface ProductFormProps {
  formData: FormData;
  setFormData: React.Dispatch<React.SetStateAction<FormData>>;
  categories: Category[];
  onSave: () => void;
  onCancel: () => void;
  label: string;
}

function ProductForm({ formData, setFormData, categories, onSave, onCancel, label }: ProductFormProps) {
  const update = (field: keyof FormData, value: string) =>
    setFormData(prev => ({ ...prev, [field]: value }));

  return (
    <div className="space-y-4 py-4">
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="text-sm font-medium">Product Name *</label>
          <Input className="mt-1" value={formData.name} onChange={e => update('name', e.target.value)} />
        </div>
        <div>
          <label className="text-sm font-medium">Brand</label>
          <Input className="mt-1" value={formData.brand} onChange={e => update('brand', e.target.value)} />
        </div>
      </div>
      <div>
        <label className="text-sm font-medium">Category</label>
        <Select value={formData.categoryId} onValueChange={v => update('categoryId', v)}>
          <SelectTrigger className="mt-1"><SelectValue placeholder="Select category" /></SelectTrigger>
          <SelectContent>
            {categories.map(c => <SelectItem key={c.id} value={String(c.id)}>{c.name}</SelectItem>)}
          </SelectContent>
        </Select>
      </div>
      <div>
        <label className="text-sm font-medium">Description</label>
        <Input className="mt-1" value={formData.description} onChange={e => update('description', e.target.value)} />
      </div>
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="text-sm font-medium">Price *</label>
          <Input type="number" step="0.01" className="mt-1" value={formData.price} onChange={e => update('price', e.target.value)} />
        </div>
        <div>
          <label className="text-sm font-medium">Original Price</label>
          <Input type="number" step="0.01" className="mt-1" value={formData.originalPrice} onChange={e => update('originalPrice', e.target.value)} />
        </div>
      </div>
      <div>
        <label className="text-sm font-medium">Product Image</label>
        <div className="mt-1">
          <ImageUploader
            value={formData.imageUrl}
            onChange={url => update('imageUrl', url)}
            onUpload={sellerApi.uploadImage}
          />
        </div>
      </div>
      <div>
        <label className="text-sm font-medium">Stock Quantity *</label>
        <Input type="number" className="mt-1" value={formData.stockQuantity} onChange={e => update('stockQuantity', e.target.value)} />
      </div>
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="text-sm font-medium">Warranty</label>
          <Input className="mt-1" value={formData.warranty} onChange={e => update('warranty', e.target.value)} />
        </div>
        <div>
          <label className="text-sm font-medium">Return Policy</label>
          <Input className="mt-1" value={formData.returnPolicy} onChange={e => update('returnPolicy', e.target.value)} />
        </div>
      </div>
      <div className="flex gap-4 pt-4">
        <Button className="flex-1" onClick={onSave} disabled={!formData.name || !formData.price || !formData.stockQuantity}>{label}</Button>
        <Button variant="outline" className="flex-1" onClick={onCancel}>Cancel</Button>
      </div>
    </div>
  );
}

export function SellerDashboard() {
  const [analytics, setAnalytics] = useState<SellerAnalytics | null>(null);
  const [products, setProducts] = useState<SellerProduct[]>([]);
  const [orders, setOrders] = useState<SellerOrderItem[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('overview');
  const [searchQuery, setSearchQuery] = useState('');
  const [isAddOpen, setIsAddOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<SellerProduct | null>(null);
  const [formData, setFormData] = useState(emptyForm);

  const loadAll = useCallback(async () => {
    setIsLoading(true);
    try {
      const [dash, prods, ords, cats] = await Promise.all([
        sellerApi.dashboard(),
        sellerApi.products(),
        sellerApi.orders(),
        categoryApi.list(),
      ]);
      setAnalytics(dash);
      setProducts(prods);
      setOrders(ords);
      setCategories(cats);
    } catch {
      toast.error('Failed to load seller data');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => { loadAll(); }, [loadAll]);

  const buildPayload = () => ({
    name: formData.name,
    description: formData.description,
    price: parseFloat(formData.price),
    originalPrice: parseFloat(formData.originalPrice) || parseFloat(formData.price),
    imageUrl: formData.imageUrl,
    stockQuantity: parseInt(formData.stockQuantity),
    brand: formData.brand,
    warranty: formData.warranty,
    returnPolicy: formData.returnPolicy,
    categoryId: formData.categoryId ? parseInt(formData.categoryId) : undefined,
  });

  const handleAddProduct = async () => {
    try {
      await sellerApi.createProduct(buildPayload());
      toast.success('Product added');
      setIsAddOpen(false);
      resetForm();
      loadAll();
    } catch {
      toast.error('Failed to add product');
    }
  };

  const handleEditProduct = async () => {
    if (!editingProduct) return;
    try {
      await sellerApi.updateProduct(editingProduct.id, buildPayload());
      toast.success('Product updated');
      setIsEditOpen(false);
      setEditingProduct(null);
      resetForm();
      loadAll();
    } catch {
      toast.error('Failed to update product');
    }
  };

  const handleDeleteProduct = async (id: number) => {
    if (!confirm('Delete this product?')) return;
    try {
      await sellerApi.deleteProduct(id);
      toast.success('Product deleted');
      loadAll();
    } catch {
      toast.error('Failed to delete product');
    }
  };

  const handleStatusChange = async (itemId: number, status: string) => {
    try {
      await sellerApi.updateOrderStatus(itemId, status);
      toast.success('Order status updated');
      loadAll();
    } catch {
      toast.error('Failed to update status');
    }
  };

  const openEditModal = (product: SellerProduct) => {
    setEditingProduct(product);
    setFormData({
      name: product.name,
      description: product.description || '',
      price: product.price.toString(),
      originalPrice: (product.originalPrice ?? product.price).toString(),
      imageUrl: product.imageUrl || '',
      stockQuantity: product.stockQuantity.toString(),
      brand: product.brand || '',
      warranty: product.warranty || '',
      returnPolicy: product.returnPolicy || '',
      categoryId: product.categoryId?.toString() || '',
    });
    setIsEditOpen(true);
  };

  const resetForm = () => setFormData(emptyForm);

  const statusBadge = (status: string) => {
    const v = status === 'PENDING' ? 'secondary'
      : status === 'SHIPPED' || status === 'DELIVERED' || status === 'PAID' ? 'default'
      : 'destructive';
    return <Badge variant={v}>{status}</Badge>;
  };

  const stockBadge = (qty: number) => {
    const s = stockStatus(qty);
    const v = s === 'Out of Stock' ? 'destructive' : s === 'Low Stock' ? 'secondary' : 'default';
    return <Badge variant={v}>{s} ({qty})</Badge>;
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-primary" />
      </div>
    );
  }

  if (!analytics) {
    return <div className="p-6 text-center text-muted-foreground">Failed to load seller dashboard</div>;
  }

  const filteredProducts = products.filter(p => p.name.toLowerCase().includes(searchQuery.toLowerCase()));

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6">
        <div className="flex items-center gap-3 mb-8">
          <div className="p-3 rounded-xl bg-emerald-100 dark:bg-emerald-900/20">
            <Store className="w-6 h-6 text-emerald-600" />
          </div>
          <div>
            <h1 className="text-3xl font-bold">Seller Portal</h1>
            <p className="text-muted-foreground">Manage inventory, orders & analytics</p>
          </div>
        </div>

        <Tabs value={activeTab} onValueChange={setActiveTab}>
          <TabsList className="mb-8">
            <TabsTrigger value="overview">Overview</TabsTrigger>
            <TabsTrigger value="products">Inventory</TabsTrigger>
            <TabsTrigger value="orders">Orders</TabsTrigger>
            <TabsTrigger value="analytics">Analytics</TabsTrigger>
          </TabsList>

          {/* Overview */}
          <TabsContent value="overview">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
              <Card className="p-6">
                <div className="flex items-center gap-4">
                  <div className="p-3 rounded-full bg-green-100 dark:bg-green-900/20"><DollarSign className="w-6 h-6 text-green-600" /></div>
                  <div>
                    <p className="text-sm text-muted-foreground">Total Revenue</p>
                    <p className="text-2xl font-bold">${analytics.revenue.totalRevenue.toFixed(2)}</p>
                  </div>
                </div>
              </Card>
              <Card className="p-6">
                <div className="flex items-center gap-4">
                  <div className="p-3 rounded-full bg-blue-100 dark:bg-blue-900/20"><ShoppingCart className="w-6 h-6 text-blue-600" /></div>
                  <div>
                    <p className="text-sm text-muted-foreground">Items Sold</p>
                    <p className="text-2xl font-bold">{analytics.products.itemsSold}</p>
                  </div>
                </div>
              </Card>
              <Card className="p-6">
                <div className="flex items-center gap-4">
                  <div className="p-3 rounded-full bg-purple-100 dark:bg-purple-900/20"><Package className="w-6 h-6 text-purple-600" /></div>
                  <div>
                    <p className="text-sm text-muted-foreground">Active Listings</p>
                    <p className="text-2xl font-bold">{analytics.products.activeProducts}</p>
                  </div>
                </div>
              </Card>
              <Card className="p-6">
                <div className="flex items-center gap-4">
                  <div className="p-3 rounded-full bg-yellow-100 dark:bg-yellow-900/20"><TrendingUp className="w-6 h-6 text-yellow-600" /></div>
                  <div>
                    <p className="text-sm text-muted-foreground">This Month</p>
                    <p className="text-2xl font-bold">${analytics.revenue.currentMonthRevenue.toFixed(2)}</p>
                  </div>
                </div>
              </Card>
            </div>

            {analytics.lowStockProducts.length > 0 && (
              <Card className="p-6 mb-8 border-amber-200 dark:border-amber-800">
                <div className="flex items-center gap-2 mb-4">
                  <AlertTriangle className="w-5 h-5 text-amber-600" />
                  <h3 className="text-lg font-semibold">Low Stock Alerts</h3>
                  <Badge variant="destructive">{analytics.inventory.lowStock} items</Badge>
                </div>
                <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-3">
                  {analytics.lowStockProducts.map(p => (
                    <div key={p.productId} className="flex items-center gap-3 p-3 rounded-lg bg-amber-50 dark:bg-amber-950/20">
                      <img src={p.imageUrl || 'https://via.placeholder.com/40'} alt="" className="w-10 h-10 rounded object-cover" />
                      <div className="min-w-0">
                        <p className="text-sm font-medium truncate">{p.productName}</p>
                        <p className="text-xs text-amber-700 dark:text-amber-400">Only {p.stockQuantity} left — restock soon</p>
                      </div>
                    </div>
                  ))}
                </div>
              </Card>
            )}

            <Card className="p-6">
              <h3 className="text-lg font-semibold mb-4">Recent Orders</h3>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Order</TableHead>
                    <TableHead>Product</TableHead>
                    <TableHead>Customer</TableHead>
                    <TableHead>Amount</TableHead>
                    <TableHead>Status</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {analytics.recentOrders.slice(0, 5).map(o => (
                    <TableRow key={o.orderItemId ?? o.orderId}>
                      <TableCell>{o.orderNumber}</TableCell>
                      <TableCell>{o.productName}</TableCell>
                      <TableCell>{o.customerName}</TableCell>
                      <TableCell>${o.totalAmount.toFixed(2)}</TableCell>
                      <TableCell>{statusBadge(o.status)}</TableCell>
                    </TableRow>
                  ))}
                  {analytics.recentOrders.length === 0 && (
                    <TableRow><TableCell colSpan={5} className="text-center text-muted-foreground py-6">No orders yet</TableCell></TableRow>
                  )}
                </TableBody>
              </Table>
            </Card>
          </TabsContent>

          {/* Products */}
          <TabsContent value="products">
            <Card className="p-6">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-lg font-semibold">Your Inventory</h3>
                <Button onClick={() => { resetForm(); setIsAddOpen(true); }}>
                  <Plus className="w-4 h-4 mr-2" /> Add Product
                </Button>
              </div>
              <Input placeholder="Search products..." value={searchQuery} onChange={e => setSearchQuery(e.target.value)} className="max-w-sm mb-6" />
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Product</TableHead>
                    <TableHead>Price</TableHead>
                    <TableHead>Stock Status</TableHead>
                    <TableHead>Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredProducts.map(p => (
                    <TableRow key={p.id}>
                      <TableCell>
                        <div className="flex items-center gap-3">
                          <img src={p.imageUrl || 'https://via.placeholder.com/40'} alt="" className="w-10 h-10 rounded object-cover" />
                          <span className="font-medium">{p.name}</span>
                        </div>
                      </TableCell>
                      <TableCell>${p.price.toFixed(2)}</TableCell>
                      <TableCell>{stockBadge(p.stockQuantity)}</TableCell>
                      <TableCell>
                        <div className="flex gap-2">
                          <Button variant="ghost" size="icon" onClick={() => openEditModal(p)}><Edit className="w-4 h-4" /></Button>
                          <Button variant="ghost" size="icon" onClick={() => handleDeleteProduct(p.id)}><Trash2 className="w-4 h-4 text-destructive" /></Button>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
              {products.length === 0 && (
                <p className="text-center py-8 text-muted-foreground">No products yet. Add your first listing.</p>
              )}
            </Card>
          </TabsContent>

          {/* Orders */}
          <TabsContent value="orders">
            <Card className="p-6">
              <h3 className="text-lg font-semibold mb-6">Order Fulfilment</h3>
              <p className="text-sm text-muted-foreground mb-4">You only see items from orders containing your products.</p>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Order</TableHead>
                    <TableHead>Product</TableHead>
                    <TableHead>Customer</TableHead>
                    <TableHead>Qty</TableHead>
                    <TableHead>Total</TableHead>
                    <TableHead>Status</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {orders.map(o => (
                    <TableRow key={o.orderItemId}>
                      <TableCell className="font-medium">{o.orderNumber}</TableCell>
                      <TableCell>{o.productName}</TableCell>
                      <TableCell>{o.customerName}</TableCell>
                      <TableCell>{o.quantity}</TableCell>
                      <TableCell>${(o.lineTotal ?? o.totalAmount).toFixed(2)}</TableCell>
                      <TableCell>
                        {o.orderItemId && ['PAID', 'SHIPPED'].includes(o.status) ? (
                          <Select value={o.status} onValueChange={v => handleStatusChange(o.orderItemId!, v)}>
                            <SelectTrigger className="w-[130px] h-8"><SelectValue /></SelectTrigger>
                            <SelectContent>
                              <SelectItem value="PAID">PAID</SelectItem>
                              <SelectItem value="SHIPPED">SHIPPED</SelectItem>
                              <SelectItem value="DELIVERED">DELIVERED</SelectItem>
                            </SelectContent>
                          </Select>
                        ) : statusBadge(o.status)}
                      </TableCell>
                    </TableRow>
                  ))}
                  {orders.length === 0 && (
                    <TableRow><TableCell colSpan={6} className="text-center text-muted-foreground py-8">No orders for your products yet</TableCell></TableRow>
                  )}
                </TableBody>
              </Table>
            </Card>
          </TabsContent>

          {/* Analytics */}
          <TabsContent value="analytics">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
              <Card className="p-6">
                <h3 className="text-lg font-semibold mb-4">Sales Metrics</h3>
                <div className="space-y-3">
                  <div className="flex justify-between"><span className="text-sm">Items Sold</span><span className="font-bold">{analytics.products.itemsSold}</span></div>
                  <div className="flex justify-between"><span className="text-sm">Active Listings</span><span className="font-bold">{analytics.products.activeProducts}</span></div>
                  <div className="flex justify-between"><span className="text-sm">Total Listings</span><span className="font-bold">{analytics.products.totalProducts}</span></div>
                  <div className="flex justify-between"><span className="text-sm">Out of Stock</span><span className="font-bold">{analytics.products.inactiveProducts}</span></div>
                </div>
              </Card>
              <Card className="p-6">
                <h3 className="text-lg font-semibold mb-4">Monthly Revenue</h3>
                <div className="h-[200px] flex items-end gap-1">
                  {analytics.monthlyRevenue.map((m, i) => {
                    const max = Math.max(...analytics.monthlyRevenue.map(d => d.value), 1);
                    return (
                      <div key={i} className="flex-1 flex flex-col items-center">
                        <div className="w-full bg-primary rounded-t" style={{ height: `${(m.value / max) * 100}%`, minHeight: m.value > 0 ? 4 : 0 }} />
                        <span className="text-[10px] text-muted-foreground mt-1 truncate w-full text-center">{m.month.split(' ')[0]}</span>
                      </div>
                    );
                  })}
                </div>
              </Card>
            </div>
            <Card className="p-6">
              <h3 className="text-lg font-semibold mb-4">Top Products</h3>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Product</TableHead>
                    <TableHead>Units Sold</TableHead>
                    <TableHead>Revenue</TableHead>
                    <TableHead>Stock</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {analytics.topProducts.map(p => (
                    <TableRow key={p.productId}>
                      <TableCell className="font-medium">{p.productName}</TableCell>
                      <TableCell>{p.unitsSold}</TableCell>
                      <TableCell>${p.totalRevenue.toFixed(2)}</TableCell>
                      <TableCell>{stockBadge(p.stockQuantity)}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </Card>
          </TabsContent>
        </Tabs>

        <Dialog open={isAddOpen} onOpenChange={setIsAddOpen}>
          <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
            <DialogHeader><DialogTitle>Add New Product</DialogTitle></DialogHeader>
            <ProductForm
              formData={formData}
              setFormData={setFormData}
              categories={categories}
              onSave={handleAddProduct}
              onCancel={() => setIsAddOpen(false)}
              label="Add Product"
            />
          </DialogContent>
        </Dialog>

        <Dialog open={isEditOpen} onOpenChange={setIsEditOpen}>
          <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
            <DialogHeader><DialogTitle>Edit Product</DialogTitle></DialogHeader>
            <ProductForm
              formData={formData}
              setFormData={setFormData}
              categories={categories}
              onSave={handleEditProduct}
              onCancel={() => { setIsEditOpen(false); setEditingProduct(null); }}
              label="Update Product"
            />
          </DialogContent>
        </Dialog>
      </div>
    </div>
  );
}
