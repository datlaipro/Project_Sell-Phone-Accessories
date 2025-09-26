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
import { MatTooltipModule } from '@angular/material/tooltip';
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

// 👇 Kiểu Page (Spring)
type PageRes<T> = {
  content: T[];
  number: number; // trang hiện tại (0-based)
  size: number; // kích thước trang
  last: boolean; // có phải trang cuối cùng
  totalElements?: number;
  totalPages?: number; // 👈 dùng để tính last chuẩn hơn
};

@Component({
  standalone: true,
  selector: 'product',
  imports: [
    CommonModule,
    NgFor,
    RouterLink,
    MatTooltipModule,
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

  // 👇 Trạng thái phân trang
  private readonly PAGE_SIZE = 15; // BE đã cố định 15/sp
  showCount = signal(this.PAGE_SIZE); // lần đầu hiển thị tối đa 15
  page = 0; // 0-based
  last = false; // đã tới trang cuối chưa
  loadingMore = signal(false); // loading cho nút More results

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

  // 👇 Danh sách đem đi render: nếu <=15 thì trả hết, >15 thì cắt còn 15
  displayed = computed<Product[]>(() => {
    const data = this.items();
    const n = this.showCount();
    return data.slice(0, Math.min(n, data.length));
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
          this.fetch(p.category, p.model); // 👈 lần đầu sẽ reset & nạp trang 0
          return;
        }

        // Reload/đi thẳng: lấy từ URL
        const q = this.qp();
        const category = q.get('category');
        const model = q.get('model');

        if (category && model) {
          this.fetch(category, model); // 👈 lần đầu sẽ reset & nạp trang 0
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

  // ================== FETCH & LOAD MORE ==================

  private categoryCurrent = '';
  private modelCurrent = '';

  private requestPage(
    category: string,
    model: string,
    page: number,
    append: boolean
  ) {
    // dùng loading cho lần đầu, loadingMore cho các lần "More results"
    if (append) this.loadingMore.set(true);
    else this.loading.set(true);
    this.error.set(null);

    const params = new HttpParams()
      .set('category', category)
      .set('model', model)
      .set('page', page.toString()); // 👈 gửi page 0-based, BE cố định size = 15

    this.http
      .get<PageRes<Product>>(`${this.apiBase}/api/products`, { params })
      .pipe(
        map((pg) => {
          

          // --- CHỈ xử lý kiểu Page ---
          if (!pg || !Array.isArray(pg.content)) {
            throw new Error('Response is not a Spring Page');
          }

          const items = pg.content ?? [];
          const number = page;

          // Ưu tiên totalPages nếu có (chính xác tuyệt đối)
          let last: boolean;
          if (typeof pg.totalPages === 'number') {
            last = number + 1 >= pg.totalPages; // kiểm tra có đang ở trang cuối không khi number là chỉ số trang 0-based còn totalPages là tổng số trang.
          } else if (typeof pg.last === 'boolean') {
            last = pg.last;
            const pageSize = pg.size && pg.size > 0 ? pg.size : this.PAGE_SIZE;
            if (items.length < pageSize) last = true; // nếu danh sách sản phẩm < pageSize thì disable nút result
          } else {
            last = items.length < (pg.size ?? this.PAGE_SIZE);
          }

          return { items, last, number };
        }),
        catchError((err) => {
          this.error.set(
            err?.error?.message || err?.message || 'Load sản phẩm thất bại'
          );
          return of({ items: [] as Product[], last: true, number: page });
        }),
        finalize(() => {
          if (append) this.loadingMore.set(false);
          else this.loading.set(false);
        })
      )
      .subscribe(({ items, last, number }) => {
        if (append) {
          // append thêm vào danh sách hiện có
          this.allItems.set([...this.allItems(), ...items]);

          // 👉 cho phép hiển thị thêm đúng số item vừa tải
          this.showCount.set(
            Math.min(this.showCount() + items.length, this.allItems().length)
          );
        } else {
          // lần đầu: reset danh sách
          this.allItems.set(items);

          // 👉 lần đầu chỉ hiển thị tối đa 15 hoặc ít hơn nếu tổng < 15
          this.showCount.set(Math.min(this.PAGE_SIZE, items.length));
        }

        this.last = last;
        this.page = typeof number === 'number' ? number : page;
      });
  }

  private fetch(category: string, model: string) {
    this.loading.set(true);
    this.error.set(null);

    // lưu filter hiện tại để dùng khi bấm More
    this.categoryCurrent = category;
    this.modelCurrent = model;

    // reset trạng thái phân trang
    this.page = 0;
    this.last = false;
    this.allItems.set([]);

    // gọi trang đầu (page = 0)
    this.requestPage(category, model, 0, /* append */ false);

    // luw danh sách sản phẩm lấy được từ api rồi gán vào items
  }

  // 👇 Hàm bấm nút “More results”: lấy trang kế tiếp
  loadMore() {
    if (this.loadingMore() || this.last) return; // đang tải hoặc hết rồi thì thôi
    const nextPage = this.page + 1;
    this.requestPage(
      this.categoryCurrent,
      this.modelCurrent,
      nextPage,
      /* append */ true
    );
  }

  // ================== END FETCH & LOAD MORE ==================

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

  // ========= Wishlist state =========
  private wishKey = 'wishlist_ids';
  wished = new Set<number>();

  ngOnInit() {
    const raw = localStorage.getItem(this.wishKey);
    if (raw) {
      try {
        const arr: number[] = JSON.parse(raw);
        this.wished = new Set(arr);
      } catch {}
    }
  }

  isWished(id: number): boolean {
    return this.wished.has(id);
  }

  toggleWish(id: number): void {
    if (this.wished.has(id)) {
      this.wished.delete(id);
    } else {
      this.wished.add(id);
    }
    localStorage.setItem(this.wishKey, JSON.stringify(Array.from(this.wished)));

    // TODO: nếu có API:
    // (this.isWished(id) ? this.wishlistService.add(id) : this.wishlistService.remove(id))
    //   .subscribe();
  }
}
