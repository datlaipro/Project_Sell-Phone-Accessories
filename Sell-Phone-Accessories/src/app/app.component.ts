import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from '../service/auth.service';
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `<router-outlet></router-outlet>`,
})
export class AppComponent implements OnInit {
  constructor(private auth: AuthService) {}

  ngOnInit() {
    this.auth.restore().subscribe(); // kích hoạt 1 lần khi app lên
  }
}
