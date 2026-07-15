import { api } from './client';

const AUTH_TOKEN_KEY = 'authToken';

export const authApi = {
  login: async (username: string, password: string) => {
    const data = await api.post<{ token: string }>('/auth/login', { username, password });
    localStorage.setItem(AUTH_TOKEN_KEY, data.token);
    return data.token;
  },
  register: (username: string, password: string) => api.post('/auth/register', { username, password }),
  logout: () => {
    localStorage.removeItem(AUTH_TOKEN_KEY);
    return Promise.resolve();
  },
};
