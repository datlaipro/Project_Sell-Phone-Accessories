// kiểm tra xem có phải admin truy cập không 
import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, of, tap,catchError } from 'rxjs';
import { environment } from '../environments/environment.development'; 

export type User = { id: string; email: string; role: 'superadmin' };

type MaybeUser = User | null;

@Injectable({ providedIn: 'root' })
export class AuthService {
    apiBase = environment.apiUrl;

  private http = inject(HttpClient);
  private cached: MaybeUser = null;// lưu thông tin user đã lấy được 

  me() {
    if (this.cached) return of(this.cached);
    return this.http.get<User>(`${this.apiBase}/admin/me`, { withCredentials: true }).pipe(
      tap(u => this.cached = u),// lưu user khi gọi api xong
      catchError(err => {
        this.cached = null;
        return of<MaybeUser>(null);
      })
    );
  }

  isLoggedIn() { return this.me().pipe(map(u => !!u)); }

  hasRole(role: 'superadmin') {// kiểm tra xem user này có phải admin không 
    return this.me().pipe(map(u => !!u && (u.role === role || u.role === 'superadmin')));
  }
}
