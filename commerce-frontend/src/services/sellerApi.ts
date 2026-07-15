import { api, BASE_URL } from './client';

const SELLER = '/api/seller';

export interface SellerProduct {
  id: number;
  name: string;
  description?: string;
  price: number;
  originalPrice?: number;
  imageUrl?: string;
  stockQuantity: number;
  brand?: string;
  warranty?: string;
  returnPolicy?: string;
  categoryId?: number;
}

export interface SellerAnalytics {
  revenue: { totalRevenue: number; currentMonthRevenue: number; pendingPayout: number; availablePayout: number };
  orders: { totalOrders: number; pendingOrders: number; shippedOrders: number; deliveredOrders: number; cancelledOrders: number };
  products: { totalProducts: number; activeProducts: number; inactiveProducts: number; itemsSold: number };
  inventory: { totalInventory: number; lowStock: number };
  recentOrders: SellerOrderItem[];
  topProducts: Array<{ productId: number; productName: string; imageUrl: string; price: number; stockQuantity: number; unitsSold: number; totalRevenue: number }>;
  monthlyRevenue: Array<{ month: string; value: number; count: number }>;
  lowStockProducts: Array<{ productId: number; productName: string; stockQuantity: number; imageUrl: string }>;
}

export interface SellerOrderItem {
  orderItemId?: number;
  orderId: number;
  orderNumber: string;
  productName?: string;
  productImageUrl?: string;
  customerName: string;
  totalAmount: number;
  quantity?: number;
  lineTotal?: number;
  status: string;
  createdAt: string;
}

export interface ProductFormData {
  name: string;
  description?: string;
  price: number;
  originalPrice?: number;
  imageUrl?: string;
  stockQuantity: number;
  brand?: string;
  warranty?: string;
  returnPolicy?: string;
  categoryId?: number;
}

function getAuthToken() {
  return localStorage.getItem('authToken');
}

export const sellerApi = {
  dashboard: () => api.get<SellerAnalytics>(`${SELLER}/dashboard`),
  products: () => api.get<SellerProduct[]>(`${SELLER}/products`),
  createProduct: (p: ProductFormData) => api.post<SellerProduct>(`${SELLER}/products`, p),
  updateProduct: (id: number, p: ProductFormData) => api.put<SellerProduct>(`${SELLER}/products/${id}`, p),
  deleteProduct: (id: number) => api.del<void>(`${SELLER}/products/${id}`),
  orders: () => api.get<SellerOrderItem[]>(`${SELLER}/orders`),
  updateOrderStatus: (itemId: number, status: string) =>
    api.put<SellerOrderItem>(`${SELLER}/orders/items/${itemId}/status`, { status }),
  uploadImage: async (file: File): Promise<string> => {
    const form = new FormData();
    form.append('file', file);
    const token = getAuthToken();
    const res = await fetch(`${BASE_URL}${SELLER}/upload`, {
      method: 'POST',
      headers: token ? { Authorization: `Bearer ${token}` } : {},
      body: form,
    });
    if (!res.ok) {
      const text = await res.text();
      throw new Error(text || 'Upload failed');
    }
    const data = await res.json();
    return data.url;
  },
};

export function stockStatus(qty: number): 'In Stock' | 'Low Stock' | 'Out of Stock' {
  if (qty === 0) return 'Out of Stock';
  if (qty < 5) return 'Low Stock';
  return 'In Stock';
}
