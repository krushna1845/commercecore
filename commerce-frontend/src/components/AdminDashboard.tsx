import { useState, useEffect } from 'react';
import { adminApi } from '../services/adminApi';
import { 
  DollarSign, ShoppingCart, Users, Package, TrendingUp, 
  TrendingDown, BarChart3, LineChart, PieChart, Download, 
  Filter, Search, Moon, Sun, Printer, FileText, X
} from 'lucide-react';
import { Button } from './ui/button';
import { Card } from './ui/card';
import { Input } from './ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Badge } from './ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from './ui/table';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './ui/tabs';

interface AnalyticsData {
  revenue: {
    totalRevenue: number;
    currentMonthRevenue: number;
    previousMonthRevenue: number;
    revenueGrowth: number;
    averageOrderValue: number;
  };
  orders: {
    totalOrders: number;
    currentMonthOrders: number;
    previousMonthOrders: number;
    orderGrowth: number;
    pendingOrders: number;
    shippedOrders: number;
    deliveredOrders: number;
    cancelledOrders: number;
  };
  users: {
    totalUsers: number;
    currentMonthUsers: number;
    previousMonthUsers: number;
    userGrowth: number;
    activeUsers: number;
    inactiveUsers: number;
  };
  products: {
    totalProducts: number;
    activeProducts: number;
    inactiveProducts: number;
    outOfStock: number;
  };
  inventory: {
    totalInventory: number;
    lowStock: number;
    overStock: number;
    inventoryValue: number;
  };
  sales: {
    totalSales: number;
    currentMonthSales: number;
    previousMonthSales: number;
    salesGrowth: number;
  };
  growth: {
    revenueGrowth: number;
    orderGrowth: number;
    userGrowth: number;
    conversionGrowth: number;
  };
  conversion: {
    conversionRate: number;
    currentMonthConversion: number;
    previousMonthConversion: number;
    conversionGrowth: number;
  };
  topCustomers: Array<{
    userId: number;
    userName: string;
    email: string;
    totalSpent: number;
    orderCount: number;
  }>;
  bestSellers: Array<{
    productId: number;
    productName: string;
    imageUrl: string;
    totalRevenue: number;
    unitsSold: number;
  }>;
  monthlyRevenue: Array<{ month: string; value: number; count: number }>;
  monthlyOrders: Array<{ month: string; value: number; count: number }>;
  monthlyUsers: Array<{ month: string; value: number; count: number }>;
}

export function AdminDashboard() {
  const [analytics, setAnalytics] = useState<AnalyticsData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [darkMode, setDarkMode] = useState(false);
  const [dateRange, setDateRange] = useState('30');
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    fetchAnalytics();
  }, [dateRange]);

  const fetchAnalytics = async () => {
    setIsLoading(true);
    try {
      const data = await adminApi.analytics();
      setAnalytics(data);
    } catch (error) {
      console.error('Failed to fetch analytics:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const toggleDarkMode = () => {
    setDarkMode(!darkMode);
    document.documentElement.classList.toggle('dark');
  };

  const exportToPDF = () => {
    window.print();
  };

  const exportToExcel = () => {
    if (!analytics) return;
    
    const csvContent = [
      ['Metric', 'Value'],
      ['Total Revenue', analytics.revenue.totalRevenue.toFixed(2)],
      ['Total Orders', analytics.orders.totalOrders],
      ['Total Users', analytics.users.totalUsers],
      ['Total Products', analytics.products.totalProducts],
      ['Conversion Rate', analytics.conversion.conversionRate.toFixed(2) + '%'],
      ['', ''],
      ['Top Customers', ''],
      ...analytics.topCustomers.map(c => [c.userName, c.totalSpent.toFixed(2)]),
      ['', ''],
      ['Best Sellers', ''],
      ...analytics.bestSellers.map(p => [p.productName, p.unitsSold]),
    ].map(row => row.join(',')).join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `admin-analytics-${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
  };

  const StatCard = ({ title, value, change, icon: Icon, positive }: any) => (
    <Card className="p-6">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm text-muted-foreground">{title}</p>
          <p className="text-2xl font-bold mt-1">{typeof value === 'number' ? value.toLocaleString() : value}</p>
          {change !== undefined && (
            <div className={`flex items-center mt-2 text-sm ${positive ? 'text-green-600' : 'text-red-600'}`}>
              {positive ? <TrendingUp className="w-4 h-4 mr-1" /> : <TrendingDown className="w-4 h-4 mr-1" />}
              {Math.abs(change).toFixed(1)}%
            </div>
          )}
        </div>
        <div className={`p-3 rounded-full ${positive ? 'bg-green-100 dark:bg-green-900/20' : 'bg-red-100 dark:bg-red-900/20'}`}>
          <Icon className={`w-6 h-6 ${positive ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'}`} />
        </div>
      </div>
    </Card>
  );

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-primary"></div>
      </div>
    );
  }

  if (!analytics) {
    return <div className="p-6">Failed to load analytics data</div>;
  }

  return (
    <div className={`min-h-screen ${darkMode ? 'dark' : ''}`}>
      <div className="container mx-auto p-6">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-3xl font-bold">Admin Dashboard</h1>
            <p className="text-muted-foreground mt-1">Overview of your e-commerce platform</p>
          </div>
          <div className="flex items-center gap-4">
            <Select value={dateRange} onValueChange={setDateRange}>
              <SelectTrigger className="w-[180px]">
                <SelectValue placeholder="Select date range" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="7">Last 7 days</SelectItem>
                <SelectItem value="30">Last 30 days</SelectItem>
                <SelectItem value="90">Last 90 days</SelectItem>
                <SelectItem value="365">Last year</SelectItem>
              </SelectContent>
            </Select>
            <Button variant="outline" onClick={toggleDarkMode}>
              {darkMode ? <Sun className="w-4 h-4" /> : <Moon className="w-4 h-4" />}
            </Button>
            <Button variant="outline" onClick={exportToPDF}>
              <Printer className="w-4 h-4 mr-2" />
              PDF
            </Button>
            <Button variant="outline" onClick={exportToExcel}>
              <Download className="w-4 h-4 mr-2" />
              Excel
            </Button>
          </div>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <StatCard
            title="Total Revenue"
            value={`$${analytics.revenue.totalRevenue.toFixed(2)}`}
            change={analytics.revenue.revenueGrowth}
            positive={analytics.revenue.revenueGrowth >= 0}
            icon={DollarSign}
          />
          <StatCard
            title="Total Orders"
            value={analytics.orders.totalOrders}
            change={analytics.orders.orderGrowth}
            positive={analytics.orders.orderGrowth >= 0}
            icon={ShoppingCart}
          />
          <StatCard
            title="Total Users"
            value={analytics.users.totalUsers}
            change={analytics.users.userGrowth}
            positive={analytics.users.userGrowth >= 0}
            icon={Users}
          />
          <StatCard
            title="Conversion Rate"
            value={`${analytics.conversion.conversionRate.toFixed(2)}%`}
            change={analytics.conversion.conversionGrowth}
            positive={analytics.conversion.conversionGrowth >= 0}
            icon={TrendingUp}
          />
        </div>

        {/* Charts Section */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          <Card className="p-6">
            <h3 className="text-lg font-semibold mb-4">Monthly Revenue</h3>
            <div className="h-[300px] flex items-end gap-2">
              {analytics.monthlyRevenue.map((item, index) => {
                const maxValue = Math.max(...analytics.monthlyRevenue.map(d => d.value));
                const height = maxValue > 0 ? (item.value / maxValue) * 100 : 0;
                return (
                  <div key={index} className="flex-1 flex flex-col items-center">
                    <div 
                      className="w-full bg-primary rounded-t transition-all hover:opacity-80"
                      style={{ height: `${height}%` }}
                    />
                    <div className="text-xs text-muted-foreground mt-2 text-center">
                      {item.month}
                    </div>
                    <div className="text-xs font-medium">
                      ${item.value.toFixed(0)}
                    </div>
                  </div>
                );
              })}
            </div>
          </Card>

          <Card className="p-6">
            <h3 className="text-lg font-semibold mb-4">Order Status Distribution</h3>
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-sm">Pending</span>
                <div className="flex items-center gap-2">
                  <div className="w-32 h-2 bg-muted rounded-full overflow-hidden">
                    <div 
                      className="h-full bg-yellow-500"
                      style={{ width: `${(analytics.orders.pendingOrders / analytics.orders.totalOrders) * 100}%` }}
                    />
                  </div>
                  <span className="text-sm font-medium">{analytics.orders.pendingOrders}</span>
                </div>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm">Shipped</span>
                <div className="flex items-center gap-2">
                  <div className="w-32 h-2 bg-muted rounded-full overflow-hidden">
                    <div 
                      className="h-full bg-blue-500"
                      style={{ width: `${(analytics.orders.shippedOrders / analytics.orders.totalOrders) * 100}%` }}
                    />
                  </div>
                  <span className="text-sm font-medium">{analytics.orders.shippedOrders}</span>
                </div>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm">Delivered</span>
                <div className="flex items-center gap-2">
                  <div className="w-32 h-2 bg-muted rounded-full overflow-hidden">
                    <div 
                      className="h-full bg-green-500"
                      style={{ width: `${(analytics.orders.deliveredOrders / analytics.orders.totalOrders) * 100}%` }}
                    />
                  </div>
                  <span className="text-sm font-medium">{analytics.orders.deliveredOrders}</span>
                </div>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm">Cancelled</span>
                <div className="flex items-center gap-2">
                  <div className="w-32 h-2 bg-muted rounded-full overflow-hidden">
                    <div 
                      className="h-full bg-red-500"
                      style={{ width: `${(analytics.orders.cancelledOrders / analytics.orders.totalOrders) * 100}%` }}
                    />
                  </div>
                  <span className="text-sm font-medium">{analytics.orders.cancelledOrders}</span>
                </div>
              </div>
            </div>
          </Card>
        </div>

        {/* Tabs for detailed views */}
        <Tabs defaultValue="customers" className="mb-8">
          <TabsList>
            <TabsTrigger value="customers">Top Customers</TabsTrigger>
            <TabsTrigger value="products">Best Sellers</TabsTrigger>
            <TabsTrigger value="inventory">Inventory</TabsTrigger>
          </TabsList>

          <TabsContent value="customers">
            <Card className="p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold">Top Customers</h3>
                <Input
                  placeholder="Search customers..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-[250px]"
                />
              </div>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Customer</TableHead>
                    <TableHead>Email</TableHead>
                    <TableHead>Orders</TableHead>
                    <TableHead className="text-right">Total Spent</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {analytics.topCustomers
                    .filter(c => c.userName.toLowerCase().includes(searchQuery.toLowerCase()))
                    .map((customer) => (
                      <TableRow key={customer.userId}>
                        <TableCell className="font-medium">{customer.userName}</TableCell>
                        <TableCell>{customer.email}</TableCell>
                        <TableCell>{customer.orderCount}</TableCell>
                        <TableCell className="text-right font-semibold">
                          ${customer.totalSpent.toFixed(2)}
                        </TableCell>
                      </TableRow>
                    ))}
                </TableBody>
              </Table>
            </Card>
          </TabsContent>

          <TabsContent value="products">
            <Card className="p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold">Best Selling Products</h3>
                <Input
                  placeholder="Search products..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-[250px]"
                />
              </div>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Product</TableHead>
                    <TableHead>Units Sold</TableHead>
                    <TableHead className="text-right">Revenue</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {analytics.bestSellers
                    .filter(p => p.productName.toLowerCase().includes(searchQuery.toLowerCase()))
                    .map((product) => (
                      <TableRow key={product.productId}>
                        <TableCell className="font-medium">{product.productName}</TableCell>
                        <TableCell>{product.unitsSold}</TableCell>
                        <TableCell className="text-right font-semibold">
                          ${product.totalRevenue.toFixed(2)}
                        </TableCell>
                      </TableRow>
                    ))}
                </TableBody>
              </Table>
            </Card>
          </TabsContent>

          <TabsContent value="inventory">
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <Card className="p-6">
                <div className="flex items-center gap-3">
                  <Package className="w-8 h-8 text-blue-600" />
                  <div>
                    <p className="text-sm text-muted-foreground">Total Inventory</p>
                    <p className="text-2xl font-bold">{analytics.inventory.totalInventory}</p>
                  </div>
                </div>
              </Card>
              <Card className="p-6">
                <div className="flex items-center gap-3">
                  <TrendingDown className="w-8 h-8 text-yellow-600" />
                  <div>
                    <p className="text-sm text-muted-foreground">Low Stock</p>
                    <p className="text-2xl font-bold">{analytics.inventory.lowStock}</p>
                  </div>
                </div>
              </Card>
              <Card className="p-6">
                <div className="flex items-center gap-3">
                  <TrendingUp className="w-8 h-8 text-green-600" />
                  <div>
                    <p className="text-sm text-muted-foreground">Over Stock</p>
                    <p className="text-2xl font-bold">{analytics.inventory.overStock}</p>
                  </div>
                </div>
              </Card>
              <Card className="p-6">
                <div className="flex items-center gap-3">
                  <DollarSign className="w-8 h-8 text-purple-600" />
                  <div>
                    <p className="text-sm text-muted-foreground">Inventory Value</p>
                    <p className="text-2xl font-bold">${analytics.inventory.inventoryValue.toFixed(0)}</p>
                  </div>
                </div>
              </Card>
            </div>

            <Card className="p-6 mt-6">
              <h3 className="text-lg font-semibold mb-4">Product Status</h3>
              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <span className="text-sm">Active Products</span>
                  <Badge variant="default">{analytics.products.activeProducts}</Badge>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-sm">Inactive Products</span>
                  <Badge variant="secondary">{analytics.products.inactiveProducts}</Badge>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-sm">Out of Stock</span>
                  <Badge variant="destructive">{analytics.products.outOfStock}</Badge>
                </div>
              </div>
            </Card>
          </TabsContent>
        </Tabs>

        {/* Growth Metrics */}
        <Card className="p-6">
          <h3 className="text-lg font-semibold mb-4">Growth Metrics</h3>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
            <div>
              <p className="text-sm text-muted-foreground mb-2">Revenue Growth</p>
              <div className={`text-2xl font-bold ${analytics.growth.revenueGrowth >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                {analytics.growth.revenueGrowth >= 0 ? '+' : ''}{analytics.growth.revenueGrowth.toFixed(1)}%
              </div>
            </div>
            <div>
              <p className="text-sm text-muted-foreground mb-2">Order Growth</p>
              <div className={`text-2xl font-bold ${analytics.growth.orderGrowth >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                {analytics.growth.orderGrowth >= 0 ? '+' : ''}{analytics.growth.orderGrowth.toFixed(1)}%
              </div>
            </div>
            <div>
              <p className="text-sm text-muted-foreground mb-2">User Growth</p>
              <div className={`text-2xl font-bold ${analytics.growth.userGrowth >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                {analytics.growth.userGrowth >= 0 ? '+' : ''}{analytics.growth.userGrowth.toFixed(1)}%
              </div>
            </div>
            <div>
              <p className="text-sm text-muted-foreground mb-2">Conversion Growth</p>
              <div className={`text-2xl font-bold ${analytics.growth.conversionGrowth >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                {analytics.growth.conversionGrowth >= 0 ? '+' : ''}{analytics.growth.conversionGrowth.toFixed(1)}%
              </div>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
}
