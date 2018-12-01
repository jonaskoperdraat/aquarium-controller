import {Injectable} from '@angular/core';
import {Observable, Subject} from "rxjs";
import {AquariumState, Color} from "./state-display/state";

import * as oboe from 'oboe';

const stateUrl = '/api/state';

@Injectable({
  providedIn: 'root'
})
export class StateService {

  stateSubject: Subject = new Subject();

  constructor() {
    StateService.log("constructor");
    this.init();
  }

  private init(): void {
    StateService.log("init");
    this.stateSubject = new Subject();
    const subject = this.stateSubject;
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
        subject.next(aquariumState);
      }
    ).fail(function(reason) {
      service.handleError('init')(reason);
    });
  }

  getState(): Observable<AquariumState> {
    return this.stateSubject;
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
