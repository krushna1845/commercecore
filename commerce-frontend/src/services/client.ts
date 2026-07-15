const DEFAULT_API_URL = 'http://localhost:8081';
export const BASE_URL = import.meta.env.VITE_API_URL || DEFAULT_API_URL;

function getAuthToken() {
  return localStorage.getItem('authToken');
}

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const url = `${BASE_URL}${path}`;
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...Object.fromEntries(new Headers(init.headers || {}).entries()),
  };

  const token = getAuthToken();
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(url, {
    ...init,
    headers,
    body: init.body ? JSON.stringify(init.body) : undefined,
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`API request failed: ${response.status} ${response.statusText} - ${errorText}`);
  }

  if (response.status === 204) {
    return {} as T;
  }

  return response.json() as Promise<T>;
}

export const api = {
  get:    <T,>(p: string)            => request<T>(p, { method: 'GET' }),
  post:   <T,>(p: string, body?: any) => request<T>(p, { method: 'POST', body }),
  put:    <T,>(p: string, body?: any) => request<T>(p, { method: 'PUT', body }),
  del:    <T,>(p: string)            => request<T>(p, { method: 'DELETE' }),
};
