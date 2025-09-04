// src/app/app.config.ts
import { ApplicationConfig, provideAppInitializer, inject } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { firstValueFrom, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { routes } from './app.routes';

// Interceptors
import { withCredsInterceptor } from './with-creds.interceptor';
import { autoRefreshInterceptor } from './auto-refresh.interceptor';

// Services
import { AuthService } from '../service/auth.service';
import { AuthAdminService } from '../service/auth.admin.service';

function isUserAuthPage(path: string) {
  // tuỳ router của bạn: thêm /register nếu cần
  return path.startsWith('/auth');
}

function isAdminAuthPage(path: string) {
  // tuỳ router của bạn: thêm /admin/register nếu cần
  return path.startsWith('/admin/auth') || path.startsWith('/admin/login');
}

function isInAdminArea(path: string) {
  // /admin hoặc bất kỳ route con của admin
  return path === '/admin' || path.startsWith('/admin/');
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),

    // Đăng ký interceptor theo thứ tự:
    // 1) withCreds: tự gắn withCredentials cho request đến API của bạn
    // 2) autoRefresh: tự refresh access token + retry request khi hết hạn
    provideHttpClient(
      withInterceptors([
        withCredsInterceptor,
        autoRefreshInterceptor,
      ])
    ),

    // Khôi phục phiên đăng nhập khi app khởi động
    provideAppInitializer(() => {
      const p = (typeof window !== 'undefined' ? window.location.pathname : '/').toLowerCase();

      // Bỏ qua các trang auth để tránh request thừa
      if (isUserAuthPage(p) || isAdminAuthPage(p)) return;

      // Ở khu vực admin -> dùng AuthAdminService
      if (isInAdminArea(p)) {
        return firstValueFrom(
          inject(AuthAdminService).restore().pipe(catchError(() => of(null)))
        );
      }

      // Còn lại -> dùng AuthService (user thường)
      return firstValueFrom(
        inject(AuthService).restore().pipe(catchError(() => of(null)))
      );
    }),
  ],
};
