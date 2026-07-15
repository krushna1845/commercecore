import { Link, useLocation } from 'react-router-dom';
import { ChevronRight } from 'lucide-react';
import { Fragment } from 'react';

export function Breadcrumbs({ items }: { items?: { label: string; to?: string }[] }) {
  const loc = useLocation();
  const auto = loc.pathname.split('/').filter(Boolean).map((seg, i, arr) => ({
    label: seg.replace(/-/g, ' '),
    to: '/' + arr.slice(0, i + 1).join('/'),
  }));
  const list = items ?? [{ label: 'Home', to: '/' }, ...auto];
  return (
    <nav className="flex items-center gap-1.5 text-sm text-muted-foreground capitalize">
      {list.map((it, i) => (
        <Fragment key={i}>
          {i > 0 && <ChevronRight size={14} />}
          {it.to && i < list.length - 1
            ? <Link to={it.to} className="hover:text-foreground">{it.label}</Link>
            : <span className="text-foreground font-medium">{it.label}</span>}
        </Fragment>
      ))}
    </nav>
  );
}
