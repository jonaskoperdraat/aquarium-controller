import {Pipe, PipeTransform} from '@angular/core';
import {Color} from "./state-display/state";

@Pipe({
  name: 'cssRgb'
})
export class CssRgbPipe implements PipeTransform {

  transform(led: Color, args?: any): any {
    return 'rgb(' + (led.r * 255) + ', ' + (led.g * 255) + ', ' + (led.b * 255) + ')';
  }

}
