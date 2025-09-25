import { Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductDetailService } from './product-detail.service';
import { ProductDetailDto, ProductImageDto } from './product-detail.model';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CommonModule } from '@angular/common';
@Component({
  standalone: true,
  selector: 'product-detail',
  imports: [CommonModule, MatIconModule, MatButtonModule, MatTooltipModule],
  templateUrl: './productsDetail.component.html',
  styleUrls: ['./productsDetail.component.css'],
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
    const raw = localStorage.getItem(this.wishKey);
    if (raw) {
      try {
        const arr: number[] = JSON.parse(raw);
        this.wished = new Set(arr);
      } catch {}
    }
  }
  private fetch(id: number): void {
    this.loading = true;
    this.error = '';

    this.srv.getDetail(id, 4).subscribe({
      next: (raw) => {
        // 1) Map từ BE -> ProductDetailDto đúng schema FE
        const mapped = this.mapFromBackend(raw);

        // 2) Chuẩn hoá mediaImages còn tối đa 4 tấm
        this.product = this.normalize(mapped);

        // 3) Ảnh preview mặc định
        this.activeImageUrl =
          this.product.coverImage ||
          (this.product.mediaImages.length
            ? this.product.mediaImages[0].url
            : null);

        this.loading = false;
      },
      error: () => {
        this.error = 'Không tải được sản phẩm.';
        this.loading = false;
      },
    });
  }

  /** ========== Helpers: map dữ liệu từ BE về đúng ProductDetailDto ========== */
  private mapFromBackend(raw: any): ProductDetailDto {
    // --- lấy specs nếu BE trả dạng [{attr,val}] ---
    const specMap = new Map<string, string>();
    (raw?.specs || []).forEach((s: any) => {
      const k = (s?.attr || '').trim().toLowerCase();
      const v = (s?.val || '').trim();
      if (k && v && !specMap.has(k)) specMap.set(k, v); // giữ bản đầu tiên
    });
    const get = (k: string) => specMap.get(k.toLowerCase());

    // --- tiện ích tách CSV/ xuống dòng ---
    const splitCsv = (v?: string): string[] | undefined => {
      if (!v) return undefined;
      const list = v
        .split(/[,\n]/)
        .map((x) => x.trim())
        .filter(Boolean);
      return list.length ? list : undefined;
    };

    // --- chuẩn hoá mediaImages (BE có thể trả key khác) ---
    const mediaImages: ProductImageDto[] = (raw?.mediaImages || [])
      .map((x: any) => ({
        url: x?.url ?? x?.imageUrl ?? x, // phòng mapper khác key
        sortOrder: x?.sortOrder ?? x?.sort ?? null,
        cover: !!(x?.cover ?? false),
      }))
      .filter((x: ProductImageDto) => !!x.url);

    // --- ép số nếu BE trả BigDecimal (string) ---
    const toNum = (v: any): number | null => {
      if (v === null || v === undefined) return null;
      const n = typeof v === 'string' ? Number(v) : (v as number);
      return isNaN(n) ? null : n;
    };

    // --- compatibility/materials có thể là mảng hoặc chuỗi CSV ---
    // Ưu tiên đọc từ field trực tiếp; nếu không có thì fallback lấy từ specs
    const compatibilityRaw = raw?.compatibility ?? get('compatibility');
    const materialsRaw = raw?.materials ?? get('materials');

    // nếu bạn muốn giữ union (string[] | string), trả như dưới:
    const compatibility: string[] | string | undefined = Array.isArray(
      compatibilityRaw
    )
      ? compatibilityRaw
      : typeof compatibilityRaw === 'string'
      ? splitCsv(compatibilityRaw) ?? compatibilityRaw
      : undefined;

    const materials: string[] | string | undefined = Array.isArray(materialsRaw)
      ? materialsRaw
      : typeof materialsRaw === 'string'
      ? splitCsv(materialsRaw) ?? materialsRaw
      : undefined;

    // weight/dimensions ưu tiên field trực tiếp -> specs
    const dimensions: string | undefined =
      raw?.dimensions ?? get('dimensions') ?? undefined;
    const weight: string | number | undefined =
      raw?.weight ?? get('weight') ?? undefined;

    // coverImage có thể là null; nếu null dùng ảnh đầu tiên
    const coverImage: string | null =
      (raw?.coverImage && String(raw.coverImage)) ||
      (mediaImages.length ? mediaImages[0].url : null);

    const dto: ProductDetailDto = {
      id: Number(raw?.id),
      name: String(raw?.name ?? ''),
      description: String(raw?.description ?? ''),

      price: (toNum(raw?.price) ?? undefined) as any, // interface đang là number
      discount: (toNum(raw?.discount) ?? undefined) as any, // ^
      finalPrice: (toNum(raw?.finalPrice) ?? undefined) as any,

      colorName: raw?.colorName ?? null,
      availability:
        raw?.availability === 'In Stock' || raw?.availability === 'Out of Stock'
          ? raw.availability
          : raw?.quantity > 0
          ? 'In Stock'
          : 'Out of Stock',

      sku: raw?.sku ?? '',
      brand: raw?.brand ?? null,
      categoryName: raw?.categoryName ?? null,

      coverImage,
      mediaImages,

      rate: raw?.rate ?? null,
      quantity: raw?.quantity ?? null,

      // 4 specs mới (union đúng theo interface của bạn)
      compatibility,
      dimensions,
      weight,
      materials,
    };

    return dto;
  }

  // đảm bảo luôn có tối đa 4 “tiles” cho media-grid
  private normalize(p: ProductDetailDto): ProductDetailDto {
    const images: ProductImageDto[] = Array.isArray(p.mediaImages)
      ? p.mediaImages
      : [];
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
    const rate = Math.max(0, Math.min(5, Math.round(this.product?.rate || 0)));
    return Array.from({ length: 5 }, (_, i) => (i < rate ? 1 : 0));
  }

  isInStock(): boolean {
    return this.product?.availability === 'In Stock';
  }

  priceFormatted(n?: number | null) {
    if (typeof n !== 'number') return '';
    // Đổi định dạng tuỳ locale/currency của bạn
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(n);
  }
  toArray(v: string | string[] | null | undefined): string[] {
    if (!v) return [];
    if (Array.isArray(v))
      return v
        .filter(Boolean)
        .map((s) => String(s).trim())
        .filter(Boolean);
    // v là chuỗi: hỗ trợ cả CSV và xuống dòng
    return String(v)
      .split(/[,\n]/)
      .map((s) => s.trim())
      .filter(Boolean);
  }

  // xử lí chức năng thêm sản phẩm vào wishlist
  private wishKey = 'wishlist_ids';
  wished = new Set<number>();

  isWished(id: number): boolean {
    return this.wished.has(id);
  }

  toggleWish(id: number): void {
    if (this.wished.has(id)) {
      this.wished.delete(id);
    } else {
      this.wished.add(id);
    }
    localStorage.setItem(this.wishKey, JSON.stringify(Array.from(this.wished)));

    // TODO: nếu có API:
    // (this.isWished(id) ? this.wishlistService.add(id) : this.wishlistService.remove(id))
    //   .subscribe();
  }
}
