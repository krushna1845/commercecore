import { motion } from 'framer-motion';
import { FiMessageSquare } from 'react-icons/fi';

interface TestimonialCardProps {
  name: string;
  role: string;
  avatar: string;
  text: string;
}

export function TestimonialCard({ name, role, avatar, text }: TestimonialCardProps) {
  return (
    <motion.article
      initial={{ opacity: 0, y: 24 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, amount: 0.3 }}
      transition={{ duration: 0.45, ease: 'easeOut' }}
      className="group rounded-[2rem] border border-white/10 bg-white/10 p-6 shadow-[0_26px_60px_-40px_rgba(15,23,42,0.25)] backdrop-blur-xl"
    >
      <div className="flex items-start gap-4">
        <img src={avatar} alt={name} loading="lazy" className="h-14 w-14 rounded-2xl object-cover ring-1 ring-white/20" />
        <div>
          <div className="flex items-center gap-2 text-sm font-semibold text-foreground">
            {name}
          </div>
          <p className="text-xs text-muted-foreground">{role}</p>
        </div>
      </div>
      <p className="mt-5 text-sm leading-7 text-muted-foreground">
        <FiMessageSquare className="inline h-5 w-5 text-primary" />
        {text}
      </p>
    </motion.article>
  );
}
