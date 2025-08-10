import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
// đây là file giống với index.js bên reactjs
bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
