import { bootstrapApplication } from '@angular/platform-browser';
import { mergeApplicationConfig } from '@angular/core';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';
import { HomeComponent } from './app/home.component';
bootstrapApplication(AppComponent, appConfig)
  .catch(err => console.error(err));
