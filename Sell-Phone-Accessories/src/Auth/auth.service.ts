// kiểm tra xem có phải admin truy cập không 
import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, of, tap,catchError } from 'rxjs';

export type User = { id: string; email: string; role: 'admin' };

type MaybeUser = User | null;

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private cached: MaybeUser = null;

  me() {
    if (this.cached) return of(this.cached);
    return this.http.get<User>('/api/auth/me', { withCredentials: true }).pipe(
      tap(u => this.cached = u),
      catchError(err => {
        console.warn('[AuthService] /me failed:', err);
        this.cached = null;
        return of<MaybeUser>(null);
      })
    );
  }

  isLoggedIn() { return this.me().pipe(map(u => !!u)); }

  hasRole(role: 'admin'|'staff'|'user') {
    return this.me().pipe(map(u => !!u && (u.role === role || u.role === 'admin')));
  }
}
