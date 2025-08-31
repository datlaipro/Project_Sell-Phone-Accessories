import { Component,inject  } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatBadgeModule } from '@angular/material/badge';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { map, shareReplay } from 'rxjs';
import { RouterOutlet } from '@angular/router';
import { RouterLink ,Router } from '@angular/router';
import { AccountMenuComponent } from './Account/accountMenu.component';
import { AuthAdminService } from '../service/auth.admin.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
    AccountMenuComponent,
    MatSidenavModule, MatToolbarModule, MatIconModule, MatButtonModule,
    MatListModule, MatBadgeModule, MatTooltipModule,RouterOutlet,RouterLink
  ],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css'],
})
export class AdminComponent {
  private bo = inject(BreakpointObserver);
  private auth = inject(AuthAdminService);
  constructor( private router: Router) {}

  isHandset$ = this.bo.observe(Breakpoints.Handset).pipe(
    map(r => r.matches),
    shareReplay({ bufferSize: 1, refCount: true }) // hoặc shareReplay(1)
  );
    currentUser$ = this.auth.currentUser$; // hoặc this.auth.currentUser()

isActive: boolean[] = Array(8).fill(false);

activeHover(index: number): void {
  // Cách 1: tạo mảng mới theo điều kiện index
  this.isActive = this.isActive.map((_, i) => i === index);

  
}

onLogout() {
    // tuỳ service của bạn, ví dụ:
   this.auth.logout().subscribe(() => {
      this.router.navigateByUrl('/'); // hoặc '/login'
    });
  }
  


}