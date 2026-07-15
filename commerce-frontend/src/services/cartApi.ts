import { api } from './client';

export const cartApi = {
  get:    () => api.get('/cart'),
  add:    (productId: string, quantity: number) => api.post('/cart/add', { productId, quantity }),
  update: (productId: string, quantity: number) => api.post('/cart/add', { productId, quantity }),
  remove: (productId: string) => api.del(`/cart/remove/${productId}`),
};
