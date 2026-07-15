import { ReactNode } from 'react';
import { PackageOpen } from 'lucide-react';
import { Button } from '@/components/ui/button';

export function EmptyState({
  icon, title, description, action,
}: { icon?: ReactNode; title: string; description?: string; action?: { label: string; onClick: () => void } }) {
  return (
    <div className="flex flex-col items-center justify-center text-center py-20 px-4">
      <div className="grid h-20 w-20 place-items-center rounded-full bg-accent text-accent-foreground mb-5">
        {icon ?? <PackageOpen size={32} />}
      </div>
      <h3 className="text-lg font-semibold">{title}</h3>
      {description && <p className="mt-1 max-w-md text-sm text-muted-foreground">{description}</p>}
      {action && <Button onClick={action.onClick} className="mt-6">{action.label}</Button>}
    </div>
  );
}
