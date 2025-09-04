import {
  Component,
  signal,
  inject,
  ViewChild,
  ElementRef,
  QueryList,
  ViewChildren,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
// thêm import
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { catchError, switchMap } from 'rxjs/operators';
import { throwError } from 'rxjs';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatTabsModule } from '@angular/material/tabs';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { environment } from '../../environments/environment.development';
type ImgItem = { file?: File; url: string };

@Component({
  selector: 'app-add-product',
  standalone: true,
  imports: [
    CommonModule,

    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatChipsModule,
    MatTabsModule,
    MatSlideToggleModule,
    MatListModule,
    MatDividerModule,
    HttpClientModule,
  ],
  templateUrl: './addNewProducts.component.html',
  styleUrls: ['./addNewProducts.component.css'],
})
export class ProductFormComponent {
  private http = inject(HttpClient);
  apiBase = environment.apiUrl;

  private fb = inject(FormBuilder);

  form = this.fb.group({
    name: ['', Validators.required],
    brand: ['', Validators.required],
    category: ['', Validators.required],
    quantity: [
      1,
      [
        Validators.required,
        Validators.min(1),
        Validators.pattern(/^\d+$/), // chỉ cho số nguyên dương
      ],
    ],
    price: [''],
    discount: [''],
    description: [''],
    visible: [true],
    productType: [''], // ví dụ: "Case"
  });
  // gallery state
  images = signal<ImgItem[]>([]);
  coverIndex = signal(0);

  // color state
  presetColors = [
    '#000000',
    '#ffffff',
    '#ff4d4f',
    '#faad14',
    '#52c41a',
    '#1677ff',
    '#722ed1',
    '#ff6b6b',
    '#f59e0b',
  ];
  selectedColors = signal<string[]>([]);
  brands = [
    'Samsung',
    'Apple',
    'Xiaomi',
    'Oppo',
    'Vivo',
    'Realme',
    'OnePlus',
    'Nokia',
  ];

  // form

  @ViewChild('fileInp') fileInp?: ElementRef<HTMLInputElement>;
  @ViewChildren('specInput') specInputs!: QueryList<
    ElementRef<HTMLInputElement | HTMLTextAreaElement>
  >;

  private resetAll() {
    // 1) Revoke tất cả object URLs để tránh leak
    this.images().forEach((img) => {
      try {
        if (img.url && img.url.startsWith('blob:'))
          URL.revokeObjectURL(img.url);
      } catch {}
    });

    // 2) Clear gallery + cover + màu
    this.images.set([]);
    this.coverIndex.set(0);
    this.selectedColors.set([]);

    // 3) Reset form về giá trị mặc định
    this.form.reset({
      name: '',
      brand: '',
      category: '',
      quantity: 1,
      price: '',
      discount: '',
      description: '',
      visible: true,
    });
    if (this.specInputs) {
      this.specInputs.forEach((ref) => {
        const el = ref.nativeElement as HTMLInputElement | HTMLTextAreaElement; // giúp TypeScript hiểu đúng type của element để có thể truy cập value
        el.value = '';
        // kích hoạt change detection/binding nếu có dùng [(ngModel)] hay listener
        el.dispatchEvent(new Event('input', { bubbles: true }));
      });
    }

    // 4) Dọn trạng thái touched/dirty (không bắt buộc nhưng sạch sẽ)
    this.form.markAsPristine();
    this.form.markAsUntouched();

    // 5) Xóa giá trị input file (để lần sau chọn lại cùng file vẫn bắt sự kiện)
    if (this.fileInp?.nativeElement) {
      this.fileInp.nativeElement.value = '';
    }
  }
  // ===== Images =====
  onFilesSelected(ev: Event) {
    const input = ev.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const picked: ImgItem[] = [];
    Array.from(input.files).forEach((file) => {
      const url = URL.createObjectURL(file);
      picked.push({ file, url });
    });
    // append to gallery
    const current = this.images();
    this.images.set([...current, ...picked]);

    // set cover to first if empty
    if (current.length === 0) this.coverIndex.set(0);

    // reset input so same files can be re-picked later
    input.value = '';
  }

  removeImage(idx: number) {
    const arr = [...this.images()];
    if (idx < 0 || idx >= arr.length) return;
    URL.revokeObjectURL(arr[idx].url);
    arr.splice(idx, 1);
    this.images.set(arr);
    // adjust cover index
    if (arr.length === 0) this.coverIndex.set(0);
    else if (this.coverIndex() >= arr.length)
      this.coverIndex.set(arr.length - 1);
  }

  setCover(idx: number) {
    if (idx >= 0 && idx < this.images().length) this.coverIndex.set(idx);
  }

  // ===== Colors =====
  toggleColor(hex: string) {
    const set = new Set(this.selectedColors());
    if (set.has(hex)) set.delete(hex);
    else set.add(hex);
    this.selectedColors.set([...set]);
  }

  addCustomColor(ev: Event) {
    const input = ev.target as HTMLInputElement;
    const val = input.value;
    if (val) {
      const set = new Set(this.selectedColors());
      set.add(val);
      this.selectedColors.set([...set]);
    }
  }

  isColorSelected(hex: string) {
    return this.selectedColors().includes(hex);
  }

  // ===== Submit demo =====
  saveDraft() {
    // handle submit; you can map images() and selectedColors() to payload here
    console.log('Draft data:', this.form.value, {
      colors: this.selectedColors(),
      images: this.images().length,
      coverIndex: this.coverIndex(),
    });
  }

  publish() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const specsObj = Object.fromEntries(
      // gom specs từ các input
      this.specInputs
        .toArray()
        .map((el) => {
          const key = el.nativeElement.getAttribute('data-key') || '';
          const val = (el.nativeElement.value || '').trim();
          return [key.trim(), val];
        })
        .filter(([k, v]) => k && v)
        // cắt độ dài an toàn theo DB: attr ≤ 100, val ≤ 500
        .map(([k, v]) => [k.slice(0, 100), v.slice(0, 500)])
    );

    const v = this.form.value as any;

    const data: any = {
      name: v.name,
      brand: v.brand,
      category: v.category,
      quantity: Number(v.quantity ?? 1),
      price: Number(v.price ?? 0),
      description: v.description || '',
      coverIndex: this.coverIndex(),
      // BE đang dùng String color -> join mảng
      color: this.selectedColors().join(',') || undefined,
    };
    if (Object.keys(specsObj).length > 0) {
      (data as any).specs = specsObj;
    }

    // 👇 Nếu người dùng không nhập discount hoặc để trống -> KHÔNG set field
    if (
      v.discount !== null &&
      v.discount !== '' &&
      !isNaN(Number(v.discount))
    ) {
      data.discount = Number(v.discount); // % 0..100
    }

    const fd = new FormData();
    fd.append(
      'data',
      new Blob([JSON.stringify(data)], { type: 'application/json' })
    );

    this.images().forEach((img, i) => {
      if (img.file)
        fd.append('files', img.file, img.file.name || `image_${i}.jpg`);
    });

    this.http
      .post(`${this.apiBase}/admin/addProduct`, fd, { withCredentials: true })
      .pipe(
        catchError((err) => {
          if (err.status === 403) {
            // gọi refresh để lấy access token mới rồi retry request cũ
            return this.http
              .post<void>(`${this.apiBase}/admin/refresh`, null, {
                withCredentials: true,
              })
              .pipe(
                switchMap(() =>
                  this.http.post(`${this.apiBase}/admin/addProduct`, fd, {
                    withCredentials: true,
                  })
                )
              );
          }
          return throwError(() => err);
        })
      )
      .subscribe({
        next: (res) => {
          this.resetAll();
          console.log('Publish OK', res);
        },
        error: (err) => console.error('Publish failed', err),
      });
  }
}
