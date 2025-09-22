import { Component, HostListener, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { ProductDetailService } from './product-detail.service';
import { ProductDetailDto, ProductImageDto } from './product-detail.model';
import { CommonModule } from '@angular/common';
@Component({
  standalone: true,
  selector: "product-detail",
  imports: [CommonModule], 
  templateUrl: "./productsDetail.component.html",
  styleUrls: ["./productsDetail.component.css"],
})


export class ProductsDetail implements OnInit {
  
    product?: ProductDetailDto;
  loading = true;
  error = '';
  qty = 1;

  // slideshow đơn giản
  activeImageUrl: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private srv: ProductDetailService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.fetch(id);
  }

  private fetch(id: number): void {
    this.loading = true;
    this.error = '';
    this.srv.getDetail(id, 4).subscribe({
      next: (res) => {
        this.product = this.normalize(res);
        // ảnh đang xem mặc định: cover nếu có, không thì ảnh phụ đầu tiên
        this.activeImageUrl =
          this.product.coverImage ||
          (this.product.mediaImages.length ? this.product.mediaImages[0].url : null);
        this.loading = false;
      },
      error: (e) => {
        this.error = 'Không tải được sản phẩm.';
        this.loading = false;
      },
    });
  }

  // đảm bảo luôn có tối đa 4 “tiles” cho media-grid
  private normalize(p: ProductDetailDto): ProductDetailDto {
    const images: ProductImageDto[] = Array.isArray(p.mediaImages) ? p.mediaImages : [];
    // cắt đúng 4
    const trimmed = images.slice(0, 4);
    // nếu thiếu, có thể đệm bằng cover để đủ slot (tuỳ bạn)
    while (trimmed.length < 4 && p.coverImage) {
      trimmed.push({ url: p.coverImage, sortOrder: null, cover: true });
    }
    return { ...p, mediaImages: trimmed };
  }

  // ===== UI handlers =====
  selectImage(url: string) {
    this.activeImageUrl = url;
  }

  adjustQty(delta: number) {
    const next = (this.qty || 1) + delta;
    this.qty = next < 1 ? 1 : next;
  }

  onQtyManualChange(raw: string) {
    const n = parseInt(raw.replace(/\D/g, ''), 10);
    this.qty = isNaN(n) || n < 1 ? 1 : n;
  }

  addToCart() {
    if (!this.product) return;
    // TODO: gọi cart service
    console.log('Add to cart', {
      productId: this.product.id,
      qty: this.qty,
    });
  }

  buyNow() {
    if (!this.product) return;
    // TODO: chuyển sang checkout nhanh
    console.log('Buy now', { productId: this.product.id, qty: this.qty });
  }

  starsArray(): number[] {
    const rate = Math.max(0, Math.min(5, Math.round((this.product?.rate || 0))));
    return Array.from({ length: 5 }, (_, i) => (i < rate ? 1 : 0));
  }

  isInStock(): boolean {
    return this.product?.availability === 'In Stock';
  }

  priceFormatted(n?: number | null) {
    if (typeof n !== 'number') return '';
    // Đổi định dạng tuỳ locale/currency của bạn
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(n);
  }

}
