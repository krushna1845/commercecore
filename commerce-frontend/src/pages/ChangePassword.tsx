import { DashboardLayout } from '@/layouts/DashboardLayout';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

export default function ChangePassword() {
  return (
    <DashboardLayout>
      <h1 className="font-display text-2xl font-semibold">Change Password</h1>
      <p className="text-sm text-muted-foreground mt-1">Use a strong password to keep your account secure.</p>
      <form className="mt-6 max-w-md space-y-4 rounded-2xl border bg-card p-6" onSubmit={(e) => e.preventDefault()}>
        <div className="space-y-1.5"><Label>Current password</Label><Input type="password" required /></div>
        <div className="space-y-1.5"><Label>New password</Label><Input type="password" required /></div>
        <div className="space-y-1.5"><Label>Confirm new password</Label><Input type="password" required /></div>
        <Button className="w-full">Update password</Button>
      </form>
    </DashboardLayout>
  );
}
