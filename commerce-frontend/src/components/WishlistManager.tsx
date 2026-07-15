import { useState, useEffect } from 'react';
import { 
  Folder, Plus, Share2, Trash2, ShoppingBag, 
  TrendingUp, AlertTriangle, Check, X, MoreVertical,
  Filter, SortAsc, BarChart3
} from 'lucide-react';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { Card } from './ui/card';
import { ScrollArea } from './ui/scroll-area';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './ui/tabs';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from './ui/dialog';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Switch } from './ui/switch';
import { Textarea } from './ui/textarea';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from './ui/dropdown-menu';

interface WishlistItem {
  id: number;
  productId: number;
  productName: string;
  productImage: string;
  price: number;
  originalPrice: number;
  inStock: boolean;
  addedAt: string;
  priceDropAlert: boolean;
  stockAlert: boolean;
  alertPrice: number;
}

interface WishlistFolder {
  id: number;
  name: string;
  description: string;
  isDefault: boolean;
  isPublic: boolean;
  shareToken: string;
  createdAt: string;
  updatedAt: string;
  itemCount: number;
  items: WishlistItem[];
}

interface WishlistAnalytics {
  productId: number;
  productName: string;
  viewCount: number;
  moveToCartCount: number;
  lastViewedAt: string;
  lastMovedToCartAt: string;
}

export function WishlistManager() {
  const [folders, setFolders] = useState<WishlistFolder[]>([]);
  const [analytics, setAnalytics] = useState<WishlistAnalytics[]>([]);
  const [selectedFolder, setSelectedFolder] = useState<WishlistFolder | null>(null);
  const [selectedItems, setSelectedItems] = useState<number[]>([]);
  const [isCreateFolderOpen, setIsCreateFolderOpen] = useState(false);
  const [isShareDialogOpen, setIsShareDialogOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [newFolderName, setNewFolderName] = useState('');
  const [newFolderDesc, setNewFolderDesc] = useState('');

  useEffect(() => {
    fetchFolders();
    fetchAnalytics();
  }, []);

  const fetchFolders = async () => {
    setIsLoading(true);
    try {
      const response = await fetch('/api/wishlist/folders', {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      });
      if (response.ok) {
        const data = await response.json();
        setFolders(data);
        if (data.length > 0 && !selectedFolder) {
          setSelectedFolder(data[0]);
        }
      }
    } catch (error) {
      console.error('Failed to fetch folders:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const fetchAnalytics = async () => {
    try {
      const response = await fetch('/api/wishlist/analytics', {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      });
      if (response.ok) {
        const data = await response.json();
        setAnalytics(data);
      }
    } catch (error) {
      console.error('Failed to fetch analytics:', error);
    }
  };

  const createFolder = async () => {
    try {
      const response = await fetch('/api/wishlist/folders', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({
          name: newFolderName,
          description: newFolderDesc,
        }),
      });
      if (response.ok) {
        fetchFolders();
        setIsCreateFolderOpen(false);
        setNewFolderName('');
        setNewFolderDesc('');
      }
    } catch (error) {
      console.error('Failed to create folder:', error);
    }
  };

  const deleteFolder = async (folderId: number) => {
    try {
      const response = await fetch(`/api/wishlist/folders/${folderId}`, {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      });
      if (response.ok) {
        fetchFolders();
        if (selectedFolder?.id === folderId) {
          setSelectedFolder(null);
        }
      }
    } catch (error) {
      console.error('Failed to delete folder:', error);
    }
  };

  const toggleFolderPublic = async (folderId: number, isPublic: boolean) => {
    const folder = folders.find(f => f.id === folderId);
    if (!folder) return;

    try {
      const response = await fetch(`/api/wishlist/folders/${folderId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}` },
        body: JSON.stringify({
          name: folder.name,
          description: folder.description,
          isPublic,
        }),
      });
      if (response.ok) {
        fetchFolders();
      }
    } catch (error) {
      console.error('Failed to update folder:', error);
    }
  };

  const moveToCart = async (productId: number) => {
    if (!selectedFolder) return;
    try {
      const response = await fetch(
        `/api/wishlist/folders/${selectedFolder.id}/items/${productId}/move-to-cart`,
        {
          method: 'POST',
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
        }
      );
      if (response.ok) {
        fetchFolders();
        fetchAnalytics();
      }
    } catch (error) {
      console.error('Failed to move to cart:', error);
    }
  };

  const bulkMoveToCart = async () => {
    if (!selectedFolder || selectedItems.length === 0) return;
    try {
      const response = await fetch(
        `/api/wishlist/folders/${selectedFolder.id}/items/bulk/move-to-cart`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${localStorage.getItem('token')}` },
          body: JSON.stringify(selectedItems),
        }
      );
      if (response.ok) {
        fetchFolders();
        fetchAnalytics();
        setSelectedItems([]);
      }
    } catch (error) {
      console.error('Failed to bulk move to cart:', error);
    }
  };

  const bulkDelete = async () => {
    if (!selectedFolder || selectedItems.length === 0) return;
    try {
      const response = await fetch(
        `/api/wishlist/folders/${selectedFolder.id}/items/bulk`,
        {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${localStorage.getItem('token')}` },
          body: JSON.stringify(selectedItems),
        }
      );
      if (response.ok) {
        fetchFolders();
        setSelectedItems([]);
      }
    } catch (error) {
      console.error('Failed to bulk delete:', error);
    }
  };

  const toggleItemSelection = (itemId: number) => {
    setSelectedItems(prev =>
      prev.includes(itemId)
        ? prev.filter(id => id !== itemId)
        : [...prev, itemId]
    );
  };

  const copyShareLink = (shareToken: string) => {
    const link = `${window.location.origin}/shared-wishlist/${shareToken}`;
    navigator.clipboard.writeText(link);
  };

  return (
    <div className="container mx-auto p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-3xl font-bold">My Wishlist</h1>
          <p className="text-muted-foreground">
            Manage your saved products with folders and alerts
          </p>
        </div>
        <Dialog open={isCreateFolderOpen} onOpenChange={setIsCreateFolderOpen}>
          <DialogTrigger asChild>
            <Button>
              <Plus className="w-4 h-4 mr-2" />
              New Folder
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Create New Folder</DialogTitle>
            </DialogHeader>
            <div className="space-y-4">
              <div>
                <Label htmlFor="folderName">Folder Name</Label>
                <Input
                  id="folderName"
                  value={newFolderName}
                  onChange={(e) => setNewFolderName(e.target.value)}
                  placeholder="My Favorites"
                />
              </div>
              <div>
                <Label htmlFor="folderDesc">Description (Optional)</Label>
                <Textarea
                  id="folderDesc"
                  value={newFolderDesc}
                  onChange={(e) => setNewFolderDesc(e.target.value)}
                  placeholder="Products I'm interested in..."
                />
              </div>
              <Button onClick={createFolder} className="w-full">
                Create Folder
              </Button>
            </div>
          </DialogContent>
        </Dialog>
      </div>

      <Tabs defaultValue="items" className="space-y-6">
        <TabsList>
          <TabsTrigger value="items">Items</TabsTrigger>
          <TabsTrigger value="analytics">Analytics</TabsTrigger>
        </TabsList>

        <TabsContent value="items" className="space-y-6">
          <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
            <Card className="p-4">
              <h3 className="font-semibold mb-4 flex items-center gap-2">
                <Folder className="w-4 h-4" />
                Folders
              </h3>
              <ScrollArea className="h-[400px]">
                <div className="space-y-2">
                  {folders.map((folder) => (
                    <div
                      key={folder.id}
                      className={`p-3 rounded-lg cursor-pointer transition-colors ${
                        selectedFolder?.id === folder.id
                          ? 'bg-primary text-primary-foreground'
                          : 'hover:bg-muted'
                      }`}
                      onClick={() => setSelectedFolder(folder)}
                    >
                      <div className="flex items-center justify-between">
                        <div className="flex-1">
                          <div className="font-medium">{folder.name}</div>
                          <div className="text-sm opacity-70">{folder.itemCount} items</div>
                        </div>
                        {!folder.isDefault && (
                          <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                              <Button variant="ghost" size="icon" className="h-6 w-6">
                                <MoreVertical className="w-4 h-4" />
                              </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent>
                              <DropdownMenuItem onClick={() => toggleFolderPublic(folder.id, !folder.isPublic)}>
                                {folder.isPublic ? 'Make Private' : 'Make Public'}
                              </DropdownMenuItem>
                              {folder.isPublic && (
                                <DropdownMenuItem onClick={() => copyShareLink(folder.shareToken)}>
                                  <Share2 className="w-4 h-4 mr-2" />
                                  Copy Share Link
                                </DropdownMenuItem>
                              )}
                              <DropdownMenuItem 
                                onClick={() => deleteFolder(folder.id)}
                                className="text-destructive"
                              >
                                <Trash2 className="w-4 h-4 mr-2" />
                                Delete
                              </DropdownMenuItem>
                            </DropdownMenuContent>
                          </DropdownMenu>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </ScrollArea>
            </Card>

            <Card className="p-4 lg:col-span-3">
              <div className="flex items-center justify-between mb-4">
                <h3 className="font-semibold flex items-center gap-2">
                  <ShoppingBag className="w-4 h-4" />
                  {selectedFolder?.name || 'Select a folder'}
                </h3>
                {selectedItems.length > 0 && (
                  <div className="flex gap-2">
                    <Button variant="outline" size="sm" onClick={bulkMoveToCart}>
                      <ShoppingBag className="w-4 h-4 mr-2" />
                      Move to Cart ({selectedItems.length})
                    </Button>
                    <Button variant="destructive" size="sm" onClick={bulkDelete}>
                      <Trash2 className="w-4 h-4 mr-2" />
                      Delete ({selectedItems.length})
                    </Button>
                  </div>
                )}
              </div>

              {selectedFolder ? (
                <ScrollArea className="h-[400px]">
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {selectedFolder.items.map((item) => (
                      <Card key={item.id} className="p-4 relative">
                        <div className="absolute top-2 right-2">
                          <input
                            type="checkbox"
                            checked={selectedItems.includes(item.id)}
                            onChange={() => toggleItemSelection(item.id)}
                            className="w-4 h-4"
                          />
                        </div>
                        <img
                          src={item.productImage}
                          alt={item.productName}
                          className="w-full h-32 object-cover rounded-lg mb-3"
                        />
                        <h4 className="font-medium line-clamp-2 mb-2">{item.productName}</h4>
                        <div className="flex items-center gap-2 mb-2">
                          <span className="font-bold">${item.price}</span>
                          {item.originalPrice > item.price && (
                            <span className="text-sm text-muted-foreground line-through">
                              ${item.originalPrice}
                            </span>
                          )}
                        </div>
                        <div className="flex gap-2 mb-3">
                          <Badge variant={item.inStock ? "default" : "destructive"}>
                            {item.inStock ? 'In Stock' : 'Out of Stock'}
                          </Badge>
                          {item.priceDropAlert && (
                            <Badge variant="secondary">
                              <AlertTriangle className="w-3 h-3 mr-1" />
                              Price Alert
                            </Badge>
                          )}
                        </div>
                        <Button
                          size="sm"
                          className="w-full"
                          onClick={() => moveToCart(item.productId)}
                          disabled={!item.inStock}
                        >
                          <ShoppingBag className="w-4 h-4 mr-2" />
                          Add to Cart
                        </Button>
                      </Card>
                    ))}
                  </div>
                </ScrollArea>
              ) : (
                <div className="flex items-center justify-center h-[400px] text-muted-foreground">
                  Select a folder to view items
                </div>
              )}
            </Card>
          </div>
        </TabsContent>

        <TabsContent value="analytics">
          <Card className="p-6">
            <h3 className="font-semibold mb-4 flex items-center gap-2">
              <BarChart3 className="w-4 h-4" />
              Wishlist Analytics
            </h3>
            <div className="space-y-4">
              {analytics.map((item) => (
                <div key={item.productId} className="p-4 border rounded-lg">
                  <div className="flex items-center justify-between mb-2">
                    <h4 className="font-medium">{item.productName}</h4>
                    <div className="flex gap-4 text-sm">
                      <div className="flex items-center gap-1">
                        <TrendingUp className="w-4 h-4 text-blue-500" />
                        <span>{item.viewCount} views</span>
                      </div>
                      <div className="flex items-center gap-1">
                        <ShoppingBag className="w-4 h-4 text-green-500" />
                        <span>{item.moveToCartCount} moved to cart</span>
                      </div>
                    </div>
                  </div>
                  <div className="text-sm text-muted-foreground">
                    Last viewed: {new Date(item.lastViewedAt).toLocaleDateString()}
                  </div>
                </div>
              ))}
            </div>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
