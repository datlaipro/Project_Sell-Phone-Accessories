import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

type Sale = { img: string; name: string; price: number | string };
type Best = { title: string; cat: string; pct: number };
type Note = { icon: string; text: string; sub: string };

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent {
  @Input() sales: Sale[] = [
    { img: 'IMG', name: 'Bamboo Watch', price: 65 },
    { img: 'IMG', name: 'Black Watch', price: 72 },
    { img: 'IMG', name: 'Blue Band', price: 79 },
    { img: 'IMG', name: 'Blue T-Shirt', price: 29 },
  ];

  @Input() best: Best[] = [
    { title: 'Defender Series', cat: 'iPhone 16 Pro', pct: 78 },
    { title: 'Commuter Series', cat: 'Galaxy S25', pct: 62 },
    { title: 'Symmetry Series', cat: 'iPhone 15', pct: 54 },
  ];

  @Input() notes: Note[] = [
    { icon: 'check_circle', text: 'Backup completed', sub: '2 hours ago' },
    { icon: 'payment', text: 'New payout received', sub: 'Today, 10:15' },
    { icon: 'warning', text: 'Low stock alert', sub: '7 items left' },
  ];
}
