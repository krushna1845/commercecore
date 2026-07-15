# Recommendation Engine - Deployment Checklist

## Pre-Deployment (Local Development)

### Backend Preparation
- [ ] Verify Java 17+ installed: `java -version`
- [ ] Verify Maven installed: `mvn -version`
- [ ] Check MySQL is running and accessible
- [ ] Create `commerce_db` database if not exists

### Database Setup
```bash
# 1. Initialize recommendation schema
mysql -u root -p commerce_db < recommendation_schema.sql

# 2. Load sample data (optional, for testing)
mysql -u root -p commerce_db < recommendation_sample_data.sql

# 3. Verify tables created
mysql -u root -p commerce_db -e "SHOW TABLES LIKE '%recommendation%' OR LIKE '%rating%' OR LIKE '%browsing%'"
```

### Redis Installation
- [ ] **Windows (WSL or Docker)**
  ```bash
  docker pull redis:latest
  docker run -d -p 6379:6379 --name redis redis:latest
  docker ps  # Verify running
  ```

- [ ] **macOS**
  ```bash
  brew install redis
  redis-server  # Start in terminal
  redis-cli ping  # Verify in another terminal
  ```

- [ ] **Linux**
  ```bash
  sudo apt-get install redis-server
  redis-server  # Start
  redis-cli ping  # Verify
  ```

### Backend Configuration
- [ ] Update `application.properties` with Redis settings (already done):
  ```properties
  spring.redis.host=localhost
  spring.redis.port=6379
  spring.cache.type=redis
  ```

- [ ] Update MySQL credentials if different:
  ```properties
  spring.datasource.username=root
  spring.datasource.password=Krish@123
  ```

### Build Backend
```bash
cd commercecore
mvn clean install
```

Expected output: `BUILD SUCCESS`

### Start Backend
```bash
mvn spring-boot:run
```

Expected console message:
```
Started CommercecoreApplication in XX.XXX seconds
```

### Verify Backend APIs
```bash
# Test trending endpoint
curl http://localhost:8081/api/recommendations/trending?limit=5

# Expected: 200 OK with recommendation JSON
```

### Frontend Preparation
- [ ] Verify Node.js installed: `node -v` (v16+)
- [ ] Verify npm installed: `npm -v`

### Build Frontend
```bash
cd commerce-frontend
npm install
npm run build
```

Expected output: `✓ built in XXXms`

### Start Frontend (Development)
```bash
npm run dev
```

Expected URL: `Local: http://localhost:5173/`

### Local Testing Checklist
- [ ] Homepage loads with trending products
- [ ] Product detail page shows recommendations
- [ ] Carousel navigation works (left/right buttons)
- [ ] Product cards display correctly
- [ ] No console errors
- [ ] Redis cache working (check with `redis-cli KEYS *`)

## Production Deployment

### Server Requirements
- [ ] Ubuntu 20+ or equivalent Linux
- [ ] Java 17 Runtime Environment
- [ ] MySQL 8.0+
- [ ] Redis 6.0+
- [ ] 2GB minimum RAM
- [ ] 50GB storage

### Database Deployment
```bash
# On production MySQL server
mysql -u prod_user -p prod_database < recommendation_schema.sql

# Verify
mysql -u prod_user -p prod_database -e "SELECT COUNT(*) as tables FROM information_schema.tables WHERE table_schema='prod_database';"
```

### Redis Deployment (Production)
- [ ] **Option 1: Self-Hosted**
  ```bash
  sudo apt-get install redis-server
  sudo systemctl start redis-server
  sudo systemctl enable redis-server
  
  # Configure persistence (optional)
  sudo nano /etc/redis/redis.conf
  # Uncomment: save 900 1  (save after 900s if 1 change)
  ```

- [ ] **Option 2: Managed Service**
  - [ ] Use AWS ElastiCache, Azure Cache, or similar
  - [ ] Update connection string in `application.properties`

### Backend Deployment
```bash
# Build
mvn clean package -DskipTests

# Deploy
cp target/commercecore-0.0.1-SNAPSHOT.war /opt/tomcat/webapps/
# OR
java -jar target/commercecore-0.0.1-SNAPSHOT.jar
```

### Frontend Deployment
```bash
# Build production bundle
npm run build

# Copy to web server
cp -r dist/* /var/www/html/
```

### Configuration for Production
```properties
# application.properties
spring.datasource.url=jdbc:mysql://prod-db-host:3306/commerce_db
spring.datasource.username=prod_user
spring.datasource.password=${DB_PASSWORD}
spring.redis.host=prod-redis-host
spring.redis.port=6379
spring.redis.password=${REDIS_PASSWORD}
server.port=8081
server.ssl.enabled=true
server.ssl.keystore=/path/to/keystore.p12
```

### Security Checklist
- [ ] Enable HTTPS/SSL
- [ ] Configure firewall rules (allow 8081 for API, 3000 for frontend)
- [ ] Set strong database credentials
- [ ] Set strong Redis password
- [ ] Enable authentication on all services
- [ ] Use environment variables for secrets (not in config files)
- [ ] Enable database backups
- [ ] Configure Redis persistence

### Monitoring Setup
- [ ] Configure application logging
- [ ] Set up Redis monitoring
  ```bash
  redis-cli MONITOR  # Real-time monitoring
  redis-cli INFO     # Statistics
  redis-cli CLIENT LIST  # Connected clients
  ```

- [ ] Configure MySQL slow query log
  ```sql
  SET GLOBAL slow_query_log = 'ON';
  SET GLOBAL long_query_time = 2;
  ```

- [ ] Add APM (New Relic, DataDog, etc.)
- [ ] Configure alerts for:
  - [ ] Redis connection failures
  - [ ] Database connection pool exhaustion
  - [ ] High response times
  - [ ] API errors (4xx, 5xx)

### Performance Tuning
- [ ] Configure Redis memory limits:
  ```bash
  redis-cli CONFIG SET maxmemory 1gb
  redis-cli CONFIG SET maxmemory-policy allkeys-lru
  ```

- [ ] Tune MySQL:
  ```sql
  SET GLOBAL innodb_buffer_pool_size = 4G;
  SET GLOBAL max_connections = 200;
  ```

- [ ] Verify indexes:
  ```sql
  ANALYZE TABLE frequently_bought_together;
  ANALYZE TABLE product_sales_frequency;
  ```

### Backup & Recovery
- [ ] Configure daily database backups:
  ```bash
  mysqldump -u root -p commerce_db > backup_$(date +%Y%m%d).sql
  ```

- [ ] Configure Redis persistence:
  - RDB snapshots (default)
  - AOF (Append Only File) for extra durability

- [ ] Test restore procedure

### Load Testing
- [ ] Test with Apache JMeter or similar
- [ ] Simulate expected traffic
- [ ] Monitor:
  - [ ] Response times
  - [ ] Cache hit ratio
  - [ ] Database query times
  - [ ] Memory usage

### DNS & CDN (Optional)
- [ ] Point domain to server
- [ ] Configure CDN for static assets (frontend)
- [ ] Set up CloudFront or similar

## Post-Deployment

### Verification
```bash
# Backend health check
curl https://yourdomain.com/api/recommendations/trending

# Frontend accessibility
curl https://yourdomain.com/

# Redis connectivity
redis-cli -h redis.yourdomain.com ping
```

### Monitoring
- [ ] Set up dashboards in monitoring tool
- [ ] Configure alert thresholds
- [ ] Daily log review for errors
- [ ] Weekly performance analysis

### Documentation
- [ ] Document infrastructure setup
- [ ] Update API documentation (Swagger)
- [ ] Create runbook for common issues
- [ ] Document backup/restore procedures

### User Communication
- [ ] Notify stakeholders of launch
- [ ] Provide user documentation
- [ ] Setup support channels
- [ ] Monitor user feedback

## Troubleshooting Checklist

### Application Won't Start
- [ ] Check logs: `tail -f /var/log/tomcat/catalina.out`
- [ ] Verify database connectivity
- [ ] Verify Redis connectivity
- [ ] Check port conflicts

### Slow Response Times
- [ ] Check Redis memory: `redis-cli INFO memory`
- [ ] Check MySQL slowlog
- [ ] Review cache hit ratio
- [ ] Check server load

### Recommendations Not Showing
- [ ] Verify sample data loaded
- [ ] Check Redis cache: `redis-cli KEYS *`
- [ ] Verify API endpoints
- [ ] Check browser console for errors
- [ ] Clear browser cache

### High Memory Usage
- [ ] Reduce Redis TTL
- [ ] Implement pagination limits
- [ ] Add more cache cleanup
- [ ] Increase server resources

## Rollback Procedure

If issues occur in production:
1. Revert frontend: `cp -r dist_backup/* /var/www/html/`
2. Revert backend: `git checkout main && mvn package`
3. Restart services: `systemctl restart tomcat`
4. Verify functionality
5. Check logs for root cause
6. Fix and re-deploy

## Success Criteria

✅ Production deployment is successful when:
- [ ] All endpoints respond within 200ms (p95)
- [ ] Cache hit ratio > 80%
- [ ] Zero recommendation errors in logs
- [ ] Frontend loads in < 3 seconds
- [ ] No database connection errors
- [ ] Redis memory stable
- [ ] All monitoring alerts functional

---

**Ready for Production**: After completing all checklists above, the recommendation engine is production-ready!
