import { MainLayout } from '@/layouts/MainLayout';
import { ProductComparison } from '@/components/ProductComparison';

export default function ComparePage() {
  return (
    <MainLayout>
      <div className="container-x py-6">
        <ProductComparison />
      </div>
    </MainLayout>
  );
}
