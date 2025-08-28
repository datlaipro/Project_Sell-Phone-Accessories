// auth.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, of } from 'rxjs';
import { tap, catchError, map, shareReplay } from 'rxjs/operators';

export type User = { id:number; email:string; name:string; role:string };

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private userSubject = new BehaviorSubject<User|null>(null);

  // streams “chuẩn”
  currentUser$ = this.userSubject.asObservable();
  isLoggedIn$  = this.currentUser$.pipe(map(Boolean), shareReplay(1));

  // alias để hợp với template bạn đang dùng
  currentUser() { return this.currentUser$; }
  isLoggedIn()  { return this.isLoggedIn$; }

  restore() {
    return this.http.get<User>('/auth/me', { withCredentials: true }).pipe(
      tap(u => this.userSubject.next(u)),
      catchError(() => { this.userSubject.next(null); return of(null); })
    );
  }

  setUser(u: User|null) { this.userSubject.next(u); }
  get snapshot() { return this.userSubject.value; }
}
