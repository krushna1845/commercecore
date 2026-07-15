import { ReactNode } from 'react';
import { Link } from 'react-router-dom';

export function AuthLayout({ title, subtitle, children, footer }: { title: string; subtitle?: string; children: ReactNode; footer?: ReactNode }) {
  return (
    <div className="min-h-screen grid lg:grid-cols-2 bg-background">
      <div className="hidden lg:flex relative bg-gradient-hero overflow-hidden p-12 flex-col justify-between text-primary-foreground">
        <Link to="/" className="flex items-center gap-2">
          <div className="grid h-10 w-10 place-items-center rounded-xl bg-white/20 backdrop-blur font-display font-bold">C</div>
          <span className="font-display text-xl font-semibold">commercecore</span>
        </Link>
        <div className="relative z-10 max-w-md">
          <h2 className="font-display text-4xl font-semibold leading-tight">A modern marketplace, built for what's next.</h2>
          <p className="mt-4 text-primary-foreground/80">Join 1.2M+ shoppers discovering trending products, lightning-fast delivery, and obsessively-curated brands.</p>
        </div>
        <div className="text-xs text-primary-foreground/60">© {new Date().getFullYear()} commercecore</div>
        <div className="absolute -bottom-32 -right-32 h-96 w-96 rounded-full bg-white/10 blur-3xl" />
        <div className="absolute -top-20 -left-10 h-64 w-64 rounded-full bg-white/10 blur-2xl" />
      </div>

      <div className="flex items-center justify-center p-6 sm:p-12">
        <div className="w-full max-w-md">
          <Link to="/" className="lg:hidden mb-8 inline-flex items-center gap-2">
            <div className="grid h-9 w-9 place-items-center rounded-xl bg-gradient-hero text-primary-foreground font-display font-bold">C</div>
            <span className="font-display text-lg font-semibold">commercecore</span>
          </Link>
          <h1 className="font-display text-2xl sm:text-3xl font-semibold">{title}</h1>
          {subtitle && <p className="mt-2 text-sm text-muted-foreground">{subtitle}</p>}
          <div className="mt-8">{children}</div>
          {footer && <div className="mt-6 text-sm text-center text-muted-foreground">{footer}</div>}
        </div>
      </div>
    </div>
  );
}
