# Recommendation Engine - Quick Start Guide

## Backend Setup (Spring Boot)

### 1. Database Initialization

```bash
# Login to MySQL
mysql -u root -p

# Create tables
source recommendation_schema.sql

# Populate sample data
source recommendation_sample_data.sql
```

### 2. Redis Installation & Setup

**On Windows (using WSL or Docker):**

```bash
# Using Docker
docker run -d -p 6379:6379 --name redis redis:latest

# Verify Redis is running
redis-cli ping
# Should return: PONG
```

**On Linux/Mac:**

```bash
# Install Redis
brew install redis  # macOS
sudo apt-get install redis-server  # Ubuntu/Debian

# Start Redis
redis-server

# Verify
redis-cli ping
```

### 3. Update Maven Dependencies

Already added to `pom.xml`:
- spring-boot-starter-data-redis
- jedis (Redis client)
- spring-boot-starter-cache

Run: `mvn clean install`

### 4. Start Spring Boot Application

```bash
cd commercecore
mvn spring-boot:run
```

Check console for: `Started CommercecoreApplication`

### 5. Verify Backend APIs

```bash
# Test Trending Products
curl http://localhost:8081/api/recommendations/trending?limit=5

# Test a specific product
curl http://localhost:8081/api/recommendations/product/1/frequently-bought-together?limit=5

# Test user recommendations
curl http://localhost:8081/api/recommendations/user/1/recommended-for-you?limit=5
```

Expected response format:
```json
{
  "recommendationType": "trending",
  "products": [...],
  "cachedAt": 1703001600000
}
```

## Frontend Setup (React)

### 1. Install Dependencies

```bash
cd commerce-frontend
npm install
```

### 2. Configure API Endpoint

Create `.env` file:
```
VITE_API_URL=http://localhost:8081
```

### 3. Use Recommendation Components

**In your product page:**

```tsx
import { useRecommendations } from '@/hooks/useRecommendations';
import RecommendationCarousel from '@/components/RecommendationCarousel';

const MyProductPage = () => {
  const { data, loading, fetchFrequentlyBoughtTogether } = useRecommendations();

  useEffect(() => {
    fetchFrequentlyBoughtTogether(productId);
  }, [productId]);

  return (
    <RecommendationCarousel
      title="Frequently Bought Together"
      products={data?.products || []}
      loading={loading}
      onProductClick={(id) => navigate(`/product/${id}`)}
    />
  );
};
```

### 4. Start Frontend

```bash
npm run dev
```

Visit: `http://localhost:5173`

## Testing Recommendation Features

### 1. Test Frequently Bought Together

```bash
# Ensure products exist and have sales data
curl http://localhost:8081/api/recommendations/product/1/frequently-bought-together
```

### 2. Test Trending Products

```bash
curl http://localhost:8081/api/recommendations/trending?limit=10
```

### 3. Track User Browsing

```bash
curl -X POST http://localhost:8081/api/recommendations/user/1/track-browse/5
```

### 4. Get Personalized Recommendations

```bash
curl http://localhost:8081/api/recommendations/user/1/recommended-for-you?limit=10
```

## Redis Cache Monitoring

```bash
# Connect to Redis CLI
redis-cli

# View all keys
KEYS *

# Check cache hit ratio
INFO stats

# View specific cache
GET "frequently_bought:1"

# Clear all caches
FLUSHALL

# Monitor live commands
MONITOR
```

## Performance Tuning

### Optimize Redis Settings

```properties
# In application.properties
spring.redis.jedis.pool.max-active=20
spring.redis.jedis.pool.max-idle=10
spring.redis.jedis.pool.min-idle=5
spring.redis.timeout=5000
spring.cache.redis.time-to-live=3600000
```

### Database Query Optimization

```sql
-- Check index effectiveness
EXPLAIN SELECT * FROM frequently_bought_together 
WHERE product_id = 1 
ORDER BY confidence_score DESC 
LIMIT 10;

-- Monitor slow queries (if enabled)
SHOW VARIABLES LIKE 'slow_query_log';
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;
```

### Monitor Cache Performance

```typescript
// Add to frontend for diagnostics
const { data, loading } = useRecommendations();

useEffect(() => {
  if (data?.cachedAt) {
    const cacheAge = Date.now() - data.cachedAt;
    console.log(`Data cached ${cacheAge}ms ago`);
  }
}, [data]);
```

## Troubleshooting

### Redis Connection Errors

```
Error: Error: connect ECONNREFUSED 127.0.0.1:6379
```

**Solution:**
```bash
# Check if Redis is running
redis-cli ping

# If not running, start it
redis-server

# Or check port
netstat -tuln | grep 6379
```

### Cache Not Working

```
# Clear Spring cache
# Add to controller or use Redis CLI
redis-cli FLUSHDB

# Restart application
```

### Slow Queries

```bash
# Check database indexes
SHOW INDEX FROM frequently_bought_together;

# If missing, add indexes
CREATE INDEX idx_product ON frequently_bought_together(product_id);
CREATE INDEX idx_confidence ON frequently_bought_together(confidence_score DESC);
```

### OutOfMemory Errors

**Reduce Redis memory usage:**

```properties
spring.cache.redis.time-to-live=1800000  # Reduce TTL to 30 minutes
```

**Or set Redis memory limits:**

```bash
# In Redis config or redis-cli
CONFIG SET maxmemory 512mb
CONFIG SET maxmemory-policy allkeys-lru
```

## Monitoring Dashboard Setup

### Using Redis Monitoring

```bash
# Terminal 1: Monitor Redis commands
redis-cli MONITOR

# Terminal 2: Send requests and watch the monitor
curl http://localhost:8081/api/recommendations/trending
```

### Database Query Logs

```sql
-- Enable slow query log
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 0.5;

-- View slow queries
tail -f /var/log/mysql/slow_query.log
```

## Production Deployment Checklist

- [ ] Redis deployed and replicated
- [ ] MySQL backups scheduled
- [ ] Query indexes verified with EXPLAIN
- [ ] Cache TTL optimized for load
- [ ] Rate limiting configured on APIs
- [ ] Error handling and logging setup
- [ ] Monitoring alerts configured
- [ ] Load testing completed
- [ ] Security audit completed
- [ ] SSL/TLS certificates installed

## Next Steps

1. **Integrate with Existing APIs** - Update order tracking to record purchases
2. **Add More Algorithms** - Implement collaborative filtering
3. **Setup Cron Jobs** - Periodically refresh trending products
4. **Implement Analytics** - Track recommendation click-through rates
5. **Add Admin Dashboard** - Monitor recommendation performance

## Support & Resources

- Spring Boot Caching: https://spring.io/guides/gs/caching/
- Redis Documentation: https://redis.io/docs/
- React Hooks: https://react.dev/reference/react/hooks
- Tailwind CSS: https://tailwindcss.com/docs
