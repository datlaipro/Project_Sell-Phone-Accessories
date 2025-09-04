// src/app/with-creds.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../environments/environment.development';

export const withCredsInterceptor: HttpInterceptorFn = (req, next) => {
  // Chỉ gắn withCredentials cho request hướng về API backend của bạn
  if (req.url.startsWith(environment.apiUrl)) {
    req = req.clone({ withCredentials: true });
  }
  return next(req);
};
