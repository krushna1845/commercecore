import { Link } from 'react-router-dom';
import { Home, Search } from 'lucide-react';
import { Button } from '@/components/ui/button';

const NotFound = () => (
  <div className="min-h-screen flex flex-col items-center justify-center p-6 bg-gradient-subtle text-center">
    <div className="font-display text-[120px] sm:text-[180px] leading-none font-bold bg-gradient-hero bg-clip-text text-transparent">404</div>
    <h1 className="font-display text-2xl font-semibold mt-2">Page not found</h1>
    <p className="text-muted-foreground mt-2 max-w-sm">The page you're looking for doesn't exist or has been moved.</p>
    <div className="mt-6 flex gap-3">
      <Link to="/"><Button><Home size={16} className="mr-1.5" />Go home</Button></Link>
      <Link to="/products"><Button variant="outline"><Search size={16} className="mr-1.5" />Browse shop</Button></Link>
    </div>
  </div>
);

export default NotFound;
