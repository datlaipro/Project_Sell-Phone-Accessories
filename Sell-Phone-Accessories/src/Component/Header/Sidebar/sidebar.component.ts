import { Component } from '@angular/core';
import { CommonModule, NgFor } from '@angular/common';
import { RouterLink } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatExpansionModule } from '@angular/material/expansion';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    CommonModule, NgFor, RouterLink,
    MatButtonModule, MatMenuModule, MatIconModule,
    MatSidenavModule, MatListModule, MatExpansionModule,
  ],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
})
export class SidebarComponent {
  expanded: 'cases' | null = null;

  products = [
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
        { id: 5, name: 'iPad (A16)' },
        { id: 6, name: 'iPad Air 11" (M3)' },
        { id: 7, name: 'iPad Air 13" (M3)' },
        { id: 8, name: 'iPad mini (A17 Pro)' },
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

  layId(id: number) { console.log('ID click:', id); }
}
