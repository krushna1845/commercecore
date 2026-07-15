# Enterprise Recommendation Engine

A sophisticated recommendation engine built with Spring Boot backend and React frontend, featuring multiple recommendation algorithms and optimized caching strategies.

## Features

### Core Recommendation Types

1. **Frequently Bought Together** - Products commonly purchased together based on transaction history
2. **Customers Also Bought** - Related products from similar categories
3. **Recommended For You** - Personalized recommendations based on browsing and purchase history
4. **Trending** - Products with highest sales in the last 24 hours
5. **Popular** - Best-performing products over 7-30 days
6. **Recently Viewed** - User's browsing history
7. **Popular Products** - Top-rated and most-reviewed items

### Algorithms

- **Category Similarity** - Recommends products from similar categories using similarity scoring
- **Purchase History** - Analyzes co-purchase patterns and frequency
- **Browsing History** - Tracks user interactions and viewing patterns
- **Wishlist History** - Considers saved items (extensible)
- **Product Ratings** - Incorporates average ratings and review counts
- **Sales Frequency** - Tracks sales volume across different time windows (24h, 7d, 30d)

## Backend Setup

### Database Schema

The recommendation engine uses the following tables:

```sql
-- Core recommendation tables
- frequently_bought_together     # Co-purchase tracking
- browsing_history               # User viewing history
- product_sales_frequency        # Sales metrics
- product_ratings                # Product reviews and ratings
- product_recommendations        # Cached product recommendations
- user_recommendations           # Cached user recommendations
- category_similarity            # Category relationship scores
- product_view_stats             # View and engagement metrics
```

### Installation

1. **Apply Database Migration**
   ```bash
   # Execute the SQL schema
   mysql -u root -p commerce_db < recommendation_schema.sql
   ```

2. **Update Dependencies** (already added to pom.xml)
   ```xml
   <!-- Redis & Cache -->
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-redis</artifactId>
   </dependency>
   <dependency>
       <groupId>redis.clients</groupId>
       <artifactId>jedis</artifactId>
   </dependency>
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-cache</artifactId>
   </dependency>
   ```

3. **Configure Redis** (in application.properties)
   ```properties
   spring.redis.host=localhost
   spring.redis.port=6379
   spring.redis.password=
   spring.cache.type=redis
   spring.cache.redis.time-to-live=3600000
   ```

### Backend API Endpoints

#### Product-Based Recommendations

```
GET /api/recommendations/product/{productId}/frequently-bought-together?limit=10
GET /api/recommendations/product/{productId}/customers-also-bought?limit=10
GET /api/recommendations/product/{productId}/all?limit=5
```

#### User-Based Recommendations

```
GET /api/recommendations/user/{userId}/recommended-for-you?limit=10
GET /api/recommendations/user/{userId}/recently-viewed?limit=10
POST /api/recommendations/user/{userId}/track-browse/{productId}
```

#### Global Recommendations

```
GET /api/recommendations/trending?limit=10
GET /api/recommendations/popular?limit=10
```

### Sample Responses

```json
{
  "recommendationType": "frequently_bought_together",
  "products": [
    {
      "productId": "123",
      "productName": "Premium Headphones",
      "price": 2999,
      "rating": 4.5,
      "reviewCount": 156,
      "score": 0.95,
      "rankPosition": 1,
      "imageUrl": "https://..."
    }
  ],
  "cachedAt": 1703001600000
}
```

## Frontend Setup

### Installation

1. **Install Dependencies**
   ```bash
   npm install
   ```

2. **Configure API URL**
   ```bash
   # Create .env file
   VITE_API_URL=http://localhost:8081
   ```

### Components

#### RecommendationCarousel Component

Reusable carousel for displaying recommendations with navigation controls.

```tsx
import RecommendationCarousel from '@/components/RecommendationCarousel';

<RecommendationCarousel
  title="Frequently Bought Together"
  products={products}
  loading={loading}
  onProductClick={(productId) => navigate(`/product/${productId}`)}
  itemsPerView={5}
  showScore={true}
/>
```

**Props:**
- `title: string` - Carousel title
- `products: RecommendedProduct[]` - Array of products
- `loading?: boolean` - Loading state
- `error?: Error` - Error object
- `onProductClick?: (productId: string) => void` - Click handler
- `itemsPerView?: number` - Items visible at once (default: 5)
- `showScore?: boolean` - Show recommendation score (default: false)

### Hooks

#### useRecommendations Hook

Custom hook for managing recommendation data fetching.

```tsx
import { useRecommendations } from '@/hooks/useRecommendations';

const {
  data,
  loading,
  error,
  fetchFrequentlyBoughtTogether,
  fetchCustomersAlsoBought,
  fetchRecommendedForYou,
  fetchRecentlyViewed,
  fetchTrendingProducts,
  fetchPopularProducts,
  trackBrowse,
} = useRecommendations({ limit: 10, autoFetch: true });

// Fetch recommendations
await fetchFrequentlyBoughtTogether(productId);

// Track user browsing
await trackBrowse(userId, productId);
```

## Caching Strategy

### Cache Layers

1. **Redis Cache** (1 hour TTL)
   - Frequently accessed recommendations
   - Trending and popular products
   - Category similarity scores

2. **Database Indexes**
   - Optimized queries on product_id
   - Sales frequency sorting
   - Confidence score retrieval

3. **Client-Side Caching**
   - React state management
   - Successful responses cached
   - Automatic invalidation on product changes

### Cache Keys

```
frequently_bought:{productId}
customers_also_bought:{productId}
recommended_for_you:{userId}
trending
popular
recently_viewed:{userId}
```

### Cache Invalidation

Caches are automatically cleared when:
- Product ratings change
- Sales records are updated
- User browsing history changes
- Category associations update

## Performance Optimization

### Database Indexes

All tables include strategic indexes on frequently queried columns:

```sql
-- Sales queries
INDEX idx_sales_24h (sales_count_24h DESC)
INDEX idx_sales_7d (sales_count_7d DESC)
INDEX idx_sales_30d (sales_count_30d DESC)

-- Rating queries
INDEX idx_product_rating (product_id, rating DESC)

-- Time-based queries
INDEX idx_user_timestamp (user_id, viewed_at DESC)
```

### Query Optimization

- Uses compiled queries where possible
- Batch inserts for bulk operations
- Efficient JOIN operations
- Limit-based pagination

### API Response Times

Typical response times:
- Frequently Bought Together: ~50ms (cached)
- Trending/Popular: ~100ms (cached)
- Personalized: ~200ms (computed)

## Integration Examples

### Product Detail Page

```tsx
const [userId] = useState(() => localStorage.getItem('userId') || '1');
const { fetchFrequentlyBoughtTogether, trackBrowse } = useRecommendations();

useEffect(() => {
  // Track browsing
  trackBrowse(userId, productId);
  
  // Fetch recommendations
  fetchFrequentlyBoughtTogether(productId);
}, [productId, userId]);
```

### Homepage

```tsx
const { fetchTrendingProducts, fetchPopularProducts } = useRecommendations();

useEffect(() => {
  Promise.all([
    fetchTrendingProducts(),
    fetchPopularProducts(),
  ]);
}, []);
```

## Algorithm Details

### Confidence Score Calculation

```
confidence_score = min(1.0, purchase_count / 100)
```

Provides a normalized score from 0-1 based on purchase frequency.

### Category Similarity Scoring

```
similarity_score = shared_purchases / total_purchases
```

Measures how often products from two categories are bought together.

### Sales Trend Calculation

```
trend_score = sales_24h + (sales_7d * 0.5) + (sales_30d * 0.25)
```

Weighted combination favoring recent sales.

## Monitoring & Analytics

### Key Metrics to Track

- Cache hit ratio (target: >80%)
- Average response time per endpoint
- Recommendation accuracy (user engagement)
- Popular vs. trending product overlap
- User conversion rate from recommendations

### Troubleshooting

**Issue: Slow recommendations**
- Check Redis connection
- Verify database indexes
- Monitor query execution plans

**Issue: Inconsistent results**
- Clear Redis cache: `FLUSHALL`
- Verify cache TTL settings
- Check for race conditions in concurrent updates

**Issue: Memory issues**
- Reduce Redis TTL
- Implement recommendation pruning
- Use connection pooling

## Future Enhancements

1. **Collaborative Filtering** - ML-based user similarity
2. **Content-Based Filtering** - Product attribute matching
3. **A/B Testing Framework** - Algorithm comparison
4. **Real-time Updates** - WebSocket recommendations
5. **Elasticsearch Integration** - Advanced search + recommendations
6. **GraphQL API** - Efficient data fetching
7. **Recommendation Explanations** - Show why products recommended

## Security Considerations

- Sanitize user input in API requests
- Implement rate limiting on endpoints
- Add authentication/authorization checks
- Encrypt sensitive data in cache
- Regular security audits

## Deployment Checklist

- [ ] Redis instance running and accessible
- [ ] MySQL tables created with proper indexes
- [ ] Spring Boot dependencies updated
- [ ] Redis configuration in application.properties
- [ ] Frontend dependencies installed
- [ ] API endpoints tested
- [ ] Cache settings optimized for load
- [ ] Monitoring setup in place
- [ ] Error handling implemented
- [ ] Performance tested under load
