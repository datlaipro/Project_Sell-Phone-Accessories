import { filter } from 'rxjs/operators';
import {
  Component,
  ViewEncapsulation,
  HostListener,
  effect,
  signal,
  computed,
} from '@angular/core';
import { map, catchError, finalize, of } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { CommonModule, NgFor } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { inject } from '@angular/core';
import { ProductList } from '../../../service/ProductList.service';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatExpansionModule } from '@angular/material/expansion';
import { HttpClient, HttpParams } from '@angular/common/http';

import { environment } from '../../../environments/environment.development';

type Product = {
  id: number;
  name: string;
  description: string | null;
  price: number;
  rate: number; // 👈 thêm
  color: string | null;
  quantity: number;
  brand: string | null;
  active: boolean;
  discount: number;
  subSlug: string | null;
  categoryName: string;
  categorySlug: string;
  deviceModelNames: string[];
  deviceModelSlugs: string[];
  coverImageId: number | null;
  coverImageUrl: string | null; // 👈 thêm
  createdAt: string; // BE trả ISO string -> để string cho đơn giản
  updatedAt: string;
};
@Component({
  standalone: true,
  selector: 'product',
  imports: [
    CommonModule,
    NgFor,
    RouterLink,
    MatButtonModule,
    MatMenuModule,
    MatIconModule,
    MatSidenavModule,
    MatListModule,
    MatExpansionModule,
  ],
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.css'],
})
export class Products {
  isFilterOpen = false;
  bus = inject(ProductList); // state báo trạng thái đăng nhập ra ngoài
  apiBase = environment.apiUrl;
  http = inject(HttpClient);
  loading = signal(false);
  error = signal<string | null>(null);
  allItems = signal<Product[]>([]); // dữ liệu gốc
  selectedColor = signal<string | null>(null); // trạng thái lọc màu sắc
  route = inject(ActivatedRoute); //
  qp = toSignal(this.route.queryParamMap, { initialValue: new Map() as any });
  filtersOpen = false;
  selectedPriceRange = signal<{ min: number | null; max: number | null }>({
    // trạng thái lọc giá
    min: null,
    max: null,
  });
  sortKey = signal<'best' | 'price-asc' | 'price-desc' | 'rating' | 'newest'>(
    'best'
  );

  onSortChange(v: string) {
    this.sortKey.set(v as any);
  }

  items = computed<Product[]>(() => {
    //computed là derived signal trong Angular: một state chỉ-đọc được tính ra từ các signal khác và tự cập nhật khi các signal phụ thuộc đổi.
    // danh sách hiển thị
    const list = this.allItems();
    const c = this.selectedColor();
    const { min, max } = this.selectedPriceRange();

    // 1) Lọc theo màu (nếu có)
    let out = c
      ? list.filter((p) => (p.color ?? '').toLowerCase() === c.toLowerCase())
      : list;

    // 2) Lọc theo giá (nếu có)
    const lo = min ?? -Infinity; // nếu min=null -> không cắt đáy
    const hi = max ?? Infinity; // nếu max=null -> không cắt trần

    out = out.filter((p) => p.price >= lo && p.price <= hi);
    // sắp xếp theo sortKey
    const key = this.sortKey();
    const arr = [...out]; // tránh mutate
    switch (key) {
      case 'price-asc':
        arr.sort((a, b) => a.price - b.price);
        break;
      case 'price-desc':
        arr.sort((a, b) => b.price - a.price);
        break;
      case 'rating':
        arr.sort((a, b) => b.rate - a.rate);
        break;
      case 'newest':
        arr.sort((a, b) => Date.parse(b.createdAt) - Date.parse(a.createdAt));
        break;
      case 'best':
      default:
        // để nguyên hoặc bạn tự định nghĩa
        break;
    }
    return arr;
  });

  // Đóng panel khi phóng to lên desktop
  @HostListener('window:resize')
  onResize() {
    if (window.innerWidth > 900 && this.filtersOpen) this.filtersOpen = false;
  }

  // Đóng bằng phím ESC
  @HostListener('document:keydown.escape')
  onEsc() {
    this.filtersOpen = false;
  }

  constructor(private router: Router) {
    effect(
      () => {
        // Ưu tiên bus nếu có (đi từ trang trước)
        const p = this.bus.payload();
        if (p?.category && p?.model) {
          this.fetch(p.category, p.model);
          return;
        }

        // Reload/đi thẳng: lấy từ URL
        const q = this.qp();
        const category = q.get('category');
        const model = q.get('model');

        if (category && model) {
          this.fetch(category, model);
          return;
        }

        // (tuỳ chọn) Fallback từ localStorage
        const cached = localStorage.getItem('productFilters');
        if (cached) {
          const { category: c, model: m } = JSON.parse(cached);
          if (c && m) this.fetch(c, m);
        }
      },
      { allowSignalWrites: true }
    );
  }

  // Không cần constructor nữa

  // Khi nhận được payload mới, nhớ lưu để reload lần sau vẫn có
  saveFilters(category: string, model: string) {
    this.bus.payload.set({ category, model }); // lưu vào bus để lần sau khỏi phải đọc URL
    localStorage.setItem('productFilters', JSON.stringify({ category, model }));
  }

  // (tuỳ chọn) Nếu model bạn cần dạng slug (iphone-16-pro-max), dùng hàm này
  private slugify(s: string) {
    return s
      .toLowerCase()
      .normalize('NFD')
      .replace(/\p{Diacritic}/gu, '') // bỏ dấu tiếng Việt
      .replace(/[^a-z0-9\s-]/g, '')
      .trim()
      .replace(/\s+/g, '-')
      .replace(/-+/g, '-');
  }

  private fetch(category: string, model: string) {
    this.loading.set(true);
    this.error.set(null);

    const params = new HttpParams()
      .set('category', category)
      .set('model', model);

    this.http
      .get<any>(`${this.apiBase}/api/products`, { params })
      .pipe(
        map((res: any) => {
          if (Array.isArray(res)) return res; // BE trả thẳng mảng
          if (Array.isArray(res?.content)) return res.content; // Spring Page
          if (Array.isArray(res?.data)) return res.data; // wrapper data
          if (Array.isArray(res?.items)) return res.items; // wrapper items
          // nếu lỡ trả về object key->product:
          if (res && typeof res === 'object') return Object.values(res);
          return [];
        }),
        catchError((err) => {
          this.error.set(err?.error?.message || 'Load sản phẩm thất bại');
          return of([] as Product[]);
        }),
        finalize(() => this.loading.set(false))
      )
      .subscribe((list: Product[]) => this.allItems.set(list));
    // luw danh sách sản phẩm lấy được từ api rồi gán vào items
  }

  goToProductDetail(productId: number) {
    // thực hiện điều hướng

    // Điều hướng đến productDetail, ví dụ truyền tham số sản phẩm
    this.router.navigate(['/product/productDetail', productId]);
  }
  filterColor(color: string | null) {
    this.selectedColor.set(color); // null = bỏ lọc
  }
  filterPrice(range: { min: number | null; max: number | null }) {
    this.selectedPriceRange.set({ min: range.min, max: range.max });
  }
}
