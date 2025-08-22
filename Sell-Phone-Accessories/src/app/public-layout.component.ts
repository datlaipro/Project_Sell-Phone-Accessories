import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../Component/Header/Navbar/navbar.component';
import { SidebarComponent } from '../Component/Header/Sidebar/sidebar.component';
import { Footer } from '../Component/Footer/footer.Component';

@Component({
  selector: 'public-layout',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, SidebarComponent, Footer],
  templateUrl: './public-layout.component.html',
})
export class PublicLayoutComponent {}
