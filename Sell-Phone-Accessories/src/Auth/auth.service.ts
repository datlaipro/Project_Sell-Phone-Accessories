// kiểm tra xem có phải admin truy cập không 
import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, of, tap } from 'rxjs';

export type User = { id: string; email: string; role: 'admin'|'staff'|'user' };

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private cached: User | null = null;

  me() {
    if (this.cached) return of(this.cached);
    return this.http.get<User>('/api/auth/me', { withCredentials: true })
      .pipe(tap(u => this.cached = u));
  }



  isLoggedIn() { return this.me().pipe(map(u => !!u)); }
  hasRole(role: 'admin'|'staff'|'user') {
    return this.me().pipe(map(u => !!u && (u.role === role || u.role === 'admin')));
  }
}
