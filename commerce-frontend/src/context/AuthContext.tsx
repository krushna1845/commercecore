import { createContext, useContext, useEffect, useState, ReactNode, useCallback } from 'react';

export interface AuthUser {
  username: string;
  role: 'ROLE_ADMIN' | 'ROLE_USER' | 'ROLE_SELLER';
}

interface AuthCtx {
  user: AuthUser | null;
  token: string | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  isSeller: boolean;
  loading: boolean;
  login: (username: string, password: string) => Promise<AuthUser>;
  register: (username: string, password: string) => Promise<void>;
  becomeSeller: () => Promise<AuthUser>;
  logout: () => void;
}

const Ctx = createContext<AuthCtx | null>(null);
const TOKEN_KEY = 'authToken';
const USER_KEY = 'authUser';
const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081';

// Decode JWT payload without verifying signature
function decodeJwt(token: string): Record<string, unknown> | null {
  try {
    const base64Payload = token.split('.')[1];
    const padded = base64Payload.padEnd(base64Payload.length + (4 - base64Payload.length % 4) % 4, '=');
    return JSON.parse(atob(padded));
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem(TOKEN_KEY));
  const [user, setUser] = useState<AuthUser | null>(() => {
    try {
      const saved = localStorage.getItem(USER_KEY);
      return saved ? JSON.parse(saved) : null;
    } catch {
      return null;
    }
  });
  const [loading, setLoading] = useState(false);

  // Rehydrate user from stored token on mount
  useEffect(() => {
    if (token && !user) {
      const payload = decodeJwt(token);
      if (payload && payload.sub) {
        const hydrated: AuthUser = {
          username: payload.sub as string,
          role: (payload.role as AuthUser['role']) || 'ROLE_USER',
        };
        setUser(hydrated);
        localStorage.setItem(USER_KEY, JSON.stringify(hydrated));
      }
    }
  }, [token, user]);

  const login = useCallback(async (username: string, password: string): Promise<AuthUser> => {
    setLoading(true);
    try {
      const res = await fetch(`${BASE_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });
      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || 'Login failed');
      }
      const data: { token: string; username: string; role: string } = await res.json();
      const authUser: AuthUser = {
        username: data.username,
        role: data.role as AuthUser['role'],
      };
      localStorage.setItem(TOKEN_KEY, data.token);
      localStorage.setItem(USER_KEY, JSON.stringify(authUser));
      setToken(data.token);
      setUser(authUser);
      return authUser;
    } finally {
      setLoading(false);
    }
  }, []);

  const register = useCallback(async (username: string, password: string): Promise<void> => {
    setLoading(true);
    try {
      const res = await fetch(`${BASE_URL}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });
      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || 'Registration failed');
      }
    } finally {
      setLoading(false);
    }
  }, []);

  const becomeSeller = useCallback(async (): Promise<AuthUser> => {
    setLoading(true);
    try {
      const res = await fetch(`${BASE_URL}/auth/become-seller`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem(TOKEN_KEY)}`,
        },
      });
      if (!res.ok) {
        let msg = 'Failed to become seller';
        try {
          const err = await res.json();
          msg = err.message || msg;
        } catch {
          const text = await res.text();
          if (text) msg = text;
        }
        throw new Error(msg);
      }
      const data: { token: string; username: string; role: string } = await res.json();
      const authUser: AuthUser = { username: data.username, role: 'ROLE_SELLER' };
      localStorage.setItem(TOKEN_KEY, data.token);
      localStorage.setItem(USER_KEY, JSON.stringify(authUser));
      setToken(data.token);
      setUser(authUser);
      return authUser;
    } finally {
      setLoading(false);
    }
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    setToken(null);
    setUser(null);
  }, []);

  return (
    <Ctx.Provider value={{
      user, token,
      isAuthenticated: !!user,
      isAdmin: user?.role === 'ROLE_ADMIN',
      isSeller: user?.role === 'ROLE_SELLER',
      loading, login, register, becomeSeller, logout,
    }}>
      {children}
    </Ctx.Provider>
  );
}

export function useAuth() {
  const c = useContext(Ctx);
  if (!c) throw new Error('useAuth must be inside AuthProvider');
  return c;
}
