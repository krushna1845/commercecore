import { api } from './client';

export const orderApi = {
  list:   () => api.get('/api/orders/user'),
  create: () => api.post('/payment/checkout'),
};
