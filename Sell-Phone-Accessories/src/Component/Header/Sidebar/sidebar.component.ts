import { Component, ViewEncapsulation, inject } from '@angular/core';
import { CommonModule, NgFor } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatExpansionModule } from '@angular/material/expansion';

import { environment } from '../../../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { ProductList } from '../../../service/ProductList.service';

interface SimpleItem { id: number; name: string; }
interface Group { title: string; product: SimpleItem[]; }

@Component({
  selector: 'app-sidebar',
  standalone: true,
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
  encapsulation: ViewEncapsulation.None,
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
})
export class SidebarComponent {
  apiBase = environment.apiUrl;
  private http = inject(HttpClient);
  private router = inject(Router);
  bus = inject(ProductList);

  // Trạng thái mở panel mobile
  expanded: 'cases' | 'screen-protection' | 'power' | 'accessories' | 'customize' | 'sale' | 'explore' | null = null;

  // Trạng thái dropdown desktop
  isOn = false;
  toggle() { this.isOn = !this.isOn; }

  // ====== DỮ LIỆU MENU ======
  products: Group[] = [
    {
      title: 'Apple iPhone',
      product: [
        { id: 1, name: 'iPhone 16 Pro Max' },
        { id: 2, name: 'iPhone 16 Pro' },
        { id: 3, name: 'iPhone 16 Plus' },
        { id: 4, name: 'iPhone 16e' },
      ],
    },
    {
      title: 'Apple iPad',
      product: [
        { id: 5,  name: 'iPad (A16)' },
        { id: 6,  name: 'iPad Air 11" (M3)' },
        { id: 7,  name: 'iPad Air 13" (M3)' },
        { id: 8,  name: 'iPad mini (A17 Pro)' },
      ],
    },
    {
      title: 'Samsung',
      product: [
        { id: 9,  name: 'Galaxy S25 Ultra' },
        { id: 10, name: 'Galaxy S25' },
        { id: 11, name: 'Galaxy S25+' },
        { id: 12, name: 'Galaxy S25 Edge' },
      ],
    },
  ];

  screenProtection: Group[] = [
    {
      title: 'Apple iPhone',
      product: [
        { id: 1,  name: 'iPhone 16 Pro Max' },
        { id: 2,  name: 'iPhone 16 Pro' },
        { id: 3,  name: 'iPhone 16' },
        { id: 4,  name: 'iPhone 16 Plus' },
        { id: 5,  name: 'iPhone 16e' },
        { id: 6,  name: 'iPhone 15 Pro Max' },
        { id: 7,  name: 'iPhone 15 Pro' },
        { id: 8,  name: 'iPhone 15' },
        { id: 9,  name: 'iPhone 15 Plus' },
        { id: 10, name: 'iPhone 14 Pro Max' },
        { id: 11, name: 'iPhone 14 Pro' },
        { id: 12, name: 'iPhone 14' },
        { id: 13, name: 'iPhone 14 Plus' },
        { id: 14, name: 'Shop All' },
      ],
    },
    {
      title: 'Apple iPad',
      product: [
        { id: 15, name: 'iPad (A16)' },
        { id: 16, name: 'iPad Air 11-inch (M3)' },
        { id: 17, name: 'iPad Air 13-inch (M3)' },
        { id: 18, name: 'iPad mini (A17 Pro)' },
        { id: 19, name: 'iPad Air 11-inch (M2)' },
        { id: 20, name: 'iPad Air 13-inch (M2)' },
        { id: 21, name: 'iPad Pro 11-inch (M4)' },
        { id: 22, name: 'iPad Pro 13-inch (M4)' },
        { id: 23, name: 'iPad (10th gen)' },
        { id: 24, name: 'iPad mini (6th gen)' },
        { id: 25, name: 'iPad (9th gen)' },
        { id: 26, name: 'Shop All' },
      ],
    },
    {
      title: 'Samsung',
      product: [
        { id: 27, name: 'Galaxy S25 Ultra' },
        { id: 28, name: 'Galaxy S25' },
        { id: 29, name: 'Galaxy S25+' },
        { id: 30, name: 'Galaxy S24 FE' },
        { id: 31, name: 'Galaxy S24 Ultra' },
        { id: 32, name: 'Galaxy S24' },
        { id: 33, name: 'Galaxy S24+' },
        { id: 34, name: 'Galaxy S23 FE' },
        { id: 35, name: 'Galaxy S23 Ultra' },
        { id: 36, name: 'Galaxy S23+' },
        { id: 37, name: 'Galaxy S23' },
        { id: 38, name: 'Galaxy A Series' },
        { id: 39, name: 'Samsung Tablets' },
        { id: 40, name: 'Shop All' },
      ],
    },
    {
      title: 'More Devices',
      product: [
        { id: 41, name: 'Google' },
        { id: 42, name: 'Motorola' },
        { id: 43, name: 'Shop All' },
      ],
    },
  ];

  power: Group[] = [
    {
      title: 'MagSafe',
      product: [
        { id: 1, name: 'Wireless Chargers' },
        { id: 2, name: 'Charging Mounts' },
        { id: 3, name: 'Charger Stands' },
        { id: 4, name: 'Shop All' },
      ],
    },
    {
      title: 'Wireless Charging',
      product: [
        { id: 5, name: 'MagSafe' },
        { id: 6, name: 'Qi Wireless Charging' },
        { id: 7, name: 'Shop All' },
      ],
    },
    {
      title: 'Power Banks',
      product: [
        { id: 8, name: 'Wireless Power Banks' },
        { id: 9, name: 'Mobile Charging Kits' },
        { id: 10, name: 'Shop All' },
      ],
    },
    {
      title: 'Cables',
      product: [
        { id: 11, name: 'USB-C to USB-C' },
        { id: 12, name: 'Lightning to USB-C' },
        { id: 13, name: 'Lightning to USB-A' },
        { id: 14, name: 'Micro-USB to USB-A' },
        { id: 15, name: 'Shop All' },
      ],
    },
    {
      title: 'Wall Charging',
      product: [
        { id: 16, name: 'Wall Chargers' },
        { id: 17, name: 'Wall Charging Kits' },
        { id: 18, name: 'Shop All' },
      ],
    },
    {
      title: 'Car Charging',
      product: [
        { id: 19, name: 'Car Chargers' },
        { id: 20, name: 'Car Charging Kits' },
        { id: 21, name: 'Shop All' },
      ],
    },
  ];

  // Tái sử dụng "power" làm dữ liệu mẫu cho Accessories/Customize/Sale/Explore (giống cách bạn đang dùng)
  accessories: Group[] = this.power;
  customize: Group[] = this.power;
  sale: Group[] = this.power;
  explore: Group[] = this.power;

  /** Điều hướng thống nhất (desktop + mobile) */
  selectAndGo(model: string, category: string) {
    // Lưu để trang /product có thể fetch kể cả khi refresh
    this.bus.payload.set({ category, model });
    localStorage.setItem('productFilters', JSON.stringify({ category, model }));

    // Điều hướng
    this.router.navigate(['/product'], {
      queryParams: { category, model },
    });

    // Đóng mọi dropdown desktop
    this.isOn = false;
  }
}
