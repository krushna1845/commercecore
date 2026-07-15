import { api } from './client';

const ADMIN = '/api/admin';

export interface AdminStats {
  totalRevenue: number;
  totalOrders: number;
  totalProducts: number;
  totalUsers: number;
  recentOrders: AdminOrder[];
}

export interface AdminOrder {
  id: number;
  username: string;
  status: string;
  totalAmount: number;
  createdAt: string;
  itemCount: number;
}

export interface AdminUser {
  id: number;
  username: string;
  role: string;
  createdAt?: string;
  orderCount?: number;
}

export const adminApi = {
  // Dashboard
  stats:          () => api.get<AdminStats>(`${ADMIN}/dashboard`),
  analytics:      () => api.get<any>(`${ADMIN}/analytics`),

  // Products
  products:       () => api.get<any[]>(`${ADMIN}/products`),
  createProduct:  (p: any) => api.post<any>(`${ADMIN}/products`, p),
  updateProduct:  (id: number, p: any) => api.put<any>(`${ADMIN}/products/${id}`, p),
  deleteProduct:  (id: number) => api.del<void>(`${ADMIN}/products/${id}`),
  pendingProducts: () => api.get<any[]>(`${ADMIN}/products/pending`),
  approveProduct: (id: number) => api.put<any>(`${ADMIN}/products/${id}/approve`),
  rejectProduct: (id: number) => api.put<any>(`${ADMIN}/products/${id}/reject`),

  // Categories
  categories:     () => api.get<any[]>('/api/categories'),
  createCategory: (p: any) => api.post<any>(`${ADMIN}/categories`, p),
  updateCategory: (id: number, p: any) => api.put<any>(`${ADMIN}/categories/${id}`, p),
  deleteCategory: (id: number) => api.del<void>(`${ADMIN}/categories/${id}`),

  // Orders
  orders:         () => api.get<AdminOrder[]>(`${ADMIN}/orders`),
  updateOrderStatus: (id: number, status: string) => api.put<any>(`${ADMIN}/orders/${id}/status`, { status }),

  // Users
  users:          () => api.get<AdminUser[]>(`${ADMIN}/users`),
};
