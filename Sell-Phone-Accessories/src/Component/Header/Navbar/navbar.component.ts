import { Component, ElementRef, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatBadgeModule } from '@angular/material/badge';
import { RouterLink } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment.development';
import { StateLogin } from '../../../service/stateLogin.service'; //service quản lí trạng thái đăng nhập
import { NgIf } from '@angular/common';
import { AuthService } from '../../../service/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatBadgeModule,
    RouterLink,
  ],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent {
  auth = inject(AuthService); // <-- expose service ra template
  bus = inject(StateLogin);
  search = new FormControl(''); // khởi tạo biến lưu giá trị tìm kiếm tham chiếu tới [formControl]="search" để thực hiện thay đổi
  // dữ liệu 2 chiều
  cartCount = 2; // demo
  apiBase = environment.apiUrl;
  private http = inject(HttpClient);

  @ViewChild('searchBox') searchBox!: ElementRef<HTMLInputElement>; // lấy giá trị từ input tìm kiếm

  focusSearch() {
    this.searchBox?.nativeElement?.focus(); //nativeElement là thuộc tính của ElementRef để truy cập dom thật
  }

  onSearch() {
    const q = (this.search.value || '').trim();
    if (!q) return;
    console.log('🔎 Searching:', q);
  }

  onCartClick() {
    console.log('🛒 Mở giỏ hàng');
  }

  account = '';
  state = '';
  ngOnInit() {
    this.http
      .post(`${this.apiBase}/auth/me`, {
        withCredentials: true, // 👈 gửi/nhận cookie
      })
      .subscribe({
        next: (res) => {
          alert('đăng nhập thành công');
          // TODO: điều hướng / thông báo
        },
        error: (err: HttpErrorResponse) => {
          if (err.status === 401) alert('chưa đăng nhập ');
        },
      });
  }
}
