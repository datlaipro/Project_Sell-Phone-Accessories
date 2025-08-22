import { Routes } from '@angular/router';
import { PublicLayoutComponent } from './public-layout.component';
import { Products } from '../Component/Main/Products/product.component';
import { HomeComponent } from './home.component';
import { authGuard } from '../Auth/auth.guard'; 
import { adminGuard } from '../Auth/auth.guard'; 
import { AdminDashboardComponent } from '../Admin/admin.component';


export const routes: Routes = [
  // Khu vực public (dùng chung Navbar/Sidebar/Footer)
  {
    path: '',
    component: PublicLayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'product', component: Products },
      // thêm các route public khác nếu cần
    ],
  },

  // Khu vực admin (layout riêng, KHÔNG dùng layout public)
  {
    path: 'admin',
    component: AdminDashboardComponent, // <-- khung admin (sidenav + topbar)
    // canMatch: [authGuard, adminGuard],         
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      // { path: 'products', loadComponent: ... }  // thêm trang admin khác ở đây
    ],
  },



  { path: '**', redirectTo: '' },
];
