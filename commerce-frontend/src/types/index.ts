export interface SellerInfo {
  id: string;
  username: string;
}

export interface Product {
  id: string;
  name: string;
  brand: string;
  category: string;
  price: number;
  originalPrice?: number;
  rating: number;
  reviewCount: number;
  image: string;
  images?: string[];
  description: string;
  specs?: Record<string, string>;
  stock: number;
  badge?: 'new' | 'best-seller' | 'trending' | 'deal';
  tags?: string[];
  warranty?: string;
  returnPolicy?: string;
  features?: string[];
  seller?: SellerInfo | string;
}

export interface Category {
  id: string;
  name: string;
  icon: string;
  color: string;
  count: number;
}

export interface CartItem {
  product: Product;
  quantity: number;
}

export interface Address {
  id: string;
  name: string;
  phone: string;
  line1: string;
  line2?: string;
  city: string;
  state: string;
  pincode: string;
  type: 'home' | 'work' | 'other';
  isDefault?: boolean;
}

export interface Order {
  id: string;
  date: string;
  items: CartItem[];
  total: number;
  status: 'placed' | 'packed' | 'shipped' | 'out-for-delivery' | 'delivered' | 'cancelled';
  paymentMethod: string;
  address: Address;
  estimatedDelivery: string;
}

export interface User {
  id: string;
  name: string;
  email: string;
  phone?: string;
  avatar?: string;
  role: 'user' | 'admin';
  joinedAt: string;
}

export interface Review {
  id: string;
  user: string;
  avatar?: string;
  rating: number;
  title: string;
  comment: string;
  date: string;
  verified?: boolean;
}

// Recommendation Engine Types
export interface RecommendedProduct {
  productId: string;
  productName: string;
  description?: string;
  price: number;
  rating: number;
  reviewCount: number;
  score: number;
  rankPosition: number;
  imageUrl: string;
}

export interface RecommendationResponse {
  recommendationType: string;
  products: RecommendedProduct[];
  cachedAt: number;
}

export interface MultipleRecommendationsResponse {
  productId: string;
  recommendations: RecommendationResponse[];
}
