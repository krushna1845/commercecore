import { Route, Routes } from 'react-router-dom';
import { ProtectedRoute, AdminRoute, SellerRoute } from './ProtectedRoute';

import Index from '@/pages/Index';
import ProductListing from '@/pages/ProductListing';
import ProductDetails from '@/pages/ProductDetails';
import Compare from '@/pages/Compare';
import Cart from '@/pages/Cart';
import Checkout from '@/pages/Checkout';
import OrderSuccess from '@/pages/OrderSuccess';
import Wishlist from '@/pages/Wishlist';
import Profile from '@/pages/Profile';
import Orders from '@/pages/Orders';
import OrderDetails from '@/pages/OrderDetails';
import Addresses from '@/pages/Addresses';
import ChangePassword from '@/pages/ChangePassword';
import Settings from '@/pages/Settings';
import Assistant from '@/pages/Assistant';
import Login from '@/pages/auth/Login';
import Register from '@/pages/auth/Register';
import ForgotPassword from '@/pages/auth/ForgotPassword';
import VerifyOtp from '@/pages/auth/VerifyOtp';
import ResetPassword from '@/pages/auth/ResetPassword';
import AdminDashboard from '@/pages/admin/AdminDashboard';
import AdminProducts from '@/pages/admin/AdminProducts';
import AdminSellerSubmissions from '@/pages/admin/AdminSellerSubmissions';
import AdminOrders from '@/pages/admin/AdminOrders';
import AdminUsers from '@/pages/admin/AdminUsers';
import AdminCategories from '@/pages/admin/AdminCategories';
import AdminInventory from '@/pages/admin/AdminInventory';
import AdminAnalytics from '@/pages/admin/AdminAnalytics';
import { SellerDashboard } from '@/components/SellerDashboard';
import NotFound from '@/pages/NotFound';

export const AppRoutes = () => (
  <Routes>
    {/* Public */}
    <Route path="/" element={<Index />} />
    <Route path="/products" element={<ProductListing />} />
    <Route path="/products/:id" element={<ProductDetails />} />
    <Route path="/compare" element={<Compare />} />
    <Route path="/cart" element={<Cart />} />

    {/* Auth */}
    <Route path="/login" element={<Login />} />
    <Route path="/register" element={<Register />} />
    <Route path="/forgot-password" element={<ForgotPassword />} />
    <Route path="/verify-otp" element={<VerifyOtp />} />
    <Route path="/reset-password" element={<ResetPassword />} />

    {/* Protected — must be logged in */}
    <Route path="/checkout" element={<ProtectedRoute><Checkout /></ProtectedRoute>} />
    <Route path="/order-success" element={<ProtectedRoute><OrderSuccess /></ProtectedRoute>} />
    <Route path="/success" element={<ProtectedRoute><OrderSuccess /></ProtectedRoute>} />
    <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />
    <Route path="/orders" element={<ProtectedRoute><Orders /></ProtectedRoute>} />
    <Route path="/orders/:id" element={<ProtectedRoute><OrderDetails /></ProtectedRoute>} />
    <Route path="/wishlist" element={<ProtectedRoute><Wishlist /></ProtectedRoute>} />
    <Route path="/addresses" element={<ProtectedRoute><Addresses /></ProtectedRoute>} />
    <Route path="/change-password" element={<ProtectedRoute><ChangePassword /></ProtectedRoute>} />
    <Route path="/settings" element={<ProtectedRoute><Settings /></ProtectedRoute>} />
    <Route path="/assistant" element={<ProtectedRoute><Assistant /></ProtectedRoute>} />

    {/* Admin — must be logged in AND have ROLE_ADMIN */}
    <Route path="/admin" element={<AdminRoute><AdminDashboard /></AdminRoute>} />
    <Route path="/admin/products" element={<AdminRoute><AdminProducts /></AdminRoute>} />
    <Route path="/admin/seller-submissions" element={<AdminRoute><AdminSellerSubmissions /></AdminRoute>} />
    <Route path="/admin/orders" element={<AdminRoute><AdminOrders /></AdminRoute>} />
    <Route path="/admin/users" element={<AdminRoute><AdminUsers /></AdminRoute>} />
    <Route path="/admin/categories" element={<AdminRoute><AdminCategories /></AdminRoute>} />
    <Route path="/admin/inventory" element={<AdminRoute><AdminInventory /></AdminRoute>} />
    <Route path="/admin/analytics" element={<AdminRoute><AdminAnalytics /></AdminRoute>} />

    {/* Seller — must be logged in AND have ROLE_SELLER */}
    <Route path="/seller" element={<SellerRoute><SellerDashboard /></SellerRoute>} />

    <Route path="*" element={<NotFound />} />
  </Routes>
);
