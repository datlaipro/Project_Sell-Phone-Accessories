import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
@Component({
  standalone: true,
  selector: 'app-footer',
  imports: [MatIconModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css'],
})
export class Footer {}
