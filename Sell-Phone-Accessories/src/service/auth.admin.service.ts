// src/app/service/auth-admin.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, shareReplay, tap } from 'rxjs/operators';
import { environment } from '../environments/environment.development';
import { SKIP_REFRESH } from '../app/auto-refresh.interceptor';

export type User = { id: number; email: string; name: string; role: string; avatar: string };

@Injectable({ providedIn: 'root' })
export class AuthAdminService {
  apiBase = environment.apiUrl;
  private http = inject(HttpClient);
  private userSubject = new BehaviorSubject<User | null>(null);

  currentUser$ = this.userSubject.asObservable();
  isLoggedIn$ = this.currentUser$.pipe(map(Boolean), shareReplay(1));

  currentUser() { return this.currentUser$; }
  isLoggedIn() { return this.isLoggedIn$; }

  restore() {
    return this.http.get<User>(`${this.apiBase}/admin/me`).pipe(
      tap(u => this.userSubject.next(u)),
      catchError(() => { this.userSubject.next(null); return of(null); })
    );
  }

  logout(): Observable<void> {
    return this.http.post<void>(
      `${this.apiBase}/admin/logout`,
      null,
      { context: new HttpContext().set(SKIP_REFRESH, true) } // không auto-refresh cho logout
    ).pipe(
      tap(() => this.userSubject.next(null)),
      catchError(() => { this.userSubject.next(null); return of(void 0); })
    );
  }

  setUser(u: User | null) { this.userSubject.next(u); }
  get snapshot() { return this.userSubject.value; }
}
