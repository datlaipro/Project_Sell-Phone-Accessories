import { Component, ElementRef, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { tap, catchError, map, shareReplay } from 'rxjs/operators';
import { switchMap, throwError } from 'rxjs';
import { BehaviorSubject, of } from 'rxjs';

import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatBadgeModule } from '@angular/material/badge';
import { RouterLink, Router } from '@angular/router';
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
  private router = inject(Router);

  auth = inject(AuthService); // dữ liệu user từ api auth/me
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

  isAccountOpen = false;

 onLogout() {
  const logout$ = () =>
    this.http.post<void>(`${this.apiBase}/auth/logout`, null, {
      withCredentials: true,
    });

  logout$().pipe(
    // Nếu access token hết hạn → thử refresh rồi logout lại
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401 || err.status === 403) {
        return this.http.post<void>(`${this.apiBase}/auth/refresh`, null, {
          withCredentials: true,
        }).pipe(
          switchMap(() => logout$()),
          // Refresh fail (401 / token reuse) → hard signout
          catchError((e: HttpErrorResponse) => {
            this.hardSignout(e);
            return of(void 0);
          })
        );
      }
      // Lỗi khác → hard signout luôn
      this.hardSignout(err);
      return of(void 0);
    })
  ).subscribe({
    next: () => this.hardSignout(), // logout OK → vẫn clear state & điều hướng
    error: (e) => this.hardSignout(e),
  });
}

private hardSignout(err?: any) {
  if (err?.error?.message) {
    console.warn('Client signout:', err.error.message);
  }
  this.auth.setUser(null);
  this.router.navigateByUrl('/auth');
}

}
