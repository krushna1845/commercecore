import { MainLayout } from '@/layouts/MainLayout';
import { AIShoppingAssistant } from '@/components/AIShoppingAssistant';

export default function Assistant() {
  return (
    <MainLayout>
      <div className="container-x py-10">
        <AIShoppingAssistant />
      </div>
    </MainLayout>
  );
}
