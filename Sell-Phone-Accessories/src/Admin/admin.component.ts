import { Component, ViewChild, inject } from '@angular/core';
import { AsyncPipe, DecimalPipe, NgFor, NgIf } from '@angular/common';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { map, shareReplay } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { MatSidenavModule, MatSidenav } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { MatBadgeModule } from '@angular/material/badge';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    // Angular
    NgFor, NgIf, AsyncPipe, DecimalPipe,
    // Material
    MatSidenavModule, MatToolbarModule, MatIconModule,
    MatButtonModule, MatListModule, MatDividerModule,
    MatBadgeModule, MatMenuModule, MatTooltipModule
  ],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminDashboardComponent {
  @ViewChild('drawer') drawer!: MatSidenav;

  private bo = inject(BreakpointObserver);
  /** true khi là thiết bị handset (điện thoại) */
  isHandset$: Observable<boolean> = this.bo
    .observe([Breakpoints.Handset])
    .pipe(map(r => r.matches), shareReplay(1));

  // Bảng Recent Sales (mock)
  sales = [
    { img: 'IMG', name: 'Bamboo Watch', price: 65 },
    { img: 'IMG', name: 'Black Watch', price: 72 },
    { img: 'IMG', name: 'Blue Band', price: 79 },
    { img: 'IMG', name: 'Blue T-Shirt', price: 29 },
    { img: 'IMG', name: 'Bracelet', price: 15 },
  ];

  // Best selling (mock %)
  best = [
    { title: 'Space T-Shirt', cat: 'Clothing', pct: 50 },
    { title: 'Portal Sticker', cat: 'Accessories', pct: 16 },
    { title: 'Supernova Sticker', cat: 'Accessories', pct: 67 },
    { title: 'Wonders Notebook', cat: 'Office', pct: 35 },
    { title: 'Mat Black Case', cat: 'Accessories', pct: 75 },
    { title: 'Robots T-Shirt', cat: 'Clothing', pct: 40 },
  ];

  // Thông báo (mock)
  notes = [
    { icon: 'payments',  text: 'Richard Jones has purchased a blue t-shirt for $79.00', sub: 'TODAY' },
    { icon: 'account_balance', text: 'Your withdrawal of $2500.00 has been initiated.', sub: 'TODAY' },
    { icon: 'person', text: 'Keyser Wick purchased a black jacket for $59.00', sub: 'YESTERDAY' },
    { icon: 'help', text: 'Jane Davis posted a new question about your product.', sub: 'YESTERDAY' },
    { icon: 'trending_up', text: 'Your revenue has increased by $25.', sub: 'LAST WEEK' },
    { icon: 'favorite', text: '12 users added your products to their wishlist.', sub: 'LAST WEEK' },
  ];

  toggleDrawer(): void {
    this.drawer.toggle();
  }
}
