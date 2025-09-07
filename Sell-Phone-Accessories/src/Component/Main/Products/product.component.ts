import {
  Component,
  ViewEncapsulation,
  HostListener,
  effect,
  signal,
} from '@angular/core';
import { CommonModule, NgFor } from '@angular/common';
import { RouterLink } from '@angular/router';
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
  items = signal<Product[] | null>(null);
  products = [
    {
      id: 1,
      name: 'iPad Air 11-inch (M3) and iPad Air 11-inch (M2) CaseDefender Series',
      price: '79.99$',
      rate: '4.2',
      img: '',
      Thickness: '3',
      DropRating: '3',
    },
    {
      id: 1,
      name: 'iPad Air 11-inch (M3) and iPad Air 11-inch (M2) CaseDefender Series',
      price: '79.99$',
      rate: '4.2',
      img: '',
      Thickness: '3',
      DropRating: '3',
    },
    {
      id: 1,
      name: 'iPad Air 11-inch (M3) and iPad Air 11-inch (M2) CaseDefender Series',
      price: '79.99$',
      rate: '4.2',
      img: 'https://www.otterbox.com/dw/image/v2/BGMS_PRD/on/demandware.static/-/Sites-masterCatalog/default/dwc2cd3cbe/productimages/dis/cases-screen-protection/defender-pro-ipha23/defender-pro-ipha23-black-2.png?sw=400&sh=400',
      Thickness: '3',
      DropRating: '3',
    },
    {
      id: 1,
      name: 'iPad Air 11-inch (M3) and iPad Air 11-inch (M2) CaseDefender Series',
      price: '79.99$',
      rate: '4.2',
      img: '',
      Thickness: '3',
      DropRating: '3',
    },
    {
      id: 1,
      name: 'iPad Air 11-inch (M3) and iPad Air 11-inch (M2) CaseDefender Series',
      price: '79.99$',
      rate: '4.2',
      img: '',
      Thickness: '3',
      DropRating: '3',
    },
    {
      id: 1,
      name: 'iPad Air 11-inch (M3) and iPad Air 11-inch (M2) CaseDefender Series',
      price: '79.99$',
      rate: '4.2',
      img: '',
      Thickness: '3',
      DropRating: '3',
    },
    {
      id: 1,
      name: 'iPad Air 11-inch (M3) and iPad Air 11-inch (M2) CaseDefender Series',
      price: '79.99$',
      rate: '4.2',
      img: '',
      Thickness: '3',
      DropRating: '3',
    },
    {
      id: 1,
      name: 'iPad Air 11-inch (M3) and iPad Air 11-inch (M2) CaseDefender Series',
      price: '79.99$',
      rate: '4.2',
      img: '',
      Thickness: '3',
      DropRating: '3',
    },
  ];

  filtersOpen = false;

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

  constructor() {
    // Khi Component A set payload -> effect chạy và gọi API
    effect(() => {
      const p = this.bus.payload();
      if (!p) return;
      this.fetch(p.category, p.model);
    });
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

    // Nếu BE nhận slug, bật dòng dưới:
    // const modelParam = this.slugify(model);
    const modelParam = model; // hoặc dùng slugify(model) tuỳ BE

    const params = new HttpParams()
      .set('category', category) // vd: Cases
      .set('model', modelParam); // vd: iphone-16-pro-max

    this.http
      .get<Product[]>(`${this.apiBase}/api/products`, {
        params,
      })
      .subscribe({
        next: (res) => {
          this.items.set(res);
          this.loading.set(false);
        },
        error: (err) => {
          this.error.set(err?.error?.message || 'Load sản phẩm thất bại');
          this.loading.set(false);
        },
      });
  }

  ngOnDestroy() {
    // tuỳ chọn: xoá state khi rời trang
    this.bus.clear();
  }
}
