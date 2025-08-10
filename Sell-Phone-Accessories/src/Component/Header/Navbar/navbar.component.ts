import { Component, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatBadgeModule } from '@angular/material/badge';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatBadgeModule,
  ],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent {
  search = new FormControl('');// khởi tạo biến lưu giá trị tìm kiếm tham chiếu tới [formControl]="search" để thực hiện thay đổi 
  // dữ liệu 2 chiều
  cartCount = 2; // demo

  @ViewChild('searchBox') searchBox!: ElementRef<HTMLInputElement>;// lấy giá trị từ input tìm kiếm 

  focusSearch() {
    this.searchBox?.nativeElement?.focus();//nativeElement là thuộc tính của ElementRef để truy cập dom thật
  }

  onSearch() {
    const q = (this.search.value || '').trim();
    if (!q) return;
    console.log('🔎 Searching:', q);
  }

  onCartClick() {
    console.log('🛒 Mở giỏ hàng');
  }
}
