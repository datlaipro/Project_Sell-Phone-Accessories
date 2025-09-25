import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

type WishlistItem = {
  id: number;
  title: string;
  series: string;
  imageUrl: string;
  colorName: string;
  status: 'In Stock' | 'Out of Stock';
  sku: string;
  priceOld?: number;
  priceNow: number;
  qty: number;
  hidePublic: boolean;
};

@Component({
  selector: 'app-wishlist',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './wishlist.component.html',
  styleUrls: ['./wishlist.component.css']
})
export class WishlistComponent {
  allHidden = false;

  items: WishlistItem[] = [
    {
      id: 1,
      title: 'iPhone 16 Pro Holster',
      series: 'Defender Series',
      imageUrl: 'https://pub-2eb5323830294e5ba1d341c9eff577e6.r2.dev/product/2025-09-09/bf0a0e3c-0f78-4577-b1d7-54a2c49a0eed.png',
      colorName: 'Black',
      status: 'In Stock',
      sku: '78-81441',
      priceOld: 19.99,
      priceNow: 14.99,
      qty: 1,
      hidePublic: false,
    },
    {
      id: 2,
      title: 'iPhone 16 Pro Case',
      series: 'Defender Series Pro for MagSafe',
      imageUrl: 'https://via.placeholder.com/160x160?text=Case',
      colorName: 'Black',
      status: 'In Stock',
      sku: '77-96024',
      priceOld: 79.99,
      priceNow: 59.99,
      qty: 1,
      hidePublic: false,
    },
  ];

  trackById = (_: number, item: WishlistItem) => item.id;

  toggleAllHidden(checked: boolean) {
    this.allHidden = checked;
    this.items = this.items.map(i => ({ ...i, hidePublic: checked }));
  }

  toggleItemHidden(item: WishlistItem, checked: boolean) {
    item.hidePublic = checked;
    this.allHidden = this.items.length > 0 && this.items.every(i => i.hidePublic);
  }

  decQty(item: WishlistItem) {
    item.qty = Math.max(1, (item.qty || 1) - 1);
  }

  incQty(item: WishlistItem) {
    item.qty = Math.min(9999, (item.qty || 1) + 1);
  }

  onQtyInput(item: WishlistItem, val: string) {
    const n = Math.max(1, Math.min(9999, Number(val) || 1));
    item.qty = n;
  }

  removeItem(id: number) {
    this.items = this.items.filter(i => i.id !== id);
    this.allHidden = this.items.length > 0 && this.items.every(i => i.hidePublic);
  }

  addToCart(item: WishlistItem) {
    console.log('Add to cart:', { id: item.id, qty: item.qty });
  }
}
