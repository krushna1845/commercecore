import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Category } from '@/types';

const MotionLink = motion(Link);

export function CategoryCard({ category }: { category: Category }) {
  return (
    <MotionLink
      to={`/products?category=${encodeURIComponent(category.name)}`}
      whileHover={{ y: -4 }}
      className="group relative overflow-hidden rounded-3xl border border-white/10 bg-white/10 p-5 shadow-[0_24px_60px_-40px_rgba(15,23,42,0.4)] backdrop-blur-xl transition-all duration-300 hover:border-white/20"
    >
      <div className="inline-flex h-16 w-16 items-center justify-center rounded-3xl bg-primary/10 text-2xl shadow-lg shadow-primary/10">
        <span>{category.icon}</span>
      </div>
      <div className="mt-6 space-y-2">
        <h3 className="text-lg font-semibold">{category.name}</h3>
        <p className="text-sm text-muted-foreground">{category.count.toLocaleString()} items curated for you</p>
      </div>
      <div className="pointer-events-none absolute inset-x-0 bottom-0 h-1 bg-gradient-to-r from-primary via-transparent to-transparent opacity-60" />
    </MotionLink>
  );
}
