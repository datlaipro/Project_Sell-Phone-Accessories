// src/app/auto-refresh.interceptor.ts
import {
  HttpInterceptorFn,
  HttpErrorResponse,
  HttpClient,
  HttpContextToken
} from '@angular/common/http';
import { inject } from '@angular/core';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, filter, finalize, shareReplay, switchMap, take } from 'rxjs/operators';
import { environment } from '../environments/environment.development';
import { AuthAdminService } from '../service/auth.admin.service'; 

// Cho phép skip refresh cho 1 request nào đó (vd: logout)
export const SKIP_REFRESH = new HttpContextToken<boolean>(() => false);

// Biến trạng thái refresh dùng chung
const refreshing$ = new BehaviorSubject<boolean>(false);
let refreshOnce$: Observable<void> | null = null;

function shouldAutoRefresh(url: string, skip: boolean): boolean {
  if (skip) return false;
  if (!url.startsWith(environment.apiUrl)) return false;
  // Không refresh cho các endpoint auth nhạy cảm
  if (/\/admin\/(login|refresh|logout)$/.test(url)) return false;
  return true;
}

function startRefresh(http: HttpClient): Observable<void> {
  if (!refreshOnce$) {
    refreshing$.next(true);
    refreshOnce$ = http
      .post<void>(`${environment.apiUrl}/admin/refresh`, null, { withCredentials: true })
      .pipe(
        // các request chờ refresh đều chia sẻ cùng 1 observable
        shareReplay(1),
        finalize(() => {
          refreshing$.next(false);
          refreshOnce$ = null;
        })
      );
  }
  return refreshOnce$;
}

export const autoRefreshInterceptor: HttpInterceptorFn = (req, next) => {
  const http = inject(HttpClient);
  const auth = inject(AuthAdminService);

  const skip = req.context.get(SKIP_REFRESH);

  return next(req).pipe(
    catchError((err: unknown) => {
      const isHttp = err instanceof HttpErrorResponse;
      const status = isHttp ? err.status : 0;
      const tokenExpired = [401, 403, 400].includes(status); // tuỳ BE
      const allow = shouldAutoRefresh(req.url, skip);

      if (!isHttp || !tokenExpired || !allow) {
        return throwError(() => err);
      }

      const retryRequest = () => next(req.clone());

      // Nếu đã có refresh đang diễn ra -> đợi xong rồi retry
      if (refreshing$.value) {
        return refreshing$
          .pipe(
            filter(v => v === false),
            take(1),
            switchMap(() => retryRequest())
          );
      }

      // Bắt đầu 1 lần refresh mới
      return startRefresh(http).pipe(
        switchMap(() => retryRequest()),
        catchError((refreshErr) => {
          // Refresh thất bại -> xoá user local, đẩy lỗi lên cho UI xử lý (vd: chuyển trang /auth)
          auth.setUser(null);
          return throwError(() => refreshErr);
        })
      );
    })
  );
};
