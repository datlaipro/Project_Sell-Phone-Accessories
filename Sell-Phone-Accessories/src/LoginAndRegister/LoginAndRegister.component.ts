import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
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

type Mode = 'login' | 'register';

// 👇 Kiểu typed cho FormGroup
type AuthControls = {
  fullName: FormControl<string>;
  email: FormControl<string>;
  password: FormControl<string>;
  confirmPassword: FormControl<string>;
  agree: FormControl<boolean>;
};

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
  mode: Mode = 'login';// mặc định ban đầu giao diện sẽ ở login

  // 👇 FormGroup đã typed
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
    { validators: authPasswordsMatchWhenRegister(() => this.mode) } // validator mức form
  );

  hidePass = true;
  hideConfirm = true;

  constructor() {// khi component được khởi tạo thì gọi hàm validate
    this.updateValidatorsForMode();
  }

  switchMode(mode: Mode): void {
    if (this.mode !== mode) {// 1) Tránh chạy lại nếu chọn đúng mode hiện tại
      this.mode = mode; // 2) Cập nhật trạng thái (login | register)
      this.updateValidatorsForMode();//3) Gắn/gỡ validator theo mode mới
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

    // re-check validator mức FormGroup
    this.form.updateValueAndValidity();
  }

  get errorPasswordMismatch(): boolean {
    return (
      this.form.hasError('passwordMismatch') &&
      this.form.controls.confirmPassword.touched
    );
  }

  onSubmit(): void {
    if (this.form.invalid) {//kiểm tra có lỗi validate ở bất kỳ control nào không.
      this.form.markAllAsTouched();//đánh dấu tất cả control là “touched” để hiển thị lỗi (<mat-error>) ngay cả khi người dùng chưa bấm vào từng ô.
      return;
    }

    const { fullName, email, password } = this.form.getRawValue(); // typed

    if (this.mode === 'login') {
      const payload = { email, password };
      console.log('LOGIN payload', payload);
      // TODO: this.authService.login(payload).subscribe(...)
    } else {
      const payload = { fullName, email, password };
      console.log('REGISTER payload', payload);
      // TODO: this.authService.register(payload).subscribe(...)
    }
  }
}

/**
 * Validator mức FormGroup: chỉ check khi ở chế độ "register".
 * Dùng factory để đọc được mode hiện tại mà không cần bind(this).
 */
function authPasswordsMatchWhenRegister(getMode: () => Mode): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    if (getMode() !== 'register') return null;
    const pass = group.get('password')?.value as string | undefined;
    const cfm = group.get('confirmPassword')?.value as string | undefined;
    if (pass && cfm && pass !== cfm) {
      return { passwordMismatch: true };
    }
    return null;
  };
}
