import { Routes } from '@angular/router';
import { PublicLayoutComponent } from './public-layout.component';
import { Products } from '../Component/Main/Products/product.component';
import { ProductsDetail } from '../Component/Main/Products/ProductsDetail/productsDetail.component'; // import component chi tiết sản phẩm
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
    component: PublicLayoutComponent, // Đảm bảo layout chứa router-outlet
    children: [
      { path: '', component: HomeComponent },

      // Router cha 'product'
      {
        path: 'product',
        children: [
          { path: '', component: Products }, // /product -> danh sách
          { path: 'productDetail/:id', component: ProductsDetail }, // /product/productDetail/:id -> chi tiết
        ],
      },
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
    canMatch: [authGuard, adminGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'addProduct', component: ProductFormComponent },
    ],
  },

  { path: '**', redirectTo: '' },
];
