import { Component, signal,inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatTabsModule } from '@angular/material/tabs';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';

type ImgItem = { file?: File; url: string };

@Component({
  selector: 'app-add-product',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    MatCardModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatInputModule, MatSelectModule,
    MatChipsModule, MatTabsModule, MatSlideToggleModule,
    MatListModule, MatDividerModule,
  ],
  templateUrl: './addNewProducts.component.html',
  styleUrls: ['./addNewProducts.component.css'],
})
export class ProductFormComponent {

  private fb = inject(FormBuilder);

  form = this.fb.group({
    name: ['', Validators.required],
    brand: ['', Validators.required],
    category: ['', Validators.required],
      quantity: [1, [
    Validators.required,
    Validators.min(1),
    Validators.pattern(/^\d+$/)  // chỉ cho số nguyên dương
  ]],
    price: [''],
    discount: [''],
    description: [''],
    visible: [true],
  });
  // gallery state
  images = signal<ImgItem[]>([]);
  coverIndex = signal(0);

  // color state
  presetColors = ['#000000', '#ffffff', '#ff4d4f', '#faad14', '#52c41a', '#1677ff', '#722ed1', '#ff6b6b', '#f59e0b'];
  selectedColors = signal<string[]>([]);
  brands = ['Rare Beauty', 'Fenty Beauty', 'L\'Oréal', 'Maybelline', 'NYX', 'Clinique'];

  // form


  // ===== Images =====
  onFilesSelected(ev: Event) {
    const input = ev.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const picked: ImgItem[] = [];
    Array.from(input.files).forEach(file => {
      const url = URL.createObjectURL(file);
      picked.push({ file, url });
    });
    // append to gallery
    const current = this.images();
    this.images.set([...current, ...picked]);

    // set cover to first if empty
    if (current.length === 0) this.coverIndex.set(0);

    // reset input so same files can be re-picked later
    input.value = '';
  }

  removeImage(idx: number) {
    const arr = [...this.images()];
    if (idx < 0 || idx >= arr.length) return;
    URL.revokeObjectURL(arr[idx].url);
    arr.splice(idx, 1);
    this.images.set(arr);
    // adjust cover index
    if (arr.length === 0) this.coverIndex.set(0);
    else if (this.coverIndex() >= arr.length) this.coverIndex.set(arr.length - 1);
  }

  setCover(idx: number) {
    if (idx >= 0 && idx < this.images().length) this.coverIndex.set(idx);
  }

  // ===== Colors =====
  toggleColor(hex: string) {
    const set = new Set(this.selectedColors());
    if (set.has(hex)) set.delete(hex);
    else set.add(hex);
    this.selectedColors.set([...set]);
  }

  addCustomColor(ev: Event) {
    const input = ev.target as HTMLInputElement;
    const val = input.value;
    if (val) {
      const set = new Set(this.selectedColors());
      set.add(val);
      this.selectedColors.set([...set]);
    }
  }

  isColorSelected(hex: string) {
    return this.selectedColors().includes(hex);
  }

  // ===== Submit demo =====
  saveDraft() {
    // handle submit; you can map images() and selectedColors() to payload here
    console.log('Draft data:', this.form.value, {
      colors: this.selectedColors(),
      images: this.images().length,
      coverIndex: this.coverIndex(),
    });
  }

  publish() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    console.log('Publish!', this.form.value);
  }
}
