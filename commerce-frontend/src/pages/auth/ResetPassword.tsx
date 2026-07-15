import { Link, useNavigate } from 'react-router-dom';
import { AuthLayout } from '@/layouts/AuthLayout';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { toast } from 'sonner';

export default function ResetPassword() {
  const navigate = useNavigate();
  return (
    <AuthLayout title="Set a new password" subtitle="Make it strong — at least 8 characters."
      footer={<><Link to="/login" className="text-primary font-medium hover:underline">← Back to sign in</Link></>}>
      <form className="space-y-4" onSubmit={(e) => { e.preventDefault(); toast.success('Password updated'); navigate('/login'); }}>
        <div className="space-y-1.5"><Label>New password</Label><Input type="password" required /></div>
        <div className="space-y-1.5"><Label>Confirm password</Label><Input type="password" required /></div>
        <Button size="lg" className="w-full">Update password</Button>
      </form>
    </AuthLayout>
  );
}
