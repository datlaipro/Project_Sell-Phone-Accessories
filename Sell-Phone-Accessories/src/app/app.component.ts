import { MatIconModule } from '@angular/material/icon';

import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../Component/Header/Navbar/navbar.component';
import { SidebarComponent } from '../Component/Header/Siderbar/sidebar.component';
import { ADS } from '../Component/Header/ADS/ads.component';
import { BannerProductList } from '../Component/Main/outstandingProducts/outstandingProducts.component';
import { Brand } from '../Component/Header/Brand/brandLogo.component';
import { FeaturedCollections } from '../Component/Main/FeaturedCollections/FeaturedCollections.component';
import { About } from '../Component/Main/About/about.component';
// đây là file giống với app.js bên reactjs
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    NavbarComponent,
    SidebarComponent,
    ADS,
    BannerProductList,
    Brand,
    FeaturedCollections,
    About,
    MatIconModule,
  ],
  template: `
    <app-navbar></app-navbar>
    <app-sidebar></app-sidebar>
    <ads></ads>
    <brand></brand>
    <product-list></product-list>
    <featuredcollections></featuredcollections>
    <about></about>
  `,
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'Sell-Phone-Accessories';
}
