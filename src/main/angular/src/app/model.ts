import {Time} from "@angular/common";

export interface ScheduleCollection {
  [name: string]: Schedule;
}

export class Schedule {
  constructor(
    public type: ScheduleType,
    public markers: ScheduleMarker<any>[]) {
  }
}

export class ScheduleMarker<T> {
  constructor(
    public time: Time,
    public value: T) {
  }
}

export enum ScheduleType{
  ON_OFF = 'on/off',
  RGB = 'rgb'
}

export type OnOff = 'on' | 'off';

export type RGB = [number, number, number];
