import { useEffect, useState } from 'react';
import { AdminLayout } from '@/layouts/AdminLayout';
import { Badge } from '@/components/ui/badge';
import { adminApi, AdminUser } from '@/services/adminApi';
import { Loader2 } from 'lucide-react';
import { toast } from 'sonner';

export default function AdminUsers() {
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminApi.users()
      .then(res => setUsers(res))
      .catch(err => toast.error(err.message || 'Failed to load users'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <AdminLayout title="Users">
      <div className="rounded-2xl border bg-card overflow-hidden">
        {loading ? (
          <div className="flex items-center justify-center py-20">
            <Loader2 className="animate-spin text-primary" size={32} />
          </div>
        ) : users.length === 0 ? (
          <div className="text-center py-12 text-muted-foreground text-sm">
            No users found.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-muted/40 text-xs uppercase tracking-wider text-muted-foreground">
                <tr>
                  <th className="text-left p-3 px-5">User ID</th>
                  <th className="text-left p-3">Username</th>
                  <th className="text-left p-3">Role</th>
                </tr>
              </thead>
              <tbody className="divide-y">
                {users.map(u => (
                  <tr key={u.id} className="hover:bg-muted/30">
                    <td className="p-3 px-5 font-mono text-xs text-muted-foreground">#{u.id}</td>
                    <td className="p-3">
                      <div className="flex items-center gap-3">
                        <div className="grid h-9 w-9 place-items-center rounded-full bg-accent text-accent-foreground font-medium uppercase">
                          {u.username[0]}
                        </div>
                        <div className="font-medium capitalize">{u.username}</div>
                      </div>
                    </td>
                    <td className="p-3">
                      <Badge 
                        className={u.role === 'ROLE_ADMIN' ? 'bg-primary text-primary-foreground border-0' : 'bg-secondary text-secondary-foreground border-0'}
                        variant={u.role === 'ROLE_ADMIN' ? 'default' : 'secondary'}
                      >
                        {u.role === 'ROLE_ADMIN' ? 'Admin' : 'User'}
                      </Badge>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </AdminLayout>
  );
}
