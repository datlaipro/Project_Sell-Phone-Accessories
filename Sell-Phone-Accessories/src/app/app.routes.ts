import { Routes } from '@angular/router';
import { PublicLayoutComponent } from './public-layout.component';
import { Products } from '../Component/Main/Products/product.component';
import { HomeComponent } from './home.component';
import { authGuard } from '../Auth/auth.guard';
import { adminGuard } from '../Auth/auth.guard';
import { AuthComponent } from '../LoginAndRegister/LoginAndRegister.component';
import { ProductFormComponent } from '../Admin/AddNewProducts/addNewProducts.component';
import { DashboardComponent } from '../Admin/Dashboard/dashboard.component';
import { Login } from '../Admin/Auth/login.component';
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
  // {path:'addProduct',component:ProductFormComponent},
  { path: 'login', component: Login },
  // Khu vực admin (layout riêng, KHÔNG dùng layout public)
  {
    path: 'admin',
    loadComponent: () =>
      import('../Admin/admin.component').then((m) => m.AdminComponent),
    //  canMatch: [ authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'addProduct', component: ProductFormComponent },

    ],
  },

  { path: '**', redirectTo: '' },
];
