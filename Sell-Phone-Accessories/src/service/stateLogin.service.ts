import { Injectable, signal } from '@angular/core';

export type Payload = { state:boolean,userName:string } | null;

@Injectable({ providedIn: 'root' })
export class StateLogin {
  payload = signal<Payload>(null);
  set(p: Payload) { this.payload.set(p); }
  clear() { this.payload.set(null); }
}
