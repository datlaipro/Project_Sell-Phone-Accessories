import { Routes } from '@angular/router';
import { PublicLayoutComponent } from './public-layout.component';
import { Products } from '../Component/Main/Products/product.component';
import { HomeComponent } from './home.component';
import { authGuard } from '../Auth/auth.guard';
import { adminGuard } from '../Auth/auth.guard';
import { AdminDashboardComponent } from '../Admin/admin.component';
import { AuthComponent } from '../LoginAndRegister/LoginAndRegister.component';

export const routes: Routes = [
  // Khu vực public (dùng chung Navbar/Sidebar/Footer)
  {
    path: '',
    component: PublicLayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'product', component: Products },
      // {path:'login&&register',component:AuthComponent}
      // thêm các route public khác nếu cần
    ],
  },
  { path: 'auth', component: AuthComponent },

  // Khu vực admin (layout riêng, KHÔNG dùng layout public)
  {
    path: 'admin',
    component: AdminDashboardComponent, // <-- khung admin (sidenav + topbar)
    // canMatch: [ adminGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      // { path: 'products', loadComponent: ... }  // thêm trang admin khác ở đây
    ],
  },

  { path: '**', redirectTo: '' },
];
