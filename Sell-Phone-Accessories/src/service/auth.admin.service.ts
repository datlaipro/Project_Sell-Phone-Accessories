// auth.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, of,Observable } from 'rxjs';
import { tap, catchError, map, shareReplay } from 'rxjs/operators';
import { switchMap, throwError } from 'rxjs';
import { environment } from '../environments/environment.development';

export type User = { id: number; email: string; name: string; role: string; avatar: string };

@Injectable({ providedIn: 'root' })
export class AuthAdminService {
  apiBase = environment.apiUrl;

  private http = inject(HttpClient);
  private userSubject = new BehaviorSubject<User | null>(null);

  // streams “chuẩn”
  currentUser$ = this.userSubject.asObservable();
  isLoggedIn$ = this.currentUser$.pipe(map(Boolean), shareReplay(1));

  // alias để hợp với template bạn đang dùng
  currentUser() {
    return this.currentUser$;
  }
  isLoggedIn() {
    return this.isLoggedIn$;
  }

  // auth.service.ts

  restore() {
    return this.http
      .get<User>(`${this.apiBase}/admin/me`, { withCredentials: true })
      .pipe(
        // nếu access hết hạn, dùng refresh để lấy access mới rồi gọi lại /auth/me
        catchError((err) => {
          if (err.status === 403) {
            return this.http
              .post<void>(`${this.apiBase}/admin/refresh`, null, {
                withCredentials: true,
              })
              .pipe(
                switchMap(() =>
                  this.http.get<User>(`${this.apiBase}/admin/me`, {
                    withCredentials: true,
                  })
                )
              );
          }
          return throwError(() => err);
        }),
        tap((u) => this.userSubject.next(u)),
        catchError(() => {
          this.userSubject.next(null);
          return of(null);
        })
      );
  }

  /** POST /admin/logout; tự refresh nếu 403 rồi gọi lại; luôn clear local state */
  logout(): Observable<void> {
    const doLogout$ = () =>
      this.http.post<void>(`${this.apiBase}/admin/logout`, null, {
        withCredentials: true,
      });

    return doLogout$().pipe(
      tap(() => this.userSubject.next(null)),
      catchError((err) => {
        if (err.status === 403) {
          // access hết hạn -> refresh rồi logout lại
          return this.http
            .post<void>(`${this.apiBase}/admin/refresh`, null, {
              withCredentials: true,
            })
            .pipe(
              switchMap(() => doLogout$()),
              tap(() => this.userSubject.next(null)),
              // refresh cũng fail -> vẫn clear local, nuốt lỗi để UI thoát ra
              catchError(() => {
                this.userSubject.next(null);
                return of(void 0);
              })
            );
        }
        // lỗi khác -> vẫn clear local để UI thoát ra
        this.userSubject.next(null);
        return of(void 0);
      })
    );
  }
  setUser(u: User | null) {
    this.userSubject.next(u);
  }
  get snapshot() {
    return this.userSubject.value;
  }
}
