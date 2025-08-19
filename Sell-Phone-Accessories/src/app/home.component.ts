import { Component } from '@angular/core';
import { ADS } from '../Component/Header/ADS/ads.component';
import { Brand } from '../Component/Header/Brand/brandLogo.component';
import { BannerProductList } from '../Component/Main/outstandingProducts/outstandingProducts.component';
import { FeaturedCollections } from '../Component/Main/FeaturedCollections/FeaturedCollections.component';
import { About } from '../Component/Main/About/about.component';

@Component({
  standalone: true,
  selector: 'app-home',
  imports: [ADS, Brand, BannerProductList, FeaturedCollections, About],
  template: `
    <ads></ads>
    <brand></brand>
    <product-list></product-list>
    <featuredcollections></featuredcollections>
    <about></about>
  `,
})
export class HomeComponent {}
