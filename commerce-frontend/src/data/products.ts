import { Product, Review } from '@/types';

const img = (seed: string, w = 800) =>
  `https://images.unsplash.com/photo-${seed}?w=${w}&q=80&auto=format&fit=crop`;

export const products: Product[] = [
  {
    id: 'p1', name: 'Wireless Noise-Cancelling Headphones', brand: 'SonicPro', category: 'electronics',
    price: 8999, originalPrice: 14999, rating: 4.6, reviewCount: 2143,
    image: img('1583394838336-acd977736f90'), stock: 24, badge: 'best-seller',
    description: 'Studio-grade ANC headphones with 40h battery, hi-res audio, and plush memory foam cushions for all-day comfort.',
    specs: { 'Battery': '40 hours', 'Driver': '40mm dynamic', 'Bluetooth': '5.3', 'Weight': '254g', 'Warranty': '1 year' },
    tags: ['headphones', 'audio', 'wireless'],
  },
  {
    id: 'p2', name: 'Smart Fitness Watch Series 7', brand: 'PulseFit', category: 'electronics',
    price: 12499, originalPrice: 19999, rating: 4.4, reviewCount: 980,
    image: img('1523275335684-37898b6baf30'), stock: 12, badge: 'trending',
    description: 'Track 100+ workouts, ECG, SpO2, sleep — with always-on AMOLED and 14-day battery life.',
    specs: { 'Display': '1.78" AMOLED', 'Battery': '14 days', 'GPS': 'Built-in', 'Water-resistance': '5ATM' },
  },
  {
    id: 'p3', name: 'Minimalist Cotton Tee — Oversized', brand: 'Northvale', category: 'fashion',
    price: 799, originalPrice: 1499, rating: 4.2, reviewCount: 412,
    image: img('1521572163474-6864f9cf17ab'), stock: 80, badge: 'deal',
    description: 'Premium 240 GSM combed cotton, garment-dyed for that lived-in feel. Drop-shoulder cut.',
  },
  {
    id: 'p4', name: 'Aroma Pour-Over Coffee Maker', brand: 'Brewlab', category: 'home',
    price: 2199, originalPrice: 2999, rating: 4.7, reviewCount: 318,
    image: img('1495474472287-4d71bcdd2085'), stock: 40,
    description: 'Borosilicate glass carafe with stainless steel filter — barista-grade pour over at home.',
  },
  {
    id: 'p5', name: 'Ergonomic Mesh Office Chair', brand: 'WorkWell', category: 'home',
    price: 11999, originalPrice: 17999, rating: 4.5, reviewCount: 712,
    image: img('1592078615290-033ee584e267'), stock: 8, badge: 'best-seller',
    description: 'Adjustable lumbar, 4D armrests, breathable mesh and a tilt-lock recline up to 135°.',
  },
  {
    id: 'p6', name: 'Hydra Glow Vitamin C Serum', brand: 'Lumière', category: 'beauty',
    price: 1199, originalPrice: 1799, rating: 4.3, reviewCount: 1521,
    image: img('1556228720-195a672e8a03'), stock: 60, badge: 'new',
    description: '15% L-ascorbic acid + ferulic acid for a brighter, even complexion in 4 weeks.',
  },
  {
    id: 'p7', name: 'Trail Runner Pro Sneakers', brand: 'Striderix', category: 'sports',
    price: 4499, originalPrice: 6999, rating: 4.5, reviewCount: 612,
    image: img('1542291026-7eec264c27ff'), stock: 22, badge: 'trending',
    description: 'Carbon plate midsole, sticky rubber outsole — built for ultras and city sprints alike.',
  },
  {
    id: 'p8', name: 'The Atlas of Modern Design', brand: 'Penfold', category: 'books',
    price: 1299, originalPrice: 1599, rating: 4.8, reviewCount: 233,
    image: img('1544947950-fa07a98d237f'), stock: 15,
    description: 'A coffee-table tour through 100 years of design icons across product, fashion, and architecture.',
  },
  {
    id: 'p9', name: '4K Mini Action Camera', brand: 'Cinemax', category: 'electronics',
    price: 6499, originalPrice: 9999, rating: 4.3, reviewCount: 540,
    image: img('1502920917128-1aa500764cbd'), stock: 30,
    description: 'Pocket-sized, waterproof to 30m, 4K60 with 6-axis stabilization.',
  },
  {
    id: 'p10', name: 'Linen Blend Wide-Leg Trousers', brand: 'Northvale', category: 'fashion',
    price: 1899, originalPrice: 2999, rating: 4.4, reviewCount: 302,
    image: img('1594633312681-425c7b97ccd1'), stock: 45,
    description: 'Breathable linen blend with elasticated back and hidden side pockets.',
  },
  {
    id: 'p11', name: 'Modern Ceramic Planter Set', brand: 'Brewlab', category: 'home',
    price: 1499, originalPrice: 1999, rating: 4.6, reviewCount: 188,
    image: img('1485955900006-10f4d324d411'), stock: 28, badge: 'deal',
    description: 'Set of 3 hand-glazed ceramic pots with bamboo trays — drainage holes included.',
  },
  {
    id: 'p12', name: 'Wireless Mechanical Keyboard', brand: 'KeyForge', category: 'electronics',
    price: 5999, originalPrice: 8499, rating: 4.7, reviewCount: 902,
    image: img('1541140532154-b024d705b90a'), stock: 18, badge: 'best-seller',
    description: 'Hot-swappable, gasket-mounted, low-profile switches. RGB and triple-mode connectivity.',
  },
];

export const reviews: Review[] = [
  { id: 'r1', user: 'Aarav Mehta', rating: 5, title: 'Brilliant build quality', comment: 'Sounds incredible and the noise cancellation is class-leading.', date: '2025-04-12', verified: true },
  { id: 'r2', user: 'Sneha Iyer', rating: 4, title: 'Great value', comment: 'Comfortable for long sessions. App could be better.', date: '2025-03-28', verified: true },
  { id: 'r3', user: 'Rahul Khanna', rating: 5, title: 'Worth every rupee', comment: 'Exactly as described. Quick delivery too.', date: '2025-02-19' },
];

export const testimonials = [
  { name: 'Priya Sharma', role: 'Verified Buyer', avatar: img('1494790108377-be9c29b29330', 200), text: 'The product quality is unmatched. Delivery was lightning fast and packaging was premium.' },
  { name: 'Karan Patel', role: 'Verified Buyer', avatar: img('1500648767791-00dcc994a43e', 200), text: 'I have been a customer for 2 years now. The curation is fantastic — never a bad purchase.' },
  { name: 'Ananya Roy', role: 'Verified Buyer', avatar: img('1438761681033-6461ffad8d80', 200), text: 'Customer support actually solved my problem in minutes. That is rare these days.' },
];
