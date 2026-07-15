import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import RecommendationCarousel from '@/components/RecommendationCarousel';
import { useRecommendations } from '@/hooks/useRecommendations';
import { productApi } from '@/services/productApi';
import type { Product } from '@/types';

export const ProductDetailPage: React.FC = () => {
  const { productId } = useParams<{ productId: string }>();
  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const [userId] = useState(() => localStorage.getItem('userId') || '1');

  const {
    data: frequentlyBoughtData,
    loading: frequentlyLoading,
    fetchFrequentlyBoughtTogether,
  } = useRecommendations();

  const {
    data: customersAlsoBoughtData,
    loading: customersLoading,
    fetchCustomersAlsoBought,
  } = useRecommendations();

  const {
    data: recommendedForYouData,
    loading: recommendedLoading,
    fetchRecommendedForYou,
  } = useRecommendations();

  const {
    data: recentlyViewedData,
    loading: recentlyLoading,
    fetchRecentlyViewed,
    trackBrowse,
  } = useRecommendations();

  useEffect(() => {
    if (productId) {
      // Fetch product details
      productApi
        .get(productId)
        .then(setProduct)
        .finally(() => setLoading(false));

      // Track browse
      trackBrowse(userId, productId);

      // Fetch all recommendations
      Promise.all([
        fetchFrequentlyBoughtTogether(productId),
        fetchCustomersAlsoBought(productId),
        fetchRecommendedForYou(userId),
        fetchRecentlyViewed(userId),
      ]).catch(console.error);
    }
  }, [productId, userId]);

  if (loading) {
    return <div className="p-8">Loading product...</div>;
  }

  if (!product) {
    return <div className="p-8">Product not found</div>;
  }

  const handleProductClick = (productId: string) => {
    window.location.href = `/product/${productId}`;
  };

  return (
    <div className="bg-white">
      {/* Product Details Section */}
      <div className="max-w-7xl mx-auto px-4 py-8">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-12">
          {/* Image */}
          <div className="bg-gray-100 rounded-lg h-96 overflow-hidden">
            <img
              src={product.image || '/placeholder.jpg'}
              alt={product.name}
              className="w-full h-full object-cover"
            />
          </div>

          {/* Product Info */}
          <div className="flex flex-col justify-center">
            <h1 className="text-4xl font-bold mb-4">{product.name}</h1>
            <p className="text-xl text-gray-600 mb-6">{product.description}</p>

            {/* Rating */}
            <div className="flex items-center gap-2 mb-6">
              <div className="flex gap-1">
                {Array.from({ length: 5 }).map((_, i) => (
                  <span key={i} className="text-2xl text-yellow-400">
                    {i < Math.floor(product.rating) ? '★' : '☆'}
                  </span>
                ))}
              </div>
              <span className="text-gray-600">({product.reviewCount} reviews)</span>
            </div>

            {/* Price */}
            <div className="mb-6">
              <span className="text-4xl font-bold text-gray-900">
                ₹{product.price.toLocaleString('en-IN')}
              </span>
              {product.originalPrice && (
                <span className="ml-4 text-xl text-gray-500 line-through">
                  ₹{product.originalPrice.toLocaleString('en-IN')}
                </span>
              )}
            </div>

            {/* Add to Cart Button */}
            <button className="bg-blue-600 text-white px-8 py-3 rounded-lg font-semibold hover:bg-blue-700 transition-colors mb-8">
              Add to Cart
            </button>
          </div>
        </div>

        {/* Recommendations Section */}
        <div className="space-y-8 border-t pt-8">
          {/* Frequently Bought Together */}
          {frequentlyBoughtData?.products && frequentlyBoughtData.products.length > 0 && (
            <RecommendationCarousel
              title="Frequently Bought Together"
              products={frequentlyBoughtData.products}
              loading={frequentlyLoading}
              onProductClick={handleProductClick}
              itemsPerView={5}
              showScore={true}
              className="mb-8"
            />
          )}

          {/* Customers Also Bought */}
          {customersAlsoBoughtData?.products && customersAlsoBoughtData.products.length > 0 && (
            <RecommendationCarousel
              title="Customers Also Bought"
              products={customersAlsoBoughtData.products}
              loading={customersLoading}
              onProductClick={handleProductClick}
              itemsPerView={5}
              className="mb-8"
            />
          )}

          {/* Recommended For You */}
          {recommendedForYouData?.products && recommendedForYouData.products.length > 0 && (
            <RecommendationCarousel
              title="Recommended For You"
              products={recommendedForYouData.products}
              loading={recommendedLoading}
              onProductClick={handleProductClick}
              itemsPerView={5}
              className="mb-8"
            />
          )}

          {/* Recently Viewed */}
          {recentlyViewedData?.products && recentlyViewedData.products.length > 0 && (
            <RecommendationCarousel
              title="Your Recently Viewed Products"
              products={recentlyViewedData.products}
              loading={recentlyLoading}
              onProductClick={handleProductClick}
              itemsPerView={5}
              className="mb-8"
            />
          )}
        </div>
      </div>
    </div>
  );
};

export default ProductDetailPage;
