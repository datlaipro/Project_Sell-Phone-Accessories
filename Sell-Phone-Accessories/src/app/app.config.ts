import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withFetch, withInterceptorsFromDi } from '@angular/common/http';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
      provideHttpClient(
      withFetch(),                 // tùy chọn: dùng fetch thay vì XHR
      withInterceptorsFromDi()     // nếu bạn đang dùng HttpInterceptors cũ qua DI
    ),
  ],
};
