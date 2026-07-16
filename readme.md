# 🛒 CommerceCore

> A production-ready full-stack E-Commerce platform built using **Spring Boot**, **React**, **MySQL**, and **AWS EC2**.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.x-brightgreen)
![React](https://img.shields.io/badge/React-19-blue)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
![JWT](https://img.shields.io/badge/Auth-JWT-success)
![AWS](https://img.shields.io/badge/Hosted-AWS%20EC2-orange)
![License](https://img.shields.io/badge/License-MIT-green)

---

## 🌐 Live Demo

**Application:** http://16.16.79.193/

---

## 📖 Overview

CommerceCore is a modern, scalable, full-stack e-commerce platform designed to simulate a real-world online marketplace.

The project supports multiple user roles, secure authentication, seller management, inventory tracking, shopping cart functionality, product reviews, recommendation features, analytics dashboards, and order management.

The goal of this project was to gain practical experience in building enterprise-level backend systems while integrating them with a modern React frontend and deploying the complete application on AWS EC2.

---

# ✨ Features

## 👤 User

- User Registration
- JWT Authentication
- Login / Logout
- User Profile
- Address Management
- Wishlist
- Shopping Cart
- Checkout
- Order History
- Product Search
- Product Comparison
- Product Reviews
- Ratings
- Notifications
- Personalized Recommendations

---

## 🛍 Product Management

- Product Catalog
- Product Categories
- Product Details
- Search Products
- Pagination
- Sorting
- Product Recommendations
- Frequently Bought Together
- Related Products

---

## 🛒 Shopping Cart

- Add to Cart
- Remove Items
- Quantity Update
- Cart Summary
- Coupon Support
- Checkout

---

## 💳 Order Management

- Place Orders
- Order Tracking
- Order History
- Order Status
- Payment Integration Ready
- Seller Orders

---

## ⭐ Reviews

- Product Reviews
- Review Images
- Review Videos
- Review Replies
- Review Voting
- Review Analytics

---

## ❤️ Wishlist

- Wishlist
- Wishlist Folders
- Save Products
- Move Between Folders

---

## 📦 Inventory

- Inventory Management
- Warehouse Support
- Stock Tracking
- Stock Movement
- Seller Inventory

---

## 🏪 Seller Dashboard

- Seller Product Management
- Upload Products
- Analytics Dashboard
- Sales Reports
- Inventory Reports
- Order Management
- Payout Tracking

---

## 👑 Admin Dashboard

- User Management
- Product Approval
- Category Management
- Coupons
- Analytics Dashboard
- Platform Statistics

---

## 🤖 Smart Features

- AI Assistant API
- Product Recommendations
- Search Suggestions
- Category Similarity
- Recently Viewed Products

---

## 🔔 Notifications

- Email Preferences
- Push Notifications
- SMS Preferences
- Notification Center

---

# 🏗 Architecture

```
                     React Frontend
                           │
                           │ REST API
                           ▼
                 Spring Boot Backend
                           │
        ┌──────────────────┼───────────────────┐
        │                  │                   │
      MySQL             JWT Auth          WebSocket
        │                  │                   │
        └──────────────────┼───────────────────┘
                           │
                    AWS EC2 Deployment
```

---

# 🛠 Tech Stack

### Frontend

- React
- JavaScript
- Tailwind CSS
- Axios
- React Router

### Backend

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- JWT Authentication
- Maven

### Database

- MySQL

### DevOps

- AWS EC2
- Ubuntu Linux
- Systemd
- Git
- GitHub

---

# 📂 Project Structure

```
CommerceCore
│
├── commerce-frontend
│     ├── src
│     ├── components
│     ├── pages
│     ├── services
│     └── context
│
├── commercecore
│     ├── controller
│     ├── service
│     ├── repository
│     ├── entity
│     ├── dto
│     ├── security
│     ├── config
│     └── util
│
└── README.md
```

---

# 🔐 Authentication

The project uses JWT-based authentication.

Roles include:

- USER
- SELLER
- ADMIN

Protected endpoints are secured using Spring Security.

---

# 📸 Screenshots

## Home

![Home](screenshots/home.png)

---

## Product Listing

![Products](screenshots/products.png)

---

## Product Details

![Details](screenshots/product-details.png)

---

## Shopping Cart

![Cart](screenshots/cart.png)

---

## Seller Dashboard

![Seller](screenshots/seller-dashboard.png)

---

## Admin Dashboard

![Admin](screenshots/admin-dashboard.png)

---

# 🚀 Running Locally

## Clone Repository

```bash
git clone https://github.com/krushna1845/commercecore.git
```

Backend

```bash
cd commercecore

mvn clean install

mvn spring-boot:run
```

Frontend

```bash
cd commerce-frontend

npm install

npm run dev
```

---

# Database

Create MySQL database

```
commercecore
```

Configure

```
application.properties
```

with your MySQL credentials.

---

# Deployment

The project is deployed on

- AWS EC2
- Ubuntu
- Java 17
- MySQL
- Spring Boot
- React

Live Demo:

http://16.16.79.193/

---

# Future Improvements

- Docker
- Docker Compose
- CI/CD using GitHub Actions
- Redis Caching
- Elasticsearch
- Payment Gateway Integration
- Microservices Architecture
- Kubernetes Deployment
- HTTPS with Nginx
- Monitoring with Prometheus & Grafana

---

# Learning Outcomes

Through this project I gained practical experience in:

- Spring Boot Development
- REST API Design
- JWT Authentication
- Database Design
- React Development
- AWS Deployment
- Linux Server Management
- Production Deployment
- Full Stack Development
- Software Architecture

---

# Author

**Krushna Malode**

Backend Developer | Java | Spring Boot | React | AWS | MySQL

GitHub:
https://github.com/krushna1845

LinkedIn:
https://www.linkedin.com/in/krushna-malode-614471374/

---

# ⭐ Support

If you found this project useful,

⭐ Star the repository

🍴 Fork the repository

📢 Share your feedback

---

## Made with ❤️ using Java, Spring Boot and React.
