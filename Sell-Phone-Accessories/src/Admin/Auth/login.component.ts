import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import {
  FormGroup,
  FormControl,
  Validators,
  AbstractControl,
  ValidationErrors,
  ReactiveFormsModule,
  ValidatorFn,
} from '@angular/forms';

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
      //kiểm tra có lỗi validate ở bất kỳ control nào không.
      this.form.markAllAsTouched(); //đánh dấu tất cả control là “touched” để hiển thị lỗi (<mat-error>) ngay cả khi người dùng chưa bấm vào từng ô.
      return;
    }

    const { email, password } = this.form.getRawValue(); // typed

    const payload = { email, password };
    console.log('LOGIN payload', payload);
    // TODO: this.authService.login(payload).subscribe(...)
  }
}
