import {
  Component,
  ViewChildren,
  QueryList,
  ElementRef,
  HostListener,
  ViewChild
} from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { NgFor } from '@angular/common';

@Component({
  selector: 'product-list',
  standalone: true,
  imports: [RouterModule, MatToolbarModule, MatButtonModule, NgFor],
  templateUrl: './outstandingProducts.component.html',
  styleUrls: ['./outstandingProducts.component.css'],
})
export class BannerProductList {
  productsPreview = [
    {
      id: 1,
      image:
        'https://cdn.tgdd.vn/Products/Images/42/329136/iphone-16-pink-600x600.png',
      name: 'OtterBox Frē Series',
      price: '79,99-99,99',
    },
    {
      id: 2,
      image:
        'https://www.otterbox.com/on/demandware.static/-/Sites-ob_us-Library/default/dwe44a2efc/2024_Files/series_comparison_carousel/defender-pro-realtree-960x960.png',
      name: 'OtterBox Frē Series',
      price: '79,99-99,99',
    },
    {
      id: 3,
      image:
        'https://www.otterbox.com/on/demandware.static/-/Sites-ob_us-Library/default/dwd393ce38/2024_Files/series_comparison_carousel/fre-black-960x960.png',
      name: 'OtterBox Frē Series',
      price: '79,99-99,99',
    },
    {
      id: 4,
      image:
        'https://www.otterbox.com/on/demandware.static/-/Sites-ob_us-Library/default/dwe8a959a1/2024_Files/series_comparison_carousel/symmetry-camera-clear-960x960.png',
      name: 'OtterBox Frē Series',
      price: '79,99-99,99',
    },
    {
      id: 5,
      image:
        'https://www.otterbox.com/on/demandware.static/-/Sites-ob_us-Library/default/dw013476de/2024_Files/series_comparison_carousel/commuter-green-960x960.png',
      name: 'OtterBox Frē Series',
      price: '79,99-99,99',
    },
    {
      id: 6,
      image:
        'https://www.otterbox.com/on/demandware.static/-/Sites-ob_us-Library/default/dw26e1aa62/2024_Files/series_comparison_carousel/defender-pro-xt-floral-960x960.png',
      name: 'OtterBox Frē Series',
      price: '79,99-99,99',
    },
    {
      id: 7,
      image:
        'https://www.otterbox.com/on/demandware.static/-/Sites-ob_us-Library/default/dw5f3656df/2024_Files/series_comparison_carousel/symmetry-stardust-960x960.png',
      name: 'OtterBox Frē Series',
      price: '79,99-99,99',
    },
    {
      id: 8,
      image:
        'https://www.otterbox.com/on/demandware.static/-/Sites-ob_us-Library/default/dwb7a78746/2024_Files/series_comparison_carousel/lumen-pink-960x960.png',
      name: 'OtterBox Frē Series',
      price: '79,99-99,99',
    },
    {
      id: 9,
      image:
        'https://www.otterbox.com/on/demandware.static/-/Sites-ob_us-Library/default/dwce778b04/2024_Files/series_comparison_carousel/symmetry-cactus-brown-960x960.png',
      name: 'OtterBox Frē Series',
      price: '79,99-99,99',
    },
    {
      id: 10,
      image:
        'https://www.otterbox.com/on/demandware.static/-/Sites-ob_us-Library/default/dw028c3c2b/2024_Files/series_comparison_carousel/statement-green-960x960.png',
      name: 'OtterBox Frē Series',
      price: '79,99-99,99',
    },
  ];


 @ViewChild('vp', { static: true }) vp!: ElementRef<HTMLDivElement>;

readonly visible = 6;
viewportWidth: number | null = null;   // ⭐ bắt đầu = null để không sinh width:0

private cardW = 250;
private gap = 13;

atStart = true;
atEnd = false;

ngAfterViewInit() {
  const viewport = this.vp.nativeElement;
  const rail = viewport.querySelector('.rail') as HTMLElement | null;
  const first = rail?.querySelector('.display') as HTMLElement | null;

  // đo kích thước thực (gồm border) ổn định hơn
  if (first) this.cardW = Math.round(first.getBoundingClientRect().width);

  // đọc đúng gap ở .rail
  if (rail) {
    const g = parseInt(getComputedStyle(rail).gap || '0', 10);
    if (!Number.isNaN(g)) this.gap = g;
  }

  // nếu .rail có padding hai bên, cộng thêm:
  const railStyle = rail ? getComputedStyle(rail) : null;
  const pad =
    (railStyle ? parseInt(railStyle.paddingLeft || '0', 10) : 0) +
    (railStyle ? parseInt(railStyle.paddingRight || '0', 10) : 0);

  // KHÓA viewport đúng 5 thẻ (có chống lệch pixel)
  this.viewportWidth = Math.ceil(this.visible * this.cardW + (this.visible - 1) * this.gap + pad);
  // cập nhật nút
  this.updateArrowState();
}

scroll(dir: number) {
  const step = this.cardW + this.gap;
  this.vp.nativeElement.querySelector('.rail')
    ?.scrollBy({ left: dir * step, behavior: 'smooth' });
  requestAnimationFrame(() => this.updateArrowState());
}

onScroll() { this.updateArrowState(); }

private updateArrowState() {
  const rail = this.vp.nativeElement.querySelector('.rail') as HTMLElement | null;
  if (!rail) return;
  const step = this.cardW + this.gap;
  const maxIdx = Math.max(0, this.totalItems() - this.visible);
  const idx = Math.round((rail.scrollLeft || 0) / step);
  this.atStart = idx <= 0;
  this.atEnd = idx >= maxIdx;
}

private totalItems() {
  return this.vp.nativeElement.querySelectorAll('.display').length;
}

}
