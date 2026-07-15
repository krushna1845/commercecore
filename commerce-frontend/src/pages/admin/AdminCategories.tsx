import { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2, Loader2 } from 'lucide-react';
import { AdminLayout } from '@/layouts/AdminLayout';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { adminApi } from '@/services/adminApi';
import { toast } from 'sonner';

interface Category {
  id: number;
  name: string;
  description?: string;
  productCount?: number;
}

const getCategoryStyling = (name: string) => {
  const hash = (name || '').split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
  const colors = [
    'bg-blue-50 text-blue-600 dark:bg-blue-950/30 dark:text-blue-400',
    'bg-purple-50 text-purple-600 dark:bg-purple-950/30 dark:text-purple-400',
    'bg-amber-50 text-amber-600 dark:bg-amber-950/30 dark:text-amber-400',
    'bg-green-50 text-green-600 dark:bg-green-950/30 dark:text-green-400',
    'bg-rose-50 text-rose-600 dark:bg-rose-950/30 dark:text-rose-400',
    'bg-indigo-50 text-indigo-600 dark:bg-indigo-950/30 dark:text-indigo-400',
  ];
  const icons = ['📱', '👕', '🏠', '💄', '📚', '⚽', '🚗', '🍔', '🎁', '📦', '⚙️', '💡'];
  return {
    color: colors[hash % colors.length],
    icon: icons[hash % icons.length],
  };
};

const emptyForm = { name: '', description: '' };

export default function AdminCategories() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [editId, setEditId] = useState<number | null>(null);
  const [saving, setSaving] = useState(false);
  const [delTarget, setDelTarget] = useState<number | null>(null);

  const load = () => {
    setLoading(true);
    adminApi.categories()
      .then(res => setCategories(res))
      .catch(err => toast.error(err.message || 'Failed to load categories'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const openAdd = () => { setForm(emptyForm); setEditId(null); setDialogOpen(true); };
  const openEdit = (c: Category) => {
    setForm({ name: c.name, description: c.description || '' });
    setEditId(c.id);
    setDialogOpen(true);
  };

  const handleSave = async () => {
    if (!form.name.trim()) {
      toast.error('Category name is required');
      return;
    }
    setSaving(true);
    try {
      if (editId) {
        await adminApi.updateCategory(editId, form);
        toast.success('Category updated');
      } else {
        await adminApi.createCategory(form);
        toast.success('Category created');
      }
      setDialogOpen(false);
      load();
    } catch (err: any) {
      toast.error(err.message || 'Failed to save category');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!delTarget) return;
    try {
      await adminApi.deleteCategory(delTarget);
      toast.success('Category deleted');
      setDelTarget(null);
      load();
    } catch (err: any) {
      toast.error(err.message || 'Failed to delete category');
    }
  };

  return (
    <AdminLayout 
      title="Categories" 
      action={<Button size="sm" onClick={openAdd}><Plus size={14} className="mr-1" />New Category</Button>}
    >
      {loading ? (
        <div className="flex justify-center py-20"><Loader2 className="animate-spin text-primary" size={32} /></div>
      ) : categories.length === 0 ? (
        <div className="text-center py-12 text-muted-foreground text-sm border border-dashed rounded-2xl">
          No categories found. Click "New Category" to create one.
        </div>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
          {categories.map(c => {
            const styling = getCategoryStyling(c.name);
            return (
              <div key={c.id} className="rounded-2xl border bg-card p-5 text-center flex flex-col justify-between card-hover relative group">
                <div>
                  <div className={`mx-auto grid h-16 w-16 place-items-center rounded-2xl text-3xl ${styling.color}`}>
                    {styling.icon}
                  </div>
                  <div className="mt-3 font-medium capitalize">{c.name}</div>
                  <div className="text-xs text-muted-foreground mt-1 line-clamp-2 min-h-[2rem]">
                    {c.description || 'No description provided.'}
                  </div>
                </div>
                <div className="mt-4 flex justify-center gap-2">
                  <Button size="sm" variant="outline" className="text-xs h-7" onClick={() => openEdit(c)}>Edit</Button>
                  <Button size="sm" variant="ghost" className="text-xs h-7 text-destructive" onClick={() => setDelTarget(c.id)}>Delete</Button>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Add/Edit Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>{editId ? 'Edit Category' : 'Add New Category'}</DialogTitle>
            <DialogDescription>Define the name and details of the category.</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-2">
            <div className="space-y-1.5">
              <Label>Category Name *</Label>
              <Input value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} placeholder="e.g. Smart Home" />
            </div>
            <div className="space-y-1.5">
              <Label>Description</Label>
              <Textarea rows={3} value={form.description} onChange={e => setForm(f => ({ ...f, description: e.target.value }))} placeholder="Enter a short description..." />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>Cancel</Button>
            <Button onClick={handleSave} disabled={saving}>
              {saving ? <><Loader2 size={14} className="mr-2 animate-spin" />Saving...</> : 'Save Category'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete confirm */}
      <Dialog open={!!delTarget} onOpenChange={o => !o && setDelTarget(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Delete category?</DialogTitle>
            <DialogDescription>This will delete the category definition. Proceed?</DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDelTarget(null)}>Cancel</Button>
            <Button variant="destructive" onClick={handleDelete}>Delete</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </AdminLayout>
  );
}
