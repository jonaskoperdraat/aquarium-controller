import {BrowserModule} from '@angular/platform-browser';
import {LOCALE_ID, NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {StateDisplayComponent} from './state-display/state-display.component';
import {HttpClientModule} from "@angular/common/http";
import {TxtRgbPipe} from './txt-rgb.pipe';
import {CssRgbPipe} from './css-rgb.pipe';
import {PercentageOfDayPipe, ScheduleComponent, TimePipe} from './schedule/schedule.component';
import {ScheduleMarkerComponent} from './schedule-marker/schedule-marker.component';
import {FormsModule} from "@angular/forms";


import {registerLocaleData} from '@angular/common';
import dutchLocale from '@angular/common/locales/nl';
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from '@angular/material/input';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

registerLocaleData(dutchLocale, 'nl');

@NgModule({
  declarations: [
    AppComponent,
    StateDisplayComponent,
    TxtRgbPipe,
    CssRgbPipe,
    ScheduleComponent,
    PercentageOfDayPipe,
    ScheduleMarkerComponent,
    TimePipe
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSlideToggleModule,
    BrowserAnimationsModule
  ],
  providers: [{
    provide: LOCALE_ID, useValue: 'nl'
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
