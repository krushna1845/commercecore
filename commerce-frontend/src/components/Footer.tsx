import { Link } from 'react-router-dom';
import { Facebook, Instagram, Mail, MapPin, Phone, Twitter, Youtube } from 'lucide-react';

const cols = [
  { title: 'Shop', links: [['New Arrivals', '/products'], ['Best Sellers', '/products?sort=popular'], ['Deals', '/products?badge=deal'], ['Brands', '/products']] },
  { title: 'Company', links: [['About Us', '#'], ['Careers', '#'], ['Press', '#'], ['Blog', '#']] },
  { title: 'Help', links: [['Customer Service', '#'], ['Track Order', '/orders'], ['Returns', '#'], ['FAQs', '#']] },
];

export function Footer() {
  return (
    <footer className="mt-20 border-t bg-surface">
      <div className="container-x py-14">
        <div className="grid gap-10 md:grid-cols-2 lg:grid-cols-5">
          <div className="lg:col-span-2">
            <Link to="/" className="flex items-center gap-2">
              <div className="grid h-9 w-9 place-items-center rounded-xl bg-gradient-hero text-primary-foreground font-display font-bold">C</div>
              <span className="font-display text-lg font-semibold">commercecore</span>
            </Link>
            <p className="mt-3 text-sm text-muted-foreground max-w-sm">
              A modern marketplace for the things you love — curated, fast, and obsessively merchandised.
            </p>
            <div className="mt-4 space-y-2 text-sm text-muted-foreground">
              <div className="flex items-center gap-2"><MapPin size={14} /> Bandra Kurla Complex, Mumbai</div>
              <div className="flex items-center gap-2"><Phone size={14} /> +91 22 4000 0000</div>
              <div className="flex items-center gap-2"><Mail size={14} /> hello@commercecore.shop</div>
            </div>
          </div>
          {cols.map(c => (
            <div key={c.title}>
              <h4 className="font-semibold text-sm mb-4">{c.title}</h4>
              <ul className="space-y-2.5">
                {c.links.map(([l, h]) => (
                  <li key={l}><Link to={h as string} className="text-sm text-muted-foreground hover:text-foreground">{l}</Link></li>
                ))}
              </ul>
            </div>
          ))}
        </div>

        <div className="mt-12 pt-6 border-t flex flex-col md:flex-row gap-4 items-center justify-between text-xs text-muted-foreground">
          <span>© {new Date().getFullYear()} commercecore. All rights reserved.</span>
          <div className="flex items-center gap-3">
            {[Twitter, Instagram, Facebook, Youtube].map((Icon, i) => (
              <a key={i} href="#" className="grid h-8 w-8 place-items-center rounded-full border hover:bg-accent"><Icon size={14} /></a>
            ))}
          </div>
        </div>
      </div>
    </footer>
  );
}
