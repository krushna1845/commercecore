import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import RecommendationCarousel from '@/components/RecommendationCarousel';
import { useRecommendations } from '@/hooks/useRecommendations';
import { categoryApi } from '@/services/categoryApi';
import type { Category } from '@/types';

const defaultCategories: Category[] = [
  { id: '1', name: 'Electronics', icon: '🎧', color: 'bg-blue-500', count: 0 },
  { id: '2', name: 'Fashion', icon: '👗', color: 'bg-pink-500', count: 0 },
  { id: '3', name: 'Home & Living', icon: '🛋️', color: 'bg-green-500', count: 0 },
  { id: '4', name: 'Beauty', icon: '💄', color: 'bg-purple-500', count: 0 },
  { id: '5', name: 'Sports', icon: '⚽', color: 'bg-orange-500', count: 0 },
  { id: '6', name: 'Books', icon: '📚', color: 'bg-yellow-500', count: 0 },
  { id: '7', name: 'Toys', icon: '🧸', color: 'bg-red-500', count: 0 },
  { id: '8', name: 'Grocery', icon: '🥬', color: 'bg-teal-500', count: 0 },
];

export const HomePage: React.FC = () => {
  const [categories, setCategories] = useState<Category[]>(defaultCategories);
  const {
    data: trendingData,
    loading: trendingLoading,
    fetchTrendingProducts,
  } = useRecommendations();

  const {
    data: popularData,
    loading: popularLoading,
    fetchPopularProducts,
  } = useRecommendations();

  const {
    data: recommendedData,
    loading: recommendedLoading,
    fetchRecommendedForYou,
  } = useRecommendations();

  useEffect(() => {
    const userId = localStorage.getItem('userId');

    Promise.all([
      fetchTrendingProducts(),
      fetchPopularProducts(),
      userId ? fetchRecommendedForYou(userId) : Promise.resolve(),
      categoryApi.list(),
    ]).then(([_, __, ___, cats]) => {
      if (cats && cats.length > 0) {
        setCategories(cats.map((c: any) => ({
          id: String(c.id),
          name: c.name,
          icon: defaultCategories.find(dc => dc.name === c.name)?.icon || '📦',
          color: defaultCategories.find(dc => dc.name === c.name)?.color || 'bg-gray-500',
          count: 0,
        })));
      }
    }).catch(console.error);
  }, []);

  const handleProductClick = (productId: string) => {
    window.location.href = `/product/${productId}`;
  };

  return (
    <div className="bg-gray-50">
      {/* Hero Section */}
      <div className="bg-gradient-to-r from-blue-600 to-blue-800 text-white py-16 mb-12">
        <div className="max-w-7xl mx-auto px-4">
          <h1 className="text-2xl font-medium mb-2">Welcome to ShopHub</h1>
          <p className="text-base opacity-90">
            Browse our collection of products
          </p>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 pb-12 space-y-12">
        {/* Categories */}
        <div>
          <h2 className="text-lg font-medium mb-4">Categories</h2>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {categories.map((category) => (
              <Link
                key={category.id}
                to={`/products?category=${encodeURIComponent(category.name)}`}
                className="group relative overflow-hidden rounded-2xl border border-white/10 bg-white p-6 shadow-sm hover:shadow-md transition-all duration-300 hover:-translate-y-1"
              >
                <div className="text-2xl mb-2">{category.icon}</div>
                <h3 className="text-sm font-medium text-gray-700">{category.name}</h3>
              </Link>
            ))}
          </div>
        </div>

        {/* Trending Products */}
        {trendingData?.products && (
          <RecommendationCarousel
            title="Trending Products"
            products={trendingData.products}
            loading={trendingLoading}
            onProductClick={handleProductClick}
            itemsPerView={6}
            className="bg-white rounded-lg p-6 shadow-sm"
          />
        )}

        {/* Popular Products */}
        {popularData?.products && (
          <RecommendationCarousel
            title="Popular Products"
            products={popularData.products}
            loading={popularLoading}
            onProductClick={handleProductClick}
            itemsPerView={6}
            className="bg-white rounded-lg p-6 shadow-sm"
          />
        )}

        {/* Personalized Recommendations */}
        {recommendedData?.products && (
          <RecommendationCarousel
            title="Recommended For You"
            products={recommendedData.products}
            loading={recommendedLoading}
            onProductClick={handleProductClick}
            itemsPerView={6}
            className="bg-white rounded-lg p-6 shadow-sm"
          />
        )}
      </div>
    </div>
  );
};

export default HomePage;
