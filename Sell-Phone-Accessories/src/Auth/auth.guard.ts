// chặn khi không phải admin
import { inject } from '@angular/core';
import { CanMatchFn, Router,UrlSegment  } from '@angular/router';
import { AuthService } from './auth.service';
import { map, tap,take } from 'rxjs';

export const authGuard: CanMatchFn = (_route, segments: UrlSegment[]) => {//chặn khi chưa đăng nhập 
  const auth = inject(AuthService);
  const router = inject(Router);
    // ghép lại URL người dùng đang cố vào
  const attempted = '/' + segments.map(s => s.path).join('/');

 return auth.isLoggedIn().pipe(
    take(1),
    map(ok => ok ? true : router.createUrlTree(['/login'], { queryParams: { redirectUrl: '/admin' } } )),
  );};

// admin.guard.ts
export const adminGuard: CanMatchFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.hasRole('superadmin').pipe(
    take(1),
    map(ok => ok ? true : router.createUrlTree(['/'])),
  );};
