import { NavbarComponent } from '../../Header/Navbar/navbar.component';
import { SidebarComponent } from '../../Header/Sidebar/sidebar.component';
import { Component, ViewEncapsulation,HostListener  } from '@angular/core';
import { CommonModule, NgFor } from '@angular/common';
import { RouterLink } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatExpansionModule } from '@angular/material/expansion';
@Component({
  standalone: true,
  selector: 'product',
  imports: [
    NavbarComponent,
    SidebarComponent,
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

  products = [
    {
      id: 1,
      name: 'iPad Air 11-inch (M3) and iPad Air 11-inch (M2) CaseDefender Series',
      price: '79.99$',
      rate: '4.2',
      img:'',
      Thickness: '3',
      DropRating: '3',
    },   {
      id: 1,
      name: 'iPad Air 11-inch (M3) and iPad Air 11-inch (M2) CaseDefender Series',
      price: '79.99$',
      rate: '4.2',
      img:'',
      Thickness: '3',
      DropRating: '3',
    },   {
      id: 1,
      name: 'iPad Air 11-inch (M3) and iPad Air 11-inch (M2) CaseDefender Series',
      price: '79.99$',
      rate: '4.2',
      img:'',
      Thickness: '3',
      DropRating: '3',
    },   {
      id: 1,
      name: 'iPad Air 11-inch (M3) and iPad Air 11-inch (M2) CaseDefender Series',
      price: '79.99$',
      rate: '4.2',
      img:'',
      Thickness: '3',
      DropRating: '3',
    },   {
      id: 1,
      name: 'iPad Air 11-inch (M3) and iPad Air 11-inch (M2) CaseDefender Series',
      price: '79.99$',
      rate: '4.2',
      img:'',
      Thickness: '3',
      DropRating: '3',
    },   {
      id: 1,
      name: 'iPad Air 11-inch (M3) and iPad Air 11-inch (M2) CaseDefender Series',
      price: '79.99$',
      rate: '4.2',
      img:'',
      Thickness: '3',
      DropRating: '3',
    },   {
      id: 1,
      name: 'iPad Air 11-inch (M3) and iPad Air 11-inch (M2) CaseDefender Series',
      price: '79.99$',
      rate: '4.2',
      img:'',
      Thickness: '3',
      DropRating: '3',
    },   {
      id: 1,
      name: 'iPad Air 11-inch (M3) and iPad Air 11-inch (M2) CaseDefender Series',
      price: '79.99$',
      rate: '4.2',
      img:'',
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
}
