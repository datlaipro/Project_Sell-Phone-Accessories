import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import {
  FormGroup,
  FormControl,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { environment } from '../../environments/environment.development';

type AuthControls = {
  email: FormControl<string>;
  password: FormControl<string>;
};

@Component({
  standalone: true,
  selector: 'app-login',
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class Login {
  private http = inject(HttpClient);
  private router = inject(Router);
  private route  = inject(ActivatedRoute);
  apiBase = environment.apiUrl;
  // (tuỳ chọn) trạng thái loading để disable nút
  loading = false;

  // đích mặc định nếu không có redirectUrl
  private readonly DEFAULT_REDIRECT = '/admin';

  hidePass = true;

  form: FormGroup<AuthControls> = new FormGroup<AuthControls>({
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(6)],
    }),
  });

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { email, password } = this.form.getRawValue();
    const payload = { email, password };

    // lấy redirectUrl từ query string, mặc định về /admin
    const redirect = this.route.snapshot.queryParamMap.get('redirectUrl') || this.DEFAULT_REDIRECT;

    this.loading = true;
    this.http.post(`${this.apiBase}/admin/login`, payload, { withCredentials: true })
      .subscribe({
        next: () => {
          // chặn open-redirect: chỉ cho phép đường dẫn nội bộ bắt đầu bằng '/'
          const safeTarget = redirect.startsWith('/') ? redirect : this.DEFAULT_REDIRECT;
          this.router.navigateByUrl(safeTarget);
        },
        error: (err) => {
          console.warn('[Login] login failed:', err);
          this.loading = false;
          // TODO: hiển thị thông báo lỗi tuỳ ý
        }
      });
  }
}
