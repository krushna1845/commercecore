import { AdminLayout } from '@/layouts/AdminLayout';

const lines = [30, 45, 38, 60, 55, 70, 65, 80, 78, 95, 88, 105];
const cats = [
  { name: 'Electronics', val: 42, color: 'bg-primary' },
  { name: 'Fashion', val: 28, color: 'bg-discount' },
  { name: 'Home & Living', val: 16, color: 'bg-warning' },
  { name: 'Beauty', val: 9, color: 'bg-success' },
  { name: 'Other', val: 5, color: 'bg-muted-foreground' },
];

export default function AdminAnalytics() {
  return (
    <AdminLayout title="Analytics">
      <div className="grid lg:grid-cols-3 gap-4">
        <div className="lg:col-span-2 rounded-2xl border bg-card p-6">
          <h3 className="font-semibold mb-1">Revenue trend</h3>
          <p className="text-xs text-muted-foreground mb-5">Last 12 months</p>
          <svg viewBox="0 0 600 200" className="w-full h-48">
            <defs>
              <linearGradient id="g" x1="0" x2="0" y1="0" y2="1">
                <stop offset="0%" stopColor="hsl(var(--primary))" stopOpacity="0.4" />
                <stop offset="100%" stopColor="hsl(var(--primary))" stopOpacity="0" />
              </linearGradient>
            </defs>
            <polyline fill="none" stroke="hsl(var(--primary))" strokeWidth="2.5"
              points={lines.map((v, i) => `${(i / 11) * 580 + 10},${200 - v * 1.6}`).join(' ')} />
            <polygon fill="url(#g)"
              points={`10,200 ${lines.map((v, i) => `${(i / 11) * 580 + 10},${200 - v * 1.6}`).join(' ')} 590,200`} />
          </svg>
        </div>
        <div className="rounded-2xl border bg-card p-6">
          <h3 className="font-semibold mb-5">Sales by category</h3>
          <div className="space-y-4">
            {cats.map(c => (
              <div key={c.name}>
                <div className="flex justify-between text-sm mb-1.5"><span>{c.name}</span><span className="text-muted-foreground">{c.val}%</span></div>
                <div className="h-2 rounded-full bg-muted overflow-hidden">
                  <div className={`h-full ${c.color}`} style={{ width: `${c.val}%` }} />
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </AdminLayout>
  );
}
