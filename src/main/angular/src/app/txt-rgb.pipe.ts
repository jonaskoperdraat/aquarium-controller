import {Pipe, PipeTransform} from '@angular/core';
import {Color} from "./state-display/state";
import {formatNumber} from "@angular/common";

@Pipe({
  name: 'txtRgb'
})
export class TxtRgbPipe implements PipeTransform {

  transform(value: Color, args?: any): any {
    return `rgb: (${this.format(value.r)}, ${this.format(value.g)}, ${this.format(value.b)})`;
  }

  private format(value: number) {
    return formatNumber(value, 'en_US', '1.0-2');
  }

}
