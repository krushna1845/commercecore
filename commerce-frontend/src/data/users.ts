import { Address, Order, User } from '@/types';
import { products } from './products';

export const currentUser: User = {
  id: 'u1', name: 'Aarav Mehta', email: 'aarav@example.com', phone: '+91 98765 43210',
  avatar: 'https://images.unsplash.com/photo-1531123897727-8f129e1688ce?w=200',
  role: 'user', joinedAt: '2024-08-01',
};

export const adminUser: User = {
  id: 'a1', name: 'Admin', email: 'admin@shop.io', role: 'admin', joinedAt: '2023-01-01',
};

export const addresses: Address[] = [
  { id: 'ad1', name: 'Aarav Mehta', phone: '+91 98765 43210', line1: '12 Marine Drive, Apt 4B', city: 'Mumbai', state: 'MH', pincode: '400020', type: 'home', isDefault: true },
  { id: 'ad2', name: 'Aarav Mehta', phone: '+91 98765 43210', line1: 'Tower 7, Bandra Kurla Complex', city: 'Mumbai', state: 'MH', pincode: '400051', type: 'work' },
];

export const orders: Order[] = [
  {
    id: 'ORD-100482', date: '2025-04-22', total: 9798, status: 'shipped', paymentMethod: 'UPI',
    address: addresses[0], estimatedDelivery: '2025-05-04',
    items: [{ product: products[0], quantity: 1 }, { product: products[2], quantity: 1 }],
  },
  {
    id: 'ORD-100398', date: '2025-04-08', total: 12499, status: 'delivered', paymentMethod: 'Card',
    address: addresses[0], estimatedDelivery: '2025-04-12',
    items: [{ product: products[1], quantity: 1 }],
  },
  {
    id: 'ORD-100210', date: '2025-03-15', total: 11999, status: 'delivered', paymentMethod: 'COD',
    address: addresses[1], estimatedDelivery: '2025-03-20',
    items: [{ product: products[4], quantity: 1 }],
  },
];

export const adminUsers: User[] = [
  currentUser,
  { id: 'u2', name: 'Sneha Iyer', email: 'sneha@example.com', role: 'user', joinedAt: '2024-09-12' },
  { id: 'u3', name: 'Rahul Khanna', email: 'rahul@example.com', role: 'user', joinedAt: '2025-01-04' },
  { id: 'u4', name: 'Priya Sharma', email: 'priya@example.com', role: 'user', joinedAt: '2025-02-18' },
  adminUser,
];
