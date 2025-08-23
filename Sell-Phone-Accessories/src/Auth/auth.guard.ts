// chặn khi không phải admin
import { inject } from '@angular/core';
import { CanMatchFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { map, tap,take } from 'rxjs';

export const authGuard: CanMatchFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
 return auth.isLoggedIn().pipe(
    take(1),
    tap(ok => console.log('[authGuard] isLoggedIn=', ok)),
    map(ok => ok ? true : router.createUrlTree(['/auth'])),
    tap(result => console.log('[authGuard] decision=', result))
  );};

// admin.guard.ts
export const adminGuard: CanMatchFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.hasRole('admin').pipe(
    take(1),
    tap(ok => console.log('[adminGuard] hasRole(admin)=', ok)), // ⟵ trước map
    map(ok => ok ? true : router.createUrlTree(['/'])),
    tap(result => console.log('[adminGuard] decision=', result)) // ⟵ sau map
  );};
