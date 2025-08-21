import { Component,ViewEncapsulation } from '@angular/core';
import { CommonModule, NgFor } from '@angular/common';
import { RouterLink } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatExpansionModule } from '@angular/material/expansion';
@Component({
  selector: 'ads',
  standalone: true,
  imports:[ CommonModule, NgFor, RouterLink,
    MatButtonModule, MatMenuModule, MatIconModule,
    MatSidenavModule, MatListModule, MatExpansionModule,],
  templateUrl: './ads.component.html',
  styleUrls: ['./ads.component.css'],
})
export class ADS {}
