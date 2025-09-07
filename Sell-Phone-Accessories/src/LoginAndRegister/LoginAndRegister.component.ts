import { Component, inject } from '@angular/core';
import { RouterLink, Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StateLogin } from '../service/stateLogin.service';
import {
  FormGroup,
  FormControl,
  Validators,
  AbstractControl,
  ValidationErrors,
  ReactiveFormsModule,
  ValidatorFn,
} from '@angular/forms';

// Angular Material
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../service/auth.service';

// 👇 Import env
import { environment } from '../environments/environment.development';
type Mode = 'login' | 'register';

// 👇 Kiểu typed cho FormGroup
type AuthControls = {
  fullName: FormControl<string>;
  email: FormControl<string>;
  password: FormControl<string>;
  confirmPassword: FormControl<string>;
  agree: FormControl<boolean>;
};
type Res = { id: number; email: string; name: string; role: 'user' | 'admin' };

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    // Material
    MatButtonToggleModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatCheckboxModule,
    MatButtonModule,
    MatCardModule,
    RouterLink,
  ],
  templateUrl: './LoginAndRegister.component.html',
  styleUrls: ['./LoginAndRegister.component.css'],
})
export class AuthComponent {
  auth = inject(AuthService); // <-- expose service ra template

  bus = inject(StateLogin); // state báo trạng thái đăng nhập ra ngoài
  mode: Mode = 'login'; // mặc định ban đầu giao diện sẽ ở login
  apiBase = environment.apiUrl;
  loading = false;
  private router = inject(Router);
  // 👇 inject HttpClient trực tiếp
  private http = inject(HttpClient);

  form: FormGroup<AuthControls> = new FormGroup<AuthControls>(
    {
      fullName: new FormControl('', { nonNullable: true }),
      email: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required, Validators.email],
      }),
      password: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required, Validators.minLength(6)],
      }),
      confirmPassword: new FormControl('', { nonNullable: true }),
      agree: new FormControl(false, { nonNullable: true }),
    },
    { validators: authPasswordsMatchWhenRegister(() => this.mode) }
  );

  hidePass = true;
  hideConfirm = true;

  constructor() {
    // khi component được khởi tạo thì gọi hàm validate
    this.updateValidatorsForMode();
  }

  switchMode(mode: Mode): void {
    if (this.mode !== mode) {
      // 1) Tránh chạy lại nếu chọn đúng mode hiện tại
      this.mode = mode; // 2) Cập nhật trạng thái (login | register)
      this.updateValidatorsForMode(); //3) Gắn/gỡ validator theo mode mới
    }
  }

  private updateValidatorsForMode(): void {
    const { fullName, confirmPassword, agree } = this.form.controls;
    if (this.mode === 'register') {
      fullName.setValidators([Validators.required, Validators.minLength(2)]);
      confirmPassword.setValidators([Validators.required]);
      agree.setValidators([Validators.requiredTrue]);
    } else {
      fullName.clearValidators();
      confirmPassword.clearValidators();
      agree.clearValidators();
    }
    fullName.updateValueAndValidity();
    confirmPassword.updateValueAndValidity();
    agree.updateValueAndValidity();
    this.form.updateValueAndValidity();
  }

  get errorPasswordMismatch(): boolean {
    return (
      this.form.hasError('passwordMismatch') &&
      this.form.controls.confirmPassword.touched
    );
  }
  login() {
    //điều hướng về trang chủ sau khi login xong
    this.router.navigateByUrl('/', {
      state: { next: this.router.url },
    });
  }
  onSubmit(): void {
    if (this.loading) return;
    if (this.form.invalid) {
      this.form.markAllAsTouched(); //đánh dấu tất cả control là “touched” để hiển thị lỗi (<mat-error>) ngay cả khi người dùng chưa bấm vào từng ô.
      return;
    }
    const { fullName, email, password } = this.form.getRawValue();
    this.loading = true;

    if (this.mode === 'login') {
      const payload = {
        email: email.trim().toLowerCase(),
        password: password,
      };
      this.http
        .post<Res>(`${this.apiBase}/auth/login`, payload, {
          withCredentials: true, // gửi/nhận cookie
        })
        .subscribe({
          next: (user) => {
            console.log('LOGIN ok:', user);

            // ✅ Cập nhật state đăng nhập ngay bằng dữ liệu trả về từ /auth/login
            this.auth.setUser(user);
            this.bus.set({ state: true, userName: user.name });

            // (tuỳ chọn) cache tên để UI đỡ nháy:
            // localStorage.setItem('ui_user', JSON.stringify({ name: user.name, t: Date.now() }));

            this.loading = false;

            // ✅ Điều hướng sau khi login
            this.router.navigate(['/']); // đổi route nếu bạn muốn
          },
          error: (err: HttpErrorResponse) => {
            this.loading = false;
            if (err.status === 401) alert('Email hoặc mật khẩu không đúng');
            else alert('Lỗi hệ thống. Thử lại sau.');
          },
        });
    } else {
      const payload = { fullName, email, password };
      console.log(payload);
      this.http
        .post<Res>(`${this.apiBase}/auth/register`, payload, {
          withCredentials: true, // 👈 nếu backend set cookie (ít gặp khi register)
        })
        .subscribe({
          next: (user) => {
            this.loading = false;
            this.router.navigate(['/']); // đổi route nếu bạn muốn
            this.auth.setUser(user);

            // TODO: điều hướng / thông báo
          },
          error: (err: HttpErrorResponse) => {
            console.error('REGISTER lỗi:', err);
            alert(this.readErr(err));
            this.loading = false;
          },
        });
    }
  }

  private readErr(err: HttpErrorResponse): string {
    // cố gắng lấy message server trả
    const m =
      (err.error && (err.error.message || err.error.error || err.error.msg)) ||
      err.message ||
      'Có lỗi xảy ra';
    return typeof m === 'string' ? m : JSON.stringify(m);
  }
}

/** Validator mức FormGroup */
function authPasswordsMatchWhenRegister(getMode: () => Mode): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    if (getMode() !== 'register') return null;
    const pass = group.get('password')?.value as string | undefined;
    const cfm = group.get('confirmPassword')?.value as string | undefined;
    return pass && cfm && pass !== cfm ? { passwordMismatch: true } : null;
  };
}
function ngOnInit() {
  throw new Error('Function not implemented.');
}
