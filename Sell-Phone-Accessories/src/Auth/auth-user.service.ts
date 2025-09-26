// src/app/core/auth/auth-user.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../environments/environment.development';

export type AppUser = {
  id: number | string;
  email: string;
  name?: string;
  roles?: string[]; // có hoặc không, tùy backend
  // ...bổ sung field bạn đang trả về từ /auth/me
};

@Injectable({ providedIn: 'root' })
export class AuthUserService {
  private http = inject(HttpClient);
  private router = inject(Router);
apiBase = environment.apiUrl;
  // trạng thái user hiện tại
  private _user$ = new BehaviorSubject<AppUser | null>(null);
  readonly user$ = this._user$.asObservable();

  // tiện: true nếu đã đăng nhập
  readonly isLoggedIn$: Observable<boolean> = this.user$.pipe(map(u => !!u));

  // cờ để tránh gọi /auth/me nhiều lần đồng thời
  private loadingMe = false;
  private loadedOnce = false;

  /** Gọi thẳng /auth/me để lấy thông tin user hiện tại */
  getMe(): Observable<AppUser | null> {
    const url = `${this.apiBase}/auth/me`;
    return this.http.get<AppUser>(url, { withCredentials: true }).pipe(
      tap(user => this._user$.next(user)),
      map(user => user),
      catchError((_err) => {
        // 401/403… coi như chưa đăng nhập
        this._user$.next(null);
        return of(null);
      })
    );
  }

  /**
   * Đảm bảo trong bộ nhớ đã có user (gọi /auth/me nếu chưa có).
   * Trả về true nếu có user, false nếu chưa đăng nhập.
   */
  ensureMe(): Observable<boolean> {
    // nếu đã load 1 lần và đang có user/null thì trả về luôn
    if (this.loadedOnce && !this.loadingMe) {
      return of(!!this._user$.value);
    }

    if (this.loadingMe) {
      // đang load: khi BehaviorSubject thay đổi thì map sang boolean
      return this.user$.pipe(map(u => !!u));
    }

    this.loadingMe = true;
    return this.getMe().pipe(
      tap(() => {
        this.loadedOnce = true;
        this.loadingMe = false;
      }),
      map(user => !!user),
      catchError((_e) => {
        this.loadedOnce = true;
        this.loadingMe = false;
        return of(false);
      })
    );
  }

  /** Đăng xuất: gọi API (nếu có), xóa state và điều hướng */
  logout(options?: { redirectTo?: string }) {
    const to = options?.redirectTo ?? '/auth';
    const url = `${this.apiBase}/auth/logout`;
    // Nếu backend chưa có /auth/logout, có thể bỏ đoạn gọi API và chỉ clear state
    this.http.post(url, {}, { withCredentials: true }).pipe(
      catchError(() => of(null))
    ).subscribe(() => {
      this._user$.next(null);
      this.router.navigateByUrl(to);
    });
  }

  /** Getter đồng bộ khi cần check nhanh */
  get snapshot(): AppUser | null {
    return this._user$.value;
  }
}
