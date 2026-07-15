import { DashboardLayout } from '@/layouts/DashboardLayout';
import { Switch } from '@/components/ui/switch';
import { Label } from '@/components/ui/label';
import { Button } from '@/components/ui/button';

export default function Settings() {
  return (
    <DashboardLayout>
      <h1 className="font-display text-2xl font-semibold">Settings</h1>
      <p className="text-sm text-muted-foreground mt-1">Manage your preferences.</p>

      <div className="mt-6 space-y-4 max-w-2xl">
        {[
          ['Email notifications', 'Receive updates about your orders and offers.'],
          ['SMS alerts', 'Get text alerts for shipping status.'],
          ['Push notifications', 'Browser push notifications for deals.'],
          ['Personalized recommendations', 'Show items based on your activity.'],
        ].map(([title, desc]) => (
          <div key={title} className="rounded-2xl border bg-card p-5 flex items-center justify-between gap-4">
            <div>
              <Label className="font-medium">{title}</Label>
              <p className="text-sm text-muted-foreground mt-0.5">{desc}</p>
            </div>
            <Switch defaultChecked />
          </div>
        ))}

        <div className="rounded-2xl border border-destructive/30 bg-destructive/5 p-5">
          <h3 className="font-semibold text-destructive">Danger zone</h3>
          <p className="text-sm text-muted-foreground mt-1">Permanently delete your account and all data.</p>
          <Button variant="destructive" className="mt-3" size="sm">Delete account</Button>
        </div>
      </div>
    </DashboardLayout>
  );
}
