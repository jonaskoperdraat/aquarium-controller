package nl.jonaskoperdraat.aquariumcontroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.jonaskoperdraat.aquariumcontroller.model.AquariumState;
import nl.jonaskoperdraat.aquariumcontroller.model.SimulatorOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.time.LocalTime;
import java.util.stream.Stream;

@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class RestController {

  final AquariumState aquariumState;
  final Simulator simulator;

  @RequestMapping(value = "/state", method = RequestMethod.GET, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
  public Flux<AquariumState> getCurrentState2() {
    log.trace("/state");
    var listener = new AquariumStateListener(aquariumState);
    return Flux.concat(
        Flux.just(aquariumState.copy()),
        Flux.create(emitter -> {
          listener.setEmitter(emitter);
          listener.init();
        }, FluxSink.OverflowStrategy.DROP)
    ).doFinally(signalType -> {
      listener.terminate();
    });
  }


  @RequestMapping(value = "/sim/time", method = RequestMethod.GET, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
  public Flux<LocalTime> getSimulationTime() {
    return Flux.fromStream(
        Stream.generate(simulator::getTime))
        .delayElements(Duration.ofSeconds(1));
  }

  @RequestMapping(value = "/sim", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public void controlSim(@RequestBody SimulatorOptions options) {
    simulator.setOptions(options);
  }

  @RequestMapping(value = "/switchTl")
  public void switchTl() {
    log.trace("/switchTl");
    aquariumState.setTl(!aquariumState.getTl());
  }
}
