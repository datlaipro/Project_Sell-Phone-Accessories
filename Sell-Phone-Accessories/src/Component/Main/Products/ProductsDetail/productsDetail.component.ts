import { Component, HostListener, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";

@Component({
  standalone: true,
  selector: "product-detail",
  imports: [],
  templateUrl: "./productsDetail.component.html",
  styleUrls: ["./productsDetail.component.css"],
})
export class ProductsDetail implements OnInit {
  // ---- route param
  productId: number = 0;

  // ---- state giống file JS
  qty: number = 1;
  selectedColor: string = "black";

  // Ảnh demo: thay bằng ảnh thật của bạn hoặc dữ liệu trả về từ API
  images: string[] = [
    "https://images.otterbox.com/1.jpg",
    "https://images.otterbox.com/2.jpg",
    "https://images.otterbox.com/3.jpg",
    "https://images.otterbox.com/4.jpg",
  ];

  // Lightbox
  lightboxOpen = false;
  lightboxSrc: string = "";

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get("id");
    this.productId = id ? +id : 0;

    // TODO: gọi API bằng productId -> gán images, price, colors...
    // ví dụ:
    // this.productService.getById(this.productId).subscribe(p => {
    //   this.images = p.images;
    //   this.selectedColor = p.defaultColor;
    // });
  }

  /* ====== Lightbox (mô phỏng JS) ====== */
  openLightbox(src: string): void {
    this.lightboxSrc = src;
    this.lightboxOpen = true;
  }

  closeLightbox(): void {
    this.lightboxOpen = false;
    this.lightboxSrc = "";
  }

  @HostListener("document:keydown.escape")
  onEsc() {
    if (this.lightboxOpen) this.closeLightbox();
  }

  /* ====== Qty cộng/trừ (mô phỏng JS) ====== */
  adjustQty(delta: number): void {
    const next = this.qty + delta;
    this.qty = next < 1 ? 1 : next;
  }

  onQtyManualChange(v: string): void {
    const n = parseInt(v, 10);
    this.qty = isNaN(n) || n < 1 ? 1 : n;
  }

  /* ====== Thao tác khác ====== */
  addToCart(): void {
    // Gọi service add-to-cart.
    // console.log({ productId: this.productId, qty: this.qty, color: this.selectedColor });
  }

  pickColor(color: string): void {
    this.selectedColor = color;
  }

  buyNow(): void {
  // TODO: điều hướng đến checkout hay mở modal thanh toán
  // console.log({ productId: this.productId, qty: this.qty, color: this.selectedColor });
}

}
