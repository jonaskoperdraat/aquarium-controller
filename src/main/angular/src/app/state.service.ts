import {Injectable} from '@angular/core';
import {Observable, Subject} from "rxjs";
import {AquariumState, Color} from "./state-display/state";

import * as oboe from 'oboe';

const stateUrl = '/api/state';
const timeUrl = '/api/sim/time';

@Injectable({
  providedIn: 'root'
})
export class StateService {

  stateSubject: Subject<AquariumState>;
  timeSubject: Subject;

  constructor() {
    StateService.log("constructor");
    this.init();
  }

  private init(): void {
    StateService.log("init");
    this.stateSubject = new Subject();
    const stateSubject = this.stateSubject;
    const service = this;
    oboe({
      'url': stateUrl,
      'method': 'GET'
    }).node(
      '!', function (s) {
        let aquariumState = new AquariumState(
          s.tl,
          new Color(s.led1.r, s.led1.g, s.led1.b),
          new Color(s.led2.r, s.led2.g, s.led2.b));
        stateSubject.next(aquariumState);
      }
    ).fail(function(reason) {
      service.handleError('init')(reason);
    });

    const timeSubject = this.timeSubject;
    oboe({
      'url': timeUrl,
      'method': 'GET'
    }).start(function(s) {
      console.log("started receiving time.");
    }).on('string', function(s) {
      console.log('received', s);
      }
    )
    //"17:02:03.361287"
  }

  getState(): Observable<AquariumState> {
    return this.stateSubject;
  }

  getTime(): Observable<any> {
    return this.timeSubject;
  }

  private handleError(operation = 'operation') {
    return (error: any): void => {

      // TODO: send the error to remote logging infrastructure
      console.error(error);

      // TODO: better job of transforming error for user consumption
      StateService.log(`${operation} failed: ${error.body}`);
    };
  }

  private static log(msg: string): void {
    console.log(`StateService: ${msg}`);
  }
}
