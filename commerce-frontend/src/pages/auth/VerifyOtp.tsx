import { useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthLayout } from '@/layouts/AuthLayout';
import { Button } from '@/components/ui/button';
import { toast } from 'sonner';

export default function VerifyOtp() {
  const [otp, setOtp] = useState(['', '', '', '', '', '']);
  const refs = useRef<(HTMLInputElement | null)[]>([]);
  const navigate = useNavigate();

  const onChange = (i: number, v: string) => {
    if (!/^\d?$/.test(v)) return;
    const next = [...otp]; next[i] = v; setOtp(next);
    if (v && i < 5) refs.current[i + 1]?.focus();
  };

  return (
    <AuthLayout
      title="Verify your email"
      subtitle="We sent a 6-digit code to your email. Enter it below."
      footer={<><Link to="/login" className="text-primary font-medium hover:underline">← Back to sign in</Link></>}
    >
      <form className="space-y-5" onSubmit={(e) => { e.preventDefault(); toast.success('Verified!'); navigate('/'); }}>
        <div className="flex gap-2 justify-center">
          {otp.map((d, i) => (
            <input
              key={i}
              ref={(el) => (refs.current[i] = el)}
              value={d}
              onChange={(e) => onChange(i, e.target.value)}
              maxLength={1}
              inputMode="numeric"
              className="h-14 w-12 text-center text-lg font-semibold rounded-xl border bg-background focus:border-primary focus:outline-none focus:ring-4 focus:ring-primary/15"
            />
          ))}
        </div>
        <Button size="lg" className="w-full">Verify</Button>
        <p className="text-xs text-center text-muted-foreground">Didn't get a code? <button type="button" className="text-primary hover:underline">Resend</button></p>
      </form>
    </AuthLayout>
  );
}
