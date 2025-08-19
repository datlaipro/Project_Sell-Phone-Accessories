import { Routes } from '@angular/router';
import { Products } from '../Component/Main/Products/product.component';
import { HomeComponent } from './home.component';
export const routes: Routes = [
  {
    path: 'product',
    component: Products,
  },
  {path:'',component:HomeComponent}
];
