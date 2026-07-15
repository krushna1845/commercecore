import React, { useState, useRef, useEffect } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { cn } from '@/lib/utils';
import type { RecommendedProduct } from '@/types';

interface RecommendationCarouselProps {
  title: string;
  products: RecommendedProduct[];
  loading?: boolean;
  error?: Error | null;
  onProductClick?: (productId: string) => void;
  className?: string;
  itemsPerView?: number;
  showScore?: boolean;
}

export const RecommendationCarousel: React.FC<RecommendationCarouselProps> = ({
  title,
  products,
  loading = false,
  error = null,
  onProductClick,
  className = '',
  itemsPerView = 5,
  showScore = false,
}) => {
  const [scrollPosition, setScrollPosition] = useState(0);
  const scrollContainerRef = useRef<HTMLDivElement>(null);
  const [canScrollLeft, setCanScrollLeft] = useState(false);
  const [canScrollRight, setCanScrollRight] = useState(true);

  const checkScroll = () => {
    if (scrollContainerRef.current) {
      const { scrollLeft, scrollWidth, clientWidth } = scrollContainerRef.current;
      setCanScrollLeft(scrollLeft > 0);
      setCanScrollRight(scrollLeft < scrollWidth - clientWidth - 10);
      setScrollPosition(scrollLeft);
    }
  };

  useEffect(() => {
    checkScroll();
    const container = scrollContainerRef.current;
    container?.addEventListener('scroll', checkScroll);
    window.addEventListener('resize', checkScroll);

    return () => {
      container?.removeEventListener('scroll', checkScroll);
      window.removeEventListener('resize', checkScroll);
    };
  }, [products]);

  const scroll = (direction: 'left' | 'right') => {
    if (scrollContainerRef.current) {
      const scrollAmount = scrollContainerRef.current.clientWidth / 2;
      scrollContainerRef.current.scrollBy({
        left: direction === 'left' ? -scrollAmount : scrollAmount,
        behavior: 'smooth',
      });
    }
  };

  if (loading) {
    return (
      <div className={cn('w-full', className)}>
        <h3 className="text-lg font-semibold mb-4">{title}</h3>
        <div className="flex gap-4 overflow-hidden">
          {Array.from({ length: itemsPerView }).map((_, i) => (
            <div
              key={i}
              className="flex-shrink-0 w-48 h-64 bg-gray-200 rounded-lg animate-pulse"
            />
          ))}
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className={cn('w-full', className)}>
        <h3 className="text-lg font-semibold mb-4">{title}</h3>
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700">
          Failed to load recommendations. Please try again later.
        </div>
      </div>
    );
  }

  if (!products || products.length === 0) {
    return (
      <div className={cn('w-full', className)}>
        <h3 className="text-lg font-semibold mb-4">{title}</h3>
        <div className="bg-gray-50 border border-gray-200 rounded-lg p-8 text-center text-gray-600">
          No recommendations available at this time.
        </div>
      </div>
    );
  }

  return (
    <div className={cn('w-full', className)}>
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold">{title}</h3>
        <div className="flex gap-2">
          <button
            onClick={() => scroll('left')}
            disabled={!canScrollLeft}
            className={cn(
              'p-2 rounded-full transition-colors',
              canScrollLeft
                ? 'bg-gray-200 hover:bg-gray-300 text-gray-800'
                : 'bg-gray-100 text-gray-400 cursor-not-allowed'
            )}
            aria-label="Scroll left"
          >
            <ChevronLeft size={20} />
          </button>
          <button
            onClick={() => scroll('right')}
            disabled={!canScrollRight}
            className={cn(
              'p-2 rounded-full transition-colors',
              canScrollRight
                ? 'bg-gray-200 hover:bg-gray-300 text-gray-800'
                : 'bg-gray-100 text-gray-400 cursor-not-allowed'
            )}
            aria-label="Scroll right"
          >
            <ChevronRight size={20} />
          </button>
        </div>
      </div>

      <div
        ref={scrollContainerRef}
        className="flex gap-4 overflow-x-auto snap-x snap-mandatory scroll-smooth"
        style={{ scrollBehavior: 'smooth' }}
      >
        {products.map((product) => (
          <ProductCard
            key={product.productId}
            product={product}
            onClick={() => onProductClick?.(product.productId)}
            showScore={showScore}
          />
        ))}
      </div>
    </div>
  );
};

interface ProductCardProps {
  product: RecommendedProduct;
  onClick?: () => void;
  showScore?: boolean;
}

const ProductCard: React.FC<ProductCardProps> = ({ product, onClick, showScore }) => {
  return (
    <div
      onClick={onClick}
      className="flex-shrink-0 w-48 snap-start cursor-pointer group"
    >
      <div className="bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow overflow-hidden">
        {/* Image Container */}
        <div className="relative bg-gray-100 h-48 overflow-hidden">
          <img
            src={product.imageUrl || '/placeholder.jpg'}
            alt={product.productName}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
            onError={(e) => {
              (e.target as HTMLImageElement).src = '/placeholder.jpg';
            }}
          />
          {showScore && product.score && (
            <div className="absolute top-2 right-2 bg-green-500 text-white px-2 py-1 rounded text-xs font-semibold">
              Score: {product.score.toFixed(2)}
            </div>
          )}
        </div>

        {/* Content */}
        <div className="p-4">
          <h4 className="font-medium text-sm line-clamp-2 mb-2 group-hover:text-blue-600 transition-colors">
            {product.productName}
          </h4>

          {/* Rating */}
          <div className="flex items-center gap-1 mb-3">
            <div className="flex">
              {Array.from({ length: 5 }).map((_, i) => (
                <span key={i} className="text-yellow-400">
                  {i < Math.floor(product.rating) ? '★' : '☆'}
                </span>
              ))}
            </div>
            <span className="text-xs text-gray-600">({product.reviewCount})</span>
          </div>

          {/* Price */}
          <div className="text-lg font-bold text-gray-900 mb-2">
            ₹{product.price.toLocaleString('en-IN')}
          </div>

          {/* Position Badge */}
          {product.rankPosition && (
            <div className="text-xs text-gray-500">
              #{product.rankPosition}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default RecommendationCarousel;
