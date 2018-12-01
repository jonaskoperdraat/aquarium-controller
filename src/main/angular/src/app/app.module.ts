import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {StateDisplayComponent} from './state-display/state-display.component';
import {HttpClientModule} from "@angular/common/http";
import {TxtRgbPipe} from './txt-rgb.pipe';
import {CssRgbPipe} from './css-rgb.pipe';

@NgModule({
  declarations: [
    AppComponent,
    StateDisplayComponent,
    TxtRgbPipe,
    CssRgbPipe
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
