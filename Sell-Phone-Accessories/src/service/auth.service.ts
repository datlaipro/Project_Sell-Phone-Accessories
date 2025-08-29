// auth.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, of } from 'rxjs';
import { tap, catchError, map, shareReplay } from 'rxjs/operators';
import { switchMap, throwError } from 'rxjs';
import { environment } from '../environments/environment.development';

export type User = { id: number; email: string; name: string; role: string };

@Injectable({ providedIn: 'root' })
export class AuthService {
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
      .get<User>(`${this.apiBase}/auth/me`, { withCredentials: true })
      .pipe(
        // nếu access hết hạn, dùng refresh để lấy access mới rồi gọi lại /auth/me
        catchError((err) => {
          if (err.status === 403) {
            return this.http
              .post<void>(`${this.apiBase}/auth/refresh`, null, {
                withCredentials: true,
              })
              .pipe(
                switchMap(() =>
                  this.http.get<User>(`${this.apiBase}/auth/me`, {
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

  setUser(u: User | null) {
    this.userSubject.next(u);
  }
  get snapshot() {
    return this.userSubject.value;
  }
}
