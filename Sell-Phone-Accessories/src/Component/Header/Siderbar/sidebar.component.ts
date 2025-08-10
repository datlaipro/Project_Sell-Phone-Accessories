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

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule, MatToolbarModule, MatButtonModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
})
export class SidebarComponent {
  @ViewChildren('dd') dropdowns!: QueryList<ElementRef<HTMLDetailsElement>>;// lấy tất cả các phần tử có #dd và lấy đúng loại
  //html thì lấy được các thuộc tính của thẻ đó 

  @HostListener('document:click', ['$event'])
  onDocClick(e: MouseEvent) {//lấy chỗ vừa được click rồi gán vào target nếu chỗ click không phải vào thẻ details thì sẽ kích hoạt 
    //         el.open = false; // đóng mọi dropdown không chứa click

    const target = e.target as Node;

    this.dropdowns?.forEach((ref) => {//duyệt qua tất cả thẻ details 
      const el = ref.nativeElement;//thẻ details hiện tại 
      const clickedInside = el.contains(target);
      if (!clickedInside && el.open) {
        el.open = false; // đóng mọi dropdown không chứa click
      }
    });
  }

  @HostListener('document:keydown.escape')
  onEsc() {
    this.dropdowns?.forEach((ref) => (ref.nativeElement.open = false));
  }
}
