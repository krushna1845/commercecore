import { Link } from 'react-router-dom';
import { AuthLayout } from '@/layouts/AuthLayout';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

export default function ForgotPassword() {
  return (
    <AuthLayout
      title="Forgot password?"
      subtitle="No worries — we'll send a reset link to your inbox."
      footer={<><Link to="/login" className="text-primary font-medium hover:underline">← Back to sign in</Link></>}
    >
      <form className="space-y-4" onSubmit={(e) => e.preventDefault()}>
        <div className="space-y-1.5"><Label>Email</Label><Input type="email" placeholder="you@example.com" required /></div>
        <Button size="lg" className="w-full">Send reset link</Button>
      </form>
    </AuthLayout>
  );
}
