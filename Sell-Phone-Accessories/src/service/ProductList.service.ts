import { Injectable, signal } from '@angular/core';

export type Payload = { category:string,model:string } | null;

@Injectable({ providedIn: 'root' })
export class ProductList {
  payload = signal<Payload>(null);
  set(p: Payload) { this.payload.set(p); }
  clear() { this.payload.set(null); }
}
