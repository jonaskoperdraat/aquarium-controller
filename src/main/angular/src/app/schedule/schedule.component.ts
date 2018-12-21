import {Component, OnInit, Pipe, PipeTransform} from '@angular/core';
import {OnOff, Schedule, ScheduleCollection, ScheduleMarker, ScheduleType} from "../model";
import {formatNumber, Time} from "@angular/common";

@Component({
  selector: 'app-schedule',
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.scss']
})
export class ScheduleComponent implements OnInit {

  objectKeys = Object.keys;

  model: ScheduleCollection = <ScheduleCollection>{
    TL: new Schedule(ScheduleType.ON_OFF, [
      new ScheduleMarker<OnOff>(<Time>{hours: 7, minutes: 0}, 'on')])
    //   new ScheduleMarker<OnOff>(<Time>{hours: 19, minutes: 0}, 'off')]),
    // 'LED 1': new Schedule(ScheduleType.RGB, [
    //   new ScheduleMarker<RGB>(<Time>{hours: 6, minutes: 45}, [0, 0, 0]),
    //   new ScheduleMarker<RGB>(<Time>{hours: 7, minutes: 0}, [1, 1, 1]),
    //   new ScheduleMarker<RGB>(<Time>{hours: 23, minutes: 59}, [1, 0, 0])
    // ]),
    // 'LED 2': new Schedule(ScheduleType.RGB, [
    //   new ScheduleMarker<RGB>(<Time>{hours: 6, minutes: 45}, [1, 0, 0]),
    //   new ScheduleMarker<RGB>(<Time>{hours: 7, minutes: 0}, [1, 0, 1])
    // ])
  };

  constructor() {
  }

  ngOnInit() {
  }

  keys() : string[] {
    return Object.keys(this.model);
  }

  schedule(key : string): Schedule {
    return this.model[key];
  }

  updateTime(marker: ScheduleMarker<any>, time : string) {
    const vals = time.split(':');
    const hours = Math.min(23, Number(vals[0]));
    const minutes = Math.min(59, Number(vals[1]));
    marker.time = {hours: hours, minutes: minutes} as Time;
  }

}

@Pipe({
  name: "percentageOfDay"
})
export class PercentageOfDayPipe implements PipeTransform {
  transform(time : Time) : number {
    return (time.hours * 60 + time.minutes) / (24 * 60 - 1) * 100;
  }
}

@Pipe({
  name: 'time'
})
export class TimePipe implements PipeTransform {
  transform(time : Time) : string {
    return `${time.hours}:${formatNumber(time.minutes, 'nl', '2.0-0')}`;
  }
}
