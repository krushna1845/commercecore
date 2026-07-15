import { Star } from 'lucide-react';
import { cn } from '@/lib/utils';

export function Rating({ value, size = 14, showValue = false, className }: { value: number; size?: number; showValue?: boolean; className?: string }) {
  return (
    <span className={cn('inline-flex items-center gap-1', className)}>
      {[1, 2, 3, 4, 5].map(i => (
        <Star
          key={i}
          size={size}
          className={i <= Math.round(value) ? 'fill-rating text-rating' : 'text-muted-foreground/30'}
        />
      ))}
      {showValue && <span className="ml-1 text-xs font-medium text-muted-foreground">{value.toFixed(1)}</span>}
    </span>
  );
}
