import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../Component/Header/Navbar/navbar.component'
import { SidebarComponent } from '../Component/Header/Siderbar/sidebar.component';
// đây là file giống với app.js bên reactjs 
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent,SidebarComponent],
  template: `
    <app-navbar></app-navbar>
    <app-sidebar></app-sidebar>
  `,
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Sell-Phone-Accessories';
}
