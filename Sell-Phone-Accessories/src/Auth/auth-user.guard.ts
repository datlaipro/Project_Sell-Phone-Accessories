// src/app/core/auth/auth-user.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthUserService } from './auth-user.service';
import { catchError, map, of } from 'rxjs';

export const authUserGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthUserService);
  const router = inject(Router);

  return auth.ensureMe().pipe(
    map((ok) => {
      if (ok) return true;
      router.navigate(['/auth'], { queryParams: { redirect: state.url } });
      return false;
    }),
    catchError(() => {
      router.navigate(['/auth'], { queryParams: { redirect: state.url } }); //Nếu gọi /auth/me lỗi (mạng, server, 401/403…), guard không ném lỗi ra router mà chuyển người dùng về /login và trả false để chặn route.
      return of(false);
    })
  );
};
