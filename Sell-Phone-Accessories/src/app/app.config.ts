import { ApplicationConfig, APP_INITIALIZER, inject } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { routes } from './app.routes';
import { AuthService } from '../service/auth.service';
import { withCredsInterceptor } from './with-creds.interceptor';
import { provideAppInitializer } from '@angular/core';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([withCredsInterceptor])),

    // Thay cho APP_INITIALIZER
    provideAppInitializer(() => {
      // (tuỳ chọn) tránh gọi ở trang /auth để đỡ request thừa lúc chưa đăng nhập:
      const onAuthPage = window.location.pathname.startsWith('/auth');
      if (onAuthPage) return;
      return firstValueFrom(inject(AuthService).restore());
    }),
  ],
};
