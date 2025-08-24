import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
type Category =
  | 'Cases' | 'Screen Protection' | 'Wearables' | 'The Drop' | 'Power'
  | 'Accessories' | 'Customize' | 'Sale' | 'Explore';

interface ColorSwatch { hex: string; name: string; }
interface DeviceMap { [device: string]: string[]; }
type SubMap = Partial<Record<Category, string[]>>;

@Component({
  selector: 'addNewProducts',
  standalone:true,
  imports:[ReactiveFormsModule,RouterLink],
  templateUrl: './addNewProducts.component.html',
  styleUrls: ['./addNewProducts.component.css']
})
export class ProductFormComponent implements OnInit, OnDestroy {
  form!: FormGroup;
defaultColorSet = new Set<string>();

  // --- Select options ---
  categories: Category[] = [
    'Cases','Screen Protection','Wearables','The Drop','Power',
    'Accessories','Customize','Sale','Explore'
  ];

  subByCategory: SubMap = {
    'Cases': ['iPhone','Samsung Galaxy','Motorola','Google Pixel','iPad','AirPods','Other'],
    'Screen Protection': ['iPhone','Samsung Galaxy','Motorola','Google Pixel','iPad','Other'],
    'Wearables': ['Apple Watch','AirPods','Other'],
    'The Drop': ['Collection','Limited','Other'],
    'Power': ['Power Bank','Wall Charger','Car Charger','Cable','MagSafe','Other'],
    'Accessories': ['Stand','Mount','Grip','Ring','Strap','Other'],
    'Customize': ['Custom Case','Custom Skin'],
    'Sale': [],
    'Explore': []
  };

  // "đầy đủ" phổ biến/đời chính cho 3 hãng — có thể mở rộng tiếp nếu bạn muốn
  modelsByDevice: DeviceMap = {
    // iPhone (6 → 15 Pro Max + SE)
    'iPhone': [
      'iPhone 6','iPhone 6 Plus','iPhone 6s','iPhone 6s Plus',
      'iPhone 7','iPhone 7 Plus','iPhone 8','iPhone 8 Plus',
      'iPhone X','iPhone XR','iPhone XS','iPhone XS Max',
      'iPhone 11','iPhone 11 Pro','iPhone 11 Pro Max',
      'iPhone SE (2016)','iPhone SE (2020)','iPhone SE (2022)',
      'iPhone 12 mini','iPhone 12','iPhone 12 Pro','iPhone 12 Pro Max',
      'iPhone 13 mini','iPhone 13','iPhone 13 Pro','iPhone 13 Pro Max',
      'iPhone 14','iPhone 14 Plus','iPhone 14 Pro','iPhone 14 Pro Max',
      'iPhone 15','iPhone 15 Plus','iPhone 15 Pro','iPhone 15 Pro Max'
    ],
    // Samsung Galaxy (S7 → S24 + Note + Z series tiêu biểu)
    'Samsung Galaxy': [
      'Galaxy S7','Galaxy S7 Edge',
      'Galaxy S8','Galaxy S8+','Galaxy S9','Galaxy S9+',
      'Galaxy S10e','Galaxy S10','Galaxy S10+',
      'Galaxy S20','Galaxy S20+','Galaxy S20 Ultra',
      'Galaxy S21','Galaxy S21+','Galaxy S21 Ultra',
      'Galaxy S22','Galaxy S22+','Galaxy S22 Ultra',
      'Galaxy S23','Galaxy S23+','Galaxy S23 Ultra',
      'Galaxy S24','Galaxy S24+','Galaxy S24 Ultra',
      'Galaxy Note8','Galaxy Note9','Galaxy Note10','Galaxy Note10+',
      'Galaxy Note20','Galaxy Note20 Ultra',
      'Galaxy Z Flip 3','Galaxy Z Flip 4','Galaxy Z Flip 5',
      'Galaxy Z Fold 3','Galaxy Z Fold 4','Galaxy Z Fold 5'
    ],
    // Motorola (G / Edge / Razr / E tiêu biểu gần đây)
    'Motorola': [
      'Moto G5','Moto G5 Plus','Moto G6','Moto G6 Plus','Moto G7','Moto G7 Plus',
      'Moto G8','Moto G8 Plus','Moto G9','Moto G9 Plus','Moto G10','Moto G30','Moto G50',
      'Moto G Power (2020)','Moto G Power (2021)','Moto G Power (2022)','Moto G Power (2023)',
      'Moto G Stylus (2020)','Moto G Stylus (2021)','Moto G Stylus (2022)','Moto G Stylus (2023)','Moto G Stylus (2024)',
      'Moto Edge (2020)','Moto Edge+ (2020)','Motorola Edge 20','Edge 20 Pro',
      'Motorola Edge 30','Edge 30 Pro','Edge 30 Ultra','Edge 40','Edge 40 Pro','Edge 40 Neo',
      'Razr 5G (2020)','Razr (2022)','Razr 40','Razr 40 Ultra / Razr+',
      'Moto E (2020)','Moto E (2023)'
    ],
    // các nhóm khác (mẫu)
    'Google Pixel': ['Pixel 4a','Pixel 5','Pixel 6','Pixel 6 Pro','Pixel 7','Pixel 7 Pro','Pixel 8','Pixel 8 Pro'],
    'iPad': ['iPad 10.2"','iPad Air','iPad Pro 11"','iPad Pro 12.9"','iPad mini'],
    'Apple Watch': ['38/40/41mm','42/44/45/49mm'],
    'AirPods': ['AirPods 3','AirPods Pro','AirPods Pro 2']
  };

  defaultColors: ColorSwatch[] = [
    {hex:'#111111',name:'Đen'},{hex:'#ffffff',name:'Trắng'},{hex:'#ff4757',name:'Đỏ'},
    {hex:'#1e90ff',name:'Xanh dương'},{hex:'#2ed573',name:'Xanh lá'},{hex:'#ffa502',name:'Cam'},
    {hex:'#9b59b6',name:'Tím'},{hex:'#f1c40f',name:'Vàng'},{hex:'#95a5a6',name:'Xám'},
    {hex:'#00b894',name:'Ngọc'},{hex:'#00cec9',name:'Xanh nhạt'},{hex:'#e17055',name:'Gạch'},{hex:'#fd79a8',name:'Hồng'}
  ];

  subtypeOptions: string[] = [];
  deviceModelOptions: string[] = [];

  previews: string[] = []; // object URLs
  get colorsFA() { return this.form.get('colors') as FormArray; }

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      price: [null, [Validators.required, Validators.min(0)]],
      discountCode: [''],
      category: [null as Category | null, Validators.required],
      subtype: [{value: null, disabled: true}],
      deviceModel: [{value: null, disabled: true}],
      otherDetail: [''],
      colors: this.fb.array<string>([]),
      images: [null] // sẽ lưu mảng File[]
      
    },);
  this.defaultColorSet = new Set(this.defaultColors.map(c => c.hex.toLowerCase()));

    // phản ứng khi category / subtype đổi
    this.form.get('category')!.valueChanges.subscribe(cat => this.onCategoryChange(cat as Category));
    this.form.get('subtype')!.valueChanges.subscribe(st => this.onSubtypeChange(st as string));
  }
isDefaultColor(hex: string): boolean {
  return this.defaultColorSet.has((hex ?? '').toLowerCase());
}
trackByIndex(index: number, _item: string): number {
  return index; // hoặc return _item nếu _item là khóa duy nhất
}
  // --- handlers ---
  onCategoryChange(cat: Category | null) {
    const list = (cat ? this.subByCategory[cat] : []) ?? [];
    this.subtypeOptions = list;
    const stCtrl = this.form.get('subtype')!;
    if (list.length) { stCtrl.enable(); }
    else { stCtrl.disable(); }
    stCtrl.setValue(null);
    this.deviceModelOptions = [];
    const dm = this.form.get('deviceModel')!;
    dm.setValue(null); dm.disable();
    this.form.get('otherDetail')!.setValue('');
  }

  onSubtypeChange(sub: string | null) {
    const dmCtrl = this.form.get('deviceModel')!;
    if (sub && this.modelsByDevice[sub]) {
      this.deviceModelOptions = this.modelsByDevice[sub];
      dmCtrl.enable();
    } else {
      this.deviceModelOptions = [];
      dmCtrl.setValue(null);
      dmCtrl.disable();
    }
    // xử lý 'Other'
    if (sub !== 'Other') this.form.get('otherDetail')!.setValue('');
  }

  toggleColor(hex: string, checked: boolean) {
    const idx = this.colorsFA.value.indexOf(hex);
    if (checked && idx === -1) this.colorsFA.push(new FormControl(hex));
    if (!checked && idx > -1) this.colorsFA.removeAt(idx);
  }

  addCustomColor(hex: string) {
    if (!hex) return;
    if (!this.colorsFA.value.includes(hex)) this.colorsFA.push(new FormControl(hex));
  }

  onFilesSelected(files: FileList | null) {
    if (!files) return;
    const arr = Array.from(files);
    this.form.get('images')!.setValue(arr);
    // cleanup previews cũ
    this.previews.forEach(u => URL.revokeObjectURL(u));
    this.previews = arr.map(f => URL.createObjectURL(f));
  }

  submit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const payload = {
      ...this.form.value,
      colors: this.colorsFA.value,
      // images: chỉ minh họa số lượng
      imagesCount: (this.form.value.images as File[] | null)?.length ?? 0
    };
    console.log('Payload gửi server:', payload);
    alert('✅ Dữ liệu sẵn sàng (xem console).');
  }

  reset() {
    this.form.reset();
    this.colorsFA.clear();
    this.onFilesSelected(null);
  }

  ngOnDestroy(): void {
    this.previews.forEach(u => URL.revokeObjectURL(u));
  }
onColorCheckboxChange(hex: string, ev: Event) {
  const checked = (ev.target as HTMLInputElement | null)?.checked ?? false;
  this.toggleColor(hex, checked);
}

  
}
