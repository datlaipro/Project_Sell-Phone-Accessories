import {
  Component,
  ViewChildren,
  QueryList,
  ElementRef,
  HostListener,
} from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { NgFor } from '@angular/common';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule, MatToolbarModule, MatButtonModule,NgFor],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
})
export class SidebarComponent {
  @ViewChildren('dd') dropdowns!: QueryList<ElementRef<HTMLDetailsElement>>; // lấy tất cả các phần tử có #dd và lấy đúng loại
  //html thì lấy được các thuộc tính của thẻ đó

  @HostListener('document:click', ['$event'])
  onDocClick(e: MouseEvent) {
    //lấy chỗ vừa được click rồi gán vào target nếu chỗ click không phải vào thẻ details thì sẽ kích hoạt
    //         el.open = false; // đóng mọi dropdown không chứa click

    const target = e.target as Node;

    this.dropdowns?.forEach((ref) => {
      //duyệt qua tất cả thẻ details
      const el = ref.nativeElement; //thẻ details hiện tại
      const clickedInside = el.contains(target);
      if (!clickedInside && el.open) {
        el.open = false; // đóng mọi dropdown không chứa click
      }
    });
  }
  layId(id: number) {
    console.log('ID bạn vừa click là:', id);
  }
  notication() {
    alert('bạn đã bấm nút');
  }
  products = [
    {
      title: 'Apple Iphone',
      product: [
        { id: 1, name: 'iPhone 16 Pro Max' },
        { id: 2, name: 'iPhone 16 Pro ' },
        { id: 3, name: 'iPhone 16 Plus' },
        { id: 4, name: 'iPhone 16e' },
      ],
    },
    {
      title: 'Apple Ipad',
      product: [
        { id: 5, name: 'IPad(A16)' },
        { id: 6, name: 'iPad Air 11-inch (M3)' },
        { id: 7, name: 'iPad Air 13-inch (M3)' },
        { id: 8, name: 'iPad mini (A17 Pro)' },
      ],
    },
    {
      title: 'SamSung',
      product: [
        { id: 9, name: 'Galaxy S25 Ultra' },
        { id: 10, name: 'Galaxy S25' },
        { id: 11, name: 'Galaxy S25+' },
        { id: 12, name: 'Galaxy S25 Edge' },
      ],
    },

    
  ];

  @HostListener('document:keydown.escape')
  onEsc() {
    this.dropdowns?.forEach((ref) => (ref.nativeElement.open = false));
  }
}
