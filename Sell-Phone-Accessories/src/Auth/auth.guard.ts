// chặn khi không phải admin
import { inject } from '@angular/core';
import { CanMatchFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { map } from 'rxjs';

export const authGuard: CanMatchFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.isLoggedIn().pipe(map(ok => ok ? true : router.createUrlTree(['/signin'])));
};

// admin.guard.ts
export const adminGuard: CanMatchFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.hasRole('admin').pipe(map(ok => ok ? true : router.createUrlTree(['/403'])));
};
