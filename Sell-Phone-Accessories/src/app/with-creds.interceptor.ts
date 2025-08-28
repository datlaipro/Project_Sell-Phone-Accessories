import { HttpInterceptorFn } from '@angular/common/http';

export const withCredsInterceptor: HttpInterceptorFn = (req, next) =>
  next(req.clone({ withCredentials: true }));
