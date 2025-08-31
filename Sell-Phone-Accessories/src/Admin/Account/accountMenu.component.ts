import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OverlayModule, ConnectedPosition } from '@angular/cdk/overlay';

@Component({
  selector: 'app-account-menu',
  standalone: true,
  imports: [CommonModule, OverlayModule],
  templateUrl: './accountMenu.component.html',
  styleUrls: ['./accountMenu.component.css'],
})
export class AccountMenuComponent {
  @Input() name = 'Mark Wood';
  @Input() email = 'markwood@gmail.com';
  @Input() avatar = 'https://hoanghamobile.com/tin-tuc/wp-content/uploads/2023/08/hinh-nen-anime-cute-1.jpg';
  @Output() signOut = new EventEmitter<void>();

  open = false;

  // Vị trí ưu tiên: cạnh phải icon, bung xuống; fallback bung lên
  positions: ConnectedPosition[] = [
    { originX: 'end', originY: 'bottom', overlayX: 'end', overlayY: 'top', offsetY: 8 },
    { originX: 'end', originY: 'top',    overlayX: 'end', overlayY: 'bottom', offsetY: -8 },
  ];

  toggle() { this.open = !this.open; }
  close()  { this.open = false; }
  onSignOut(){ this.signOut.emit(); this.close(); }
}
