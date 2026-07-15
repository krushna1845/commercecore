import { api } from './client';

export interface UserOrder {
  id: number;
  status: string;
  totalAmount: number;
  createdAt: string;
  items: Array<{
    productId: number;
    productName: string;
    quantity: number;
    price: number;
    imageUrl?: string;
  }>;
}

export interface UserAddress {
  id: number;
  street: string;
  city: string;
  zipCode: string;
  phone: string;
  isDefault: boolean;
}

export const userApi = {
  me: () => api.get<{ username: string; role: string }>('/auth/me'),
  orders: () => api.get<UserOrder[]>('/orders/my'),
  changePassword: (oldPassword: string, newPassword: string) =>
    api.post('/user/change-password', { oldPassword, newPassword }),

  addresses: () => api.get<UserAddress[]>('/api/addresses'),
  createAddress: (a: Omit<UserAddress, 'id'>) => api.post<UserAddress>('/api/addresses', a),
  updateAddress: (id: number, a: Omit<UserAddress, 'id'>) => api.put<UserAddress>(`/api/addresses/${id}`, a),
  deleteAddress: (id: number) => api.del<void>(`/api/addresses/${id}`),
  setDefaultAddress: (id: number) => api.put<UserAddress>(`/api/addresses/${id}/set-default`),
};
