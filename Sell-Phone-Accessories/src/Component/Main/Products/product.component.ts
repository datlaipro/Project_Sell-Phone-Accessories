import { Component } from '@angular/core';
import { NavbarComponent } from '../../Header/Navbar/navbar.component';
import { SidebarComponent } from '../../Header/Sidebar/sidebar.component';

@Component({
  standalone: true,
  selector: 'product',
  imports: [NavbarComponent, SidebarComponent],
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.css'],
})
export class Products {}
