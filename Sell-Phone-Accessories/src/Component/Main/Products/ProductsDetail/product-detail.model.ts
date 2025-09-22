export interface ProductImageDto {
  url: string;
  sortOrder: number | null;
  cover: boolean;
}

export interface ProductDetailDto {
  id: number;
  name: string;               // pd-title
  description: string;        // mô tả dài
  price: number;              // giá gốc
  discount: number;           // % nếu backend đang dùng %
  finalPrice: number;         // đã tính sẵn
  colorName: string;          // "Black" hoặc mã màu/label
  availability: 'In Stock' | 'Out of Stock';
  sku: string;                // "SKU 77-xxxx"
  brand: string | null;
  categoryName: string | null;

  coverImage: string | null;  // ảnh đại diện
  mediaImages: ProductImageDto[]; // tối đa 4 ảnh phụ (loại cover)

  rate: number | null;        // 0..5 (tuỳ backend)
  quantity: number | null;    // tồn kho
}
