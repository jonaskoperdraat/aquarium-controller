import {Component, OnInit} from '@angular/core';
import {AquariumState} from "./state";
import {Observable} from "rxjs";
import {StateService} from "../state.service";

@Component({
  selector: 'app-state-display',
  templateUrl: './state-display.component.html',
  styleUrls: ['./state-display.component.css']
})
export class StateDisplayComponent implements OnInit {

  state$: Observable<AquariumState>;

  constructor(
    private stateService: StateService
  ) { }

  ngOnInit() {
    this.state$ = this.stateService.getState();
  }

}
