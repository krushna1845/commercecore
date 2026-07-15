import { useState, useEffect } from 'react';
import { X, Plus, Check, Star, AlertCircle } from 'lucide-react';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { Card } from './ui/card';
import { ScrollArea } from './ui/scroll-area';
import { Separator } from './ui/separator';

interface ProductComparisonItem {
  id: number;
  name: string;
  imageUrl: string;
  price: number;
  originalPrice: number;
  discountPercentage: number;
  rating: number;
  reviewCount: number;
  stockQuantity: number;
  brand: string;
  warranty: string;
  returnPolicy: string;
  specs: Record<string, string>;
  features: string[];
  bestPrice: boolean;
  bestRating: boolean;
  bestDiscount: boolean;
  inStock: boolean;
}

interface ComparisonResult {
  products: ProductComparisonItem[];
  totalCount: number;
  canAddMore: boolean;
}

export function ProductComparison() {
  const [comparison, setComparison] = useState<ComparisonResult | null>(null);
  const [isOpen, setIsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    fetchComparison();
  }, []);

  const fetchComparison = async () => {
    setIsLoading(true);
    try {
      const response = await fetch('/api/comparison', {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
      });
      if (response.ok) {
        const data = await response.json();
        setComparison(data);
      }
    } catch (error) {
      console.error('Failed to fetch comparison:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const addToComparison = async (productId: number) => {
    try {
      const response = await fetch(`/api/comparison/add/${productId}`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
      });
      if (response.ok) {
        fetchComparison();
      }
    } catch (error) {
      console.error('Failed to add to comparison:', error);
    }
  };

  const removeFromComparison = async (productId: number) => {
    try {
      const response = await fetch(`/api/comparison/remove/${productId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
      });
      if (response.ok) {
        fetchComparison();
      }
    } catch (error) {
      console.error('Failed to remove from comparison:', error);
    }
  };

  const clearComparison = async () => {
    try {
      const response = await fetch('/api/comparison/clear', {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
      });
      if (response.ok) {
        fetchComparison();
      }
    } catch (error) {
      console.error('Failed to clear comparison:', error);
    }
  };

  const BestValueBadge = ({ isBest, label }: { isBest: boolean; label: string }) => {
    if (!isBest) return null;
    return (
      <Badge className="bg-green-500 hover:bg-green-600">
        <Check className="w-3 h-3 mr-1" />
        {label}
      </Badge>
    );
  };

  if (!isOpen) {
    return (
      <div className="fixed bottom-4 right-4 z-50">
        <Button
          onClick={() => setIsOpen(true)}
          className="shadow-lg"
          size="lg"
        >
          <Plus className="w-4 h-4 mr-2" />
          Compare Products
          {comparison && comparison.totalCount > 0 && (
            <Badge className="ml-2 bg-primary">{comparison.totalCount}</Badge>
          )}
        </Button>
      </div>
    );
  }

  return (
    <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
      <Card className="w-full max-w-7xl max-h-[90vh] overflow-hidden flex flex-col">
        <div className="p-6 border-b flex items-center justify-between">
          <div>
            <h2 className="text-2xl font-bold">Product Comparison</h2>
            <p className="text-muted-foreground">
              {comparison?.totalCount || 0} of 4 products selected
            </p>
          </div>
          <div className="flex gap-2">
            {comparison && comparison.totalCount > 0 && (
              <Button variant="outline" onClick={clearComparison}>
                Clear All
              </Button>
            )}
            <Button variant="ghost" size="icon" onClick={() => setIsOpen(false)}>
              <X className="w-5 h-5" />
            </Button>
          </div>
        </div>

        <ScrollArea className="flex-1 p-6">
          {isLoading ? (
            <div className="flex items-center justify-center h-64">
              <div className="text-muted-foreground">Loading...</div>
            </div>
          ) : !comparison || comparison.totalCount === 0 ? (
            <div className="flex flex-col items-center justify-center h-64 text-center">
              <AlertCircle className="w-12 h-12 text-muted-foreground mb-4" />
              <h3 className="text-lg font-semibold mb-2">No products to compare</h3>
              <p className="text-muted-foreground mb-4">
                Add up to 4 products to compare their features
              </p>
              <Button onClick={() => setIsOpen(false)}>Browse Products</Button>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
              {comparison.products.map((product) => (
                <div key={product.id} className="space-y-4">
                  <Card className="p-4 relative">
                    <Button
                      variant="ghost"
                      size="icon"
                      className="absolute top-2 right-2"
                      onClick={() => removeFromComparison(product.id)}
                    >
                      <X className="w-4 h-4" />
                    </Button>
                    <img
                      src={product.imageUrl}
                      alt={product.name}
                      className="w-full h-48 object-cover rounded-lg mb-4"
                    />
                    <h3 className="font-semibold text-lg mb-2 line-clamp-2">{product.name}</h3>
                    <div className="space-y-2">
                      <div className="flex items-center justify-between">
                        <div>
                          <span className="text-2xl font-bold">${product.price}</span>
                          {product.originalPrice > product.price && (
                            <span className="text-sm text-muted-foreground line-through ml-2">
                              ${product.originalPrice}
                            </span>
                          )}
                        </div>
                        <BestValueBadge isBest={product.bestPrice} label="Best Price" />
                      </div>
                      {product.discountPercentage > 0 && (
                        <div className="flex items-center justify-between">
                          <Badge variant="secondary">{product.discountPercentage.toFixed(0)}% OFF</Badge>
                          <BestValueBadge isBest={product.bestDiscount} label="Best Discount" />
                        </div>
                      )}
                      <div className="flex items-center gap-1">
                        <Star className="w-4 h-4 fill-yellow-400 text-yellow-400" />
                        <span className="font-medium">{product.rating}</span>
                        <span className="text-muted-foreground">({product.reviewCount})</span>
                        <BestValueBadge isBest={product.bestRating} label="Top Rated" />
                      </div>
                      <Badge variant={product.inStock ? "default" : "destructive"}>
                        {product.inStock ? 'In Stock' : 'Out of Stock'}
                      </Badge>
                    </div>
                  </Card>

                  <Card className="p-4">
                    <h4 className="font-semibold mb-3">Specifications</h4>
                    <div className="space-y-2 text-sm">
                      {Object.entries(product.specs).map(([key, value]) => (
                        <div key={key} className="flex justify-between">
                          <span className="text-muted-foreground">{key}:</span>
                          <span className="font-medium">{value}</span>
                        </div>
                      ))}
                    </div>
                  </Card>

                  <Card className="p-4">
                    <h4 className="font-semibold mb-3">Features</h4>
                    <ul className="space-y-1 text-sm">
                      {product.features.map((feature, index) => (
                        <li key={index} className="flex items-start gap-2">
                          <Check className="w-4 h-4 text-green-500 flex-shrink-0 mt-0.5" />
                          <span>{feature}</span>
                        </li>
                      ))}
                    </ul>
                  </Card>

                  <Card className="p-4">
                    <h4 className="font-semibold mb-3">Policies</h4>
                    <div className="space-y-2 text-sm">
                      <div>
                        <span className="text-muted-foreground">Brand:</span>
                        <span className="ml-2 font-medium">{product.brand}</span>
                      </div>
                      <div>
                        <span className="text-muted-foreground">Warranty:</span>
                        <span className="ml-2 font-medium">{product.warranty}</span>
                      </div>
                      <div>
                        <span className="text-muted-foreground">Return Policy:</span>
                        <span className="ml-2 font-medium">{product.returnPolicy}</span>
                      </div>
                    </div>
                  </Card>
                </div>
              ))}
            </div>
          )}
        </ScrollArea>
      </Card>
    </div>
  );
}
