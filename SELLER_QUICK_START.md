# Seller System Quick Reference Guide

## 🎯 For Sellers

### Getting Started

1. **Create Your Account**
   - Go to Register page
   - Enter username and password
   - You'll have ROLE_USER by default

2. **Request Seller Access**
   - Contact admin to upgrade your account to ROLE_SELLER
   - Or use a pre-created seller account

3. **Access Your Seller Portal**
   - Login with your seller account
   - Navigate to `/seller` URL
   - You'll see your dashboard

### Seller Dashboard Tabs

#### 📊 Overview Tab
- **Total Revenue**: Sum of all product sales
- **Total Orders**: Number of orders
- **Active Products**: Count of listed products
- **Available Payout**: Money ready to withdraw
- **Monthly Revenue Chart**: Visual revenue trends
- **Recent Orders**: Latest order activity

#### 📦 Products Tab
- **Add Product Button**: Click to create new product
- **Product Search**: Filter your products by name
- **Product Table**:
  - Product name with thumbnail image
  - Current price
  - Stock quantity (red if < 10 units)
  - Edit button (pencil icon)
  - Delete button (trash icon)

**Adding a Product:**
1. Click "Add Product" button
2. Fill in required fields:
   - Product Name *
   - Price *
   - Stock Quantity *
3. Optional fields:
   - Brand
   - Description
   - Original Price (for discounts)
   - Image URL
   - Warranty period
   - Return policy
4. Click "Add Product"

**Editing a Product:**
1. Click Edit button (pencil icon) on product
2. Modal opens with pre-filled data
3. Update fields as needed
4. Click "Update Product"

**Deleting a Product:**
1. Click Delete button (trash icon)
2. Confirm deletion
3. Product removed from inventory

#### 📋 Orders Tab
- **View All Orders**: All customer orders for your products
- **Order Search**: Filter by order number or customer name
- **Order Status Filter**: View by Pending, Shipped, Cancelled
- **Order Details**: Order number, customer, amount, status, date
- **Actions**: View order, mark as complete

#### 💰 Payouts Tab
- **Available Payout**: Amount ready to withdraw
- **Pending Payout**: Amount being processed
- **Current Month Revenue**: This month's earnings
- **Request Payout**: Submit payout request (enabled if balance > $0)
- **Payout History**: Track all past payouts

#### 📈 Analytics Tab
- **Order Status Distribution**: Visual breakdown of order statuses
- **Inventory Status**: Total inventory count, low stock alerts
- **Performance Reports**: Export analytics data

---

## 🛠️ For Administrators

### Admin Functions

1. **User Management**
   - Create/update user accounts
   - Assign/revoke roles
   - Grant ROLE_SELLER access to users

2. **Monitor Sellers**
   - View seller performance
   - Access seller analytics
   - Monitor product listings

3. **System Configuration**
   - Manage commission rates
   - Configure payout settings
   - Set seller verification requirements

### Test Accounts

**Admin Account:**
```
Username: admin
Password: Admin@123
Role: ROLE_ADMIN
URL: /admin (admin dashboard)
```

**Seller Accounts (Pre-created):**
```
Username: seller
Password: seller123
Role: ROLE_SELLER
URL: /seller (seller dashboard)

Username: seller2
Password: seller123
Role: ROLE_SELLER
URL: /seller (seller dashboard)

Username: seller3
Password: seller123
Role: ROLE_SELLER
URL: /seller (seller dashboard)
```

**Regular User Account:**
```
Username: user
Password: user123
Role: ROLE_USER
URL: / (main store)
```

---

## 🔍 Seller Features Checklist

- [x] Create seller account with ROLE_SELLER
- [x] Access protected `/seller` dashboard
- [x] Add new products with full details
- [x] Edit existing products
- [x] Delete products
- [x] View all owned products in inventory
- [x] See product images/thumbnails
- [x] Track low stock items (red badge < 10 units)
- [x] View customer orders for products
- [x] Filter and search products/orders
- [x] View revenue analytics
- [x] Request payouts
- [x] Track monthly revenue
- [x] See order status distribution
- [x] Monitor inventory levels
- [x] Download performance reports

---

## 🌐 Customer View Features

- [x] Browse products from all sellers
- [x] See "Sold by: [Seller Name]" badge on each product
- [x] Search includes products from all sellers
- [x] Product details show seller information
- [x] Can purchase from any seller
- [x] Seller information visible in order details

---

## 🔒 Security & Access Control

### What Each Role Can Do

**ROLE_ADMIN:**
- Access admin dashboard
- View all users
- Manage all products
- View all orders
- Configure system settings

**ROLE_SELLER:**
- Access seller dashboard at `/seller`
- Create, read, update, delete OWN products only
- Cannot modify other sellers' products
- View orders for own products
- Request payouts
- View own analytics

**ROLE_USER:**
- Browse products
- Add to cart
- Place orders
- View own orders
- Cannot access seller or admin panels

---

## 📱 Product Image Upload

When adding/editing products:
1. Enter image URL (e.g., from Cloudinary)
2. Image preview shown in modal
3. Supports standard image formats (JPG, PNG, etc.)
4. Image displays on product cards in main store

Example image URLs:
```
https://via.placeholder.com/400x400?text=Product
https://res.cloudinary.com/your-account/image/upload/v123/image.jpg
```

---

## ⚠️ Important Notes

1. **Seller Isolation**: Each seller can ONLY see/edit their own products
2. **Legacy Products**: Products created before seller system are assigned to admin
3. **Price Validation**: Prices must be positive numbers
4. **Stock Validation**: Stock quantity must be non-negative
5. **Deletion Warning**: Deleted products cannot be recovered
6. **Payout Minimum**: Payout requests only available if balance > $0

---

## 🐛 Troubleshooting

**Problem: Can't access /seller page**
- Solution: Verify you have ROLE_SELLER (check user roles in database)
- Contact admin if needed

**Problem: Can't see own products**
- Solution: Products must be created by your logged-in account
- Check if you're logged in as the correct seller

**Problem: Can't edit/delete other seller's products**
- Solution: This is intentional! Each seller can only manage their own products
- This is a security feature

**Problem: Image not showing on product**
- Solution: Verify image URL is valid and accessible
- Try a test image URL from placeholder service

**Problem: Price or stock rejected**
- Solution: Verify prices are positive and stock is non-negative numbers
- Check for special characters or text in number fields

---

## 📞 Support

For issues or questions:
1. Check this guide first
2. Contact the admin team
3. Check application logs for error details

---

## 🚀 Next Features (Coming Soon)

- Seller ratings and reviews
- Product analytics dashboard
- Bulk product upload
- Automated inventory alerts
- Commission breakdown
- Tax calculation
- Shipping integration
- Marketing tools for sellers

---

**Last Updated**: 2024
**Version**: 1.0
