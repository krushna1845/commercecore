# B2B2C Seller System - Deployment & Configuration Guide

## 🚀 Pre-Deployment Checklist

### Prerequisites
- [ ] Java 17+ installed
- [ ] PostgreSQL 12+ database running
- [ ] Node.js 18+ or Bun installed
- [ ] Git repository configured
- [ ] Maven build tool available

---

## 📦 Backend Deployment

### Step 1: Database Setup

```bash
# Connect to PostgreSQL
psql -U postgres

# Create database if not exists
CREATE DATABASE commercecore;

# Execute schema (if not already done)
\c commercecore
\i /path/to/schema.sql

# Execute seller system setup
\i /path/to/setup-seller-system.sql

# Verify setup
SELECT username, GROUP_CONCAT(name) as roles 
FROM users u 
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
GROUP BY u.id;
```

### Step 2: Backend Configuration

#### application.properties (or application.yml)
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/commercecore
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQL10Dialect

# Security
jwt.secret=your_jwt_secret_key_here
jwt.expiration=86400000

# Server
server.port=8081
server.servlet.context-path=/

# Cors
spring.web.cors.allowed-origins=http://localhost:5173,http://localhost:3000
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true
```

### Step 3: Build Backend

```bash
cd commercecore

# Clean build
mvn clean

# Compile and test
mvn compile
mvn test

# Package as JAR
mvn package

# Run application
java -jar target/commercecore-0.0.1-SNAPSHOT.jar

# Or run with Maven
mvn spring-boot:run
```

### Step 4: Verify Backend

```bash
# Test health endpoint
curl http://localhost:8081/actuator/health

# Test login
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"seller","password":"seller123"}'

# Test seller endpoint
curl http://localhost:8081/api/seller/dashboard \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🎨 Frontend Deployment

### Step 1: Install Dependencies

```bash
cd commerce-frontend

# Using npm
npm install

# Or using Bun (faster)
bun install
```

### Step 2: Environment Configuration

Create `.env.local` file:
```env
VITE_API_URL=http://localhost:8081
VITE_APP_NAME=CommerceCore
```

For production:
```env
VITE_API_URL=https://api.yourdomain.com
VITE_APP_NAME=CommerceCore
```

### Step 3: Build Frontend

```bash
# Development mode
npm run dev
# or
bun run dev

# Production build
npm run build
# or
bun run build

# Preview production build
npm run preview
```

### Step 4: Deploy Frontend

#### Option A: Static Hosting (Vercel, Netlify)
```bash
# Connect to Vercel
npm i -g vercel
vercel

# Deploy
vercel --prod
```

#### Option B: Nginx
```nginx
server {
    listen 80;
    server_name yourdomain.com;

    root /var/www/commercecore/commerce-frontend/dist;
    index index.html;

    # React Router fallback
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API proxy
    location /api {
        proxy_pass http://backend:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # HTTPS redirect
    if ($scheme != "https") {
        return 301 https://$server_name$request_uri;
    }
}
```

#### Option C: Docker
```dockerfile
# frontend/Dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "run", "preview"]
```

---

## 🐳 Docker Deployment

### Docker Compose Setup

```yaml
# docker-compose.yml
version: '3.8'

services:
  # PostgreSQL Database
  db:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: commercecore
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: your_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./setup-seller-system.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Backend Application
  backend:
    build:
      context: ./commercecore
      dockerfile: Dockerfile
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/commercecore
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: your_password
      JWT_SECRET: your_jwt_secret_key
    ports:
      - "8081:8081"
    depends_on:
      db:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  # Frontend Application
  frontend:
    build:
      context: ./commerce-frontend
      dockerfile: Dockerfile
    environment:
      VITE_API_URL: http://backend:8081
    ports:
      - "80:3000"
    depends_on:
      - backend

  # Nginx Reverse Proxy (Optional)
  nginx:
    image: nginx:alpine
    ports:
      - "443:443"
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl/cert.pem:/etc/nginx/ssl/cert.pem:ro
      - ./ssl/key.pem:/etc/nginx/ssl/key.pem:ro
    depends_on:
      - frontend
      - backend

volumes:
  postgres_data:
```

Deploy:
```bash
docker-compose up -d

# Check logs
docker-compose logs -f

# Stop
docker-compose down
```

---

## 🔐 Security Configuration

### JWT Secret Generation
```bash
# Generate strong JWT secret
openssl rand -base64 32
```

### HTTPS Configuration
```bash
# Generate self-signed certificate (for testing)
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes

# For production, use Let's Encrypt
certbot certonly --standalone -d yourdomain.com
```

### Environment Variables (Production)
```bash
# .env.production
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/commercecore
SPRING_DATASOURCE_USERNAME=dbuser
SPRING_DATASOURCE_PASSWORD=strong_password_here

JWT_SECRET=very_long_random_secret_key_here

SPRING_JPA_HIBERNATE_DDL_AUTO=validate

CORS_ALLOWED_ORIGINS=https://yourdomain.com

LOG_LEVEL=INFO
```

---

## 🔍 Post-Deployment Testing

### 1. Database Verification
```sql
-- Verify roles
SELECT * FROM roles;

-- Verify sellers
SELECT username, created_at FROM users 
WHERE id IN (SELECT DISTINCT user_id FROM user_roles WHERE role_id IN (SELECT id FROM roles WHERE name = 'ROLE_SELLER'));

-- Verify seller products
SELECT p.id, p.name, u.username as seller, p.price, p.stock_quantity
FROM products p
JOIN users u ON p.seller_id = u.id
ORDER BY u.username;
```

### 2. API Testing
```bash
# Test seller login
TOKEN=$(curl -s -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"seller","password":"seller123"}' | jq -r '.token')

# Test get seller products
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8081/api/seller/products | jq

# Test get seller dashboard
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8081/api/seller/dashboard | jq

# Test add product
curl -X POST http://localhost:8081/api/seller/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "description": "Test Description",
    "price": 99.99,
    "stockQuantity": 50,
    "imageUrl": "https://via.placeholder.com/400"
  }' | jq
```

### 3. Frontend Testing
```bash
# Navigate to seller dashboard
http://localhost:3000/seller

# Test add product
- Click "Add Product"
- Fill form fields
- Submit and verify product appears

# Test edit product
- Click Edit on a product
- Modify fields
- Submit and verify changes

# Test delete product
- Click Delete on a product
- Confirm deletion
- Verify product removed
```

### 4. Cross-Seller Protection Test
```bash
# Get token for seller1
TOKEN1=$(curl -s -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"seller","password":"seller123"}' | jq -r '.token')

# Get token for seller2
TOKEN2=$(curl -s -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"seller2","password":"seller123"}' | jq -r '.token')

# Get seller1's product ID
PRODUCT_ID=$(curl -s -H "Authorization: Bearer $TOKEN1" \
  http://localhost:8081/api/seller/products | jq '.[0].id')

# Try to update with seller2's token (should fail)
curl -X PUT http://localhost:8081/api/seller/products/$PRODUCT_ID \
  -H "Authorization: Bearer $TOKEN2" \
  -H "Content-Type: application/json" \
  -d '{"name":"Hacked Product","price":1}' 

# Expected: 404 or 403 error
```

---

## 📊 Monitoring & Logging

### Application Logs
```bash
# View backend logs
docker-compose logs -f backend

# View frontend logs
docker-compose logs -f frontend

# View database logs
docker-compose logs -f db
```

### Metrics Endpoint
```bash
# Spring Boot Actuator metrics
curl http://localhost:8081/actuator/metrics

# Specific metrics
curl http://localhost:8081/actuator/metrics/http.server.requests
```

### Logging Configuration
```properties
# application.properties
logging.level.root=INFO
logging.level.com.krushna.commercecore=DEBUG
logging.file.name=logs/application.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

---

## 🆘 Troubleshooting Deployment

### Issue: Database Connection Failed
```bash
# Check PostgreSQL is running
psql -U postgres -c "SELECT version();"

# Verify credentials in application.properties
# Check database exists
psql -U postgres -l | grep commercecore

# Recreate if needed
dropdb -U postgres commercecore
createdb -U postgres commercecore
```

### Issue: JWT Token Invalid
```bash
# Verify JWT_SECRET is same in all services
echo $JWT_SECRET

# Check token expiration
# Tokens valid for 24 hours (86400000ms)
# Refresh login if needed
```

### Issue: CORS Errors
```properties
# Verify CORS settings match frontend URL
spring.web.cors.allowed-origins=http://localhost:5173,http://yourdomain.com
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allow-credentials=true
```

### Issue: Products Not Showing
```sql
-- Verify products have seller_id
SELECT id, name, seller_id FROM products WHERE seller_id IS NOT NULL;

-- Verify seller exists
SELECT * FROM users WHERE id = {seller_id};
```

---

## 📈 Performance Optimization

### Database Indexing
```sql
-- Add indexes for faster queries
CREATE INDEX idx_products_seller_id ON products(seller_id);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_products_name ON products(name);
```

### Caching Configuration
```properties
spring.cache.type=redis
spring.redis.host=localhost
spring.redis.port=6379
```

### Connection Pooling
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

---

## 🔄 Continuous Deployment

### GitHub Actions Example
```yaml
name: Deploy

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Build Backend
        run: cd commercecore && mvn clean package
      
      - name: Build Frontend
        run: cd commerce-frontend && npm ci && npm run build
      
      - name: Deploy to Production
        run: |
          # Deploy commands here
          docker-compose -f docker-compose.prod.yml up -d
```

---

## 📝 Database Backup

```bash
# Backup database
pg_dump -U postgres commercecore > backup.sql

# Restore database
psql -U postgres commercecore < backup.sql

# Automated daily backup
0 2 * * * pg_dump -U postgres commercecore > /backups/commercecore_$(date +\%Y\%m\%d).sql
```

---

**Last Updated**: 2024
**Version**: 1.0
