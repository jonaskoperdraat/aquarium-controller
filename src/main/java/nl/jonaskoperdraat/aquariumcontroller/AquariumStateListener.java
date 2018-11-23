package nl.jonaskoperdraat.aquariumcontroller;

import nl.jonaskoperdraat.aquariumcontroller.model.AquariumState;
import reactor.core.publisher.FluxSink;

public class AquariumStateListener {

  private AquariumState aquariumState;
  private FluxSink<AquariumState> emitter;

  AquariumStateListener(AquariumState state) {
    this.aquariumState = state;
  }

  public void stateChanged(AquariumState state) {
    emitter.next(state.copy());
  }

  void init() {
    aquariumState.addChangeListener(this);
  }

  void terminate() {
    aquariumState.removeChangeListener(this);
  }

  void setEmitter(FluxSink<AquariumState> emitter) {
    this.emitter = emitter;
  }

}
