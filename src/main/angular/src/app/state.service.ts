import {Injectable} from '@angular/core';
import {Observable, of, Subject} from "rxjs";
import {AquariumState, Color} from "./state-display/state";

import * as oboe from 'oboe';

const stateUrl = '/api/state';

@Injectable({
  providedIn: 'root'
})
export class StateService {

  stateSubject: Subject = new Subject();

  constructor() {
    this.log("constructor");
    this.init();
  }


  private init(): void {
    this.log("init");
    this.stateSubject = new Subject();
    const subject = this.stateSubject;
    // return of(new AquariumState());
    oboe({
      'url': stateUrl,
      'method': 'GET'
    }).node(
      '!', function (s) {
        let aquariumState = new AquariumState(
          s.tl,
          new Color(s.led1.r, s.led1.g, s.led1.b),
          new Color(s.led2.r, s.led2.g, s.led2.b));
        console.log(`received state ${aquariumState}`);
        subject.next(aquariumState);
      }
    );
  }

  getState(): Observable<AquariumState> {
    return this.stateSubject;
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error);

      // TODO: better job of transforming error for user consumption
      console.log("error", error);
      this.log(`${operation} failed: ${error.message}`);

      // let the app keep running by returning an empty result
      return of(result as T);
    };
  }

  log(msg: string): void {
    console.log(`StateService: ${msg}`);
  }
}
