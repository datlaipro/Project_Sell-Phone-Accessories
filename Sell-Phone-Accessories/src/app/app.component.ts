import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../Component/Header/Navbar/navbar.component';
import { SidebarComponent } from '../Component/Header/Sidebar/sidebar.component'; // <- sửa Siderbar -> Sidebar
import { Footer } from '../Component/Footer/footer.Component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, SidebarComponent, Footer],
  template: `
    <app-navbar></app-navbar>
    <app-sidebar></app-sidebar>

    <!-- Trang con sẽ render vào đây -->
    <router-outlet></router-outlet>

    <app-footer></app-footer>
  `,
})
export class AppComponent {}
