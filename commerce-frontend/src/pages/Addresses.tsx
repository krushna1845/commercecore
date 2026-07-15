import { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2, Loader2 } from 'lucide-react';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { userApi, UserAddress } from '@/services/userApi';
import { toast } from 'sonner';

function parseStreet(street: string) {
  const parts = (street || '').split('|');
  return {
    name: parts[0] || '',
    line1: parts[1] || '',
    state: parts[2] || '',
    type: parts[3] || 'home',
  };
}

function stringifyStreet(name: string, line1: string, state: string, type: string) {
  return `${name.replace(/\|/g, '')}|${line1.replace(/\|/g, '')}|${state.replace(/\|/g, '')}|${type.replace(/\|/g, '')}`;
}

const emptyForm = { name: '', phone: '', line1: '', city: '', state: '', pincode: '', type: 'home' };

export default function Addresses() {
  const [list, setList] = useState<UserAddress[]>([]);
  const [loading, setLoading] = useState(true);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [editId, setEditId] = useState<number | null>(null);
  const [saving, setSaving] = useState(false);

  const load = () => {
    setLoading(true);
    userApi.addresses()
      .then(res => setList(res))
      .catch(err => toast.error(err.message || 'Failed to load addresses'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const openAdd = () => { setForm(emptyForm); setEditId(null); setDialogOpen(true); };
  const openEdit = (a: UserAddress) => {
    const details = parseStreet(a.street);
    setForm({
      name: details.name,
      phone: a.phone,
      line1: details.line1,
      city: a.city,
      state: details.state,
      pincode: a.zipCode,
      type: details.type,
    });
    setEditId(a.id);
    setDialogOpen(true);
  };

  const handleSave = async () => {
    if (!form.name.trim() || !form.phone.trim() || !form.line1.trim() || !form.city.trim() || !form.pincode.trim()) {
      toast.error('All asterisk (*) fields are required');
      return;
    }

    // Phone format validation matching backend pattern ^[+]?[1-9][0-9]{9,14}$
    const cleanPhone = form.phone.replace(/\s+/g, '');
    const phoneRegex = /^[+]?[1-9][0-9]{9,14}$/;
    if (!phoneRegex.test(cleanPhone)) {
      toast.error('Invalid phone number format. Use 10 digits (e.g. 9876543210).');
      return;
    }

    // ZipCode format validation matching backend pattern ^[0-9]{5,6}$
    const cleanZip = form.pincode.replace(/\s+/g, '');
    const zipRegex = /^[0-9]{5,6}$/;
    if (!zipRegex.test(cleanZip)) {
      toast.error('Invalid ZIP/Pincode. Must be 5 or 6 digits.');
      return;
    }

    setSaving(true);
    const street = stringifyStreet(form.name, form.line1, form.state, form.type);
    const payload = {
      street,
      city: form.city,
      zipCode: cleanZip,
      phone: cleanPhone,
      isDefault: editId ? list.find(x => x.id === editId)?.isDefault || false : list.length === 0,
    };

    try {
      if (editId) {
        await userApi.updateAddress(editId, payload);
        toast.success('Address updated successfully');
      } else {
        await userApi.createAddress(payload);
        toast.success('Address added successfully');
      }
      setDialogOpen(false);
      load();
    } catch (err: any) {
      toast.error(err.message || 'Failed to save address');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await userApi.deleteAddress(id);
      toast.success('Address deleted');
      load();
    } catch (err: any) {
      toast.error(err.message || 'Failed to delete address');
    }
  };

  const handleSetDefault = async (id: number) => {
    try {
      await userApi.setDefaultAddress(id);
      toast.success('Default address updated');
      load();
    } catch (err: any) {
      toast.error(err.message || 'Failed to set default');
    }
  };

  return (
    <DashboardLayout>
      <div className="flex items-end justify-between flex-wrap gap-3">
        <div>
          <h1 className="font-display text-2xl font-semibold">Saved Addresses</h1>
          <p className="text-sm text-muted-foreground mt-1">Add or edit your delivery addresses.</p>
        </div>
        <Button onClick={openAdd}><Plus size={14} className="mr-1" />Add new address</Button>
      </div>

      {loading ? (
        <div className="flex justify-center py-20"><Loader2 className="animate-spin text-primary" size={32} /></div>
      ) : list.length === 0 ? (
        <div className="mt-8 text-center text-sm text-muted-foreground bg-muted/30 rounded-xl p-10 border border-dashed">
          No saved addresses. Click "Add new address" to add one.
        </div>
      ) : (
        <div className="mt-6 grid md:grid-cols-2 gap-4">
          {list.map(a => {
            const details = parseStreet(a.street);
            return (
              <div key={a.id} className="rounded-2xl border bg-card p-5 relative flex flex-col justify-between">
                <div>
                  <div className="flex items-start gap-2">
                    <Badge variant="secondary" className="capitalize">{details.type}</Badge>
                    {a.isDefault ? (
                      <Badge className="bg-success text-success-foreground border-0">Default</Badge>
                    ) : (
                      <button onClick={() => handleSetDefault(a.id)} className="text-[10px] text-muted-foreground hover:text-primary transition underline">
                        Set as default
                      </button>
                    )}
                    <div className="ml-auto flex gap-1">
                      <Button variant="ghost" size="icon" className="h-8 w-8" onClick={() => openEdit(a)}><Pencil size={14} /></Button>
                      <Button variant="ghost" size="icon" className="h-8 w-8 text-destructive" onClick={() => handleDelete(a.id)}><Trash2 size={14} /></Button>
                    </div>
                  </div>
                  <div className="mt-3 font-medium">{details.name}</div>
                  <div className="text-sm text-muted-foreground mt-1">
                    {details.line1}<br />
                    {a.city}{details.state ? `, ${details.state}` : ''} - {a.zipCode}<br />
                    {a.phone}
                  </div>
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
            <DialogTitle>{editId ? 'Edit Address' : 'Add New Address'}</DialogTitle>
            <DialogDescription>Please provide your shipping details below.</DialogDescription>
          </DialogHeader>
          <div className="space-y-3 py-2">
            <div className="space-y-1"><Label>Full Name *</Label>
              <Input value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} placeholder="e.g. Aarav Mehta" /></div>
            <div className="space-y-1"><Label>Phone Number *</Label>
              <Input value={form.phone} onChange={e => setForm(f => ({ ...f, phone: e.target.value }))} placeholder="e.g. 9876543210 (10 digits)" /></div>
            <div className="space-y-1"><Label>Address Line 1 *</Label>
              <Input value={form.line1} onChange={e => setForm(f => ({ ...f, line1: e.target.value }))} placeholder="e.g. Apartment, Suite, Street address" /></div>
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1"><Label>City *</Label>
                <Input value={form.city} onChange={e => setForm(f => ({ ...f, city: e.target.value }))} placeholder="e.g. Mumbai" /></div>
              <div className="space-y-1"><Label>State</Label>
                <Input value={form.state} onChange={e => setForm(f => ({ ...f, state: e.target.value }))} placeholder="e.g. MH" /></div>
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1"><Label>ZIP / Pincode *</Label>
                <Input value={form.pincode} onChange={e => setForm(f => ({ ...f, pincode: e.target.value }))} placeholder="e.g. 400020" /></div>
              <div className="space-y-1"><Label>Address Type</Label>
                <select className="w-full h-10 rounded-md border bg-background px-3 text-sm capitalize"
                  value={form.type} onChange={e => setForm(f => ({ ...f, type: e.target.value }))}>
                  <option value="home">Home</option>
                  <option value="work">Work</option>
                  <option value="other">Other</option>
                </select>
              </div>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>Cancel</Button>
            <Button onClick={handleSave} disabled={saving}>
              {saving ? <><Loader2 size={14} className="mr-2 animate-spin" />Saving...</> : 'Save Address'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </DashboardLayout>
  );
}
