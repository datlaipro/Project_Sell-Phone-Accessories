import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ProductDetailDto } from './product-detail.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ProductDetailService {
  private readonly BASE = '/api/products';

  constructor(private http: HttpClient) {}

  getDetail(id: number, limitImages = 4): Observable<ProductDetailDto> {
    return this.http.get<ProductDetailDto>(`${this.BASE}/${id}/detail`, {
      params: { limitImages: String(limitImages) },
    });
    // JSON trả về đúng schema ProductDetailDto
  }
}
