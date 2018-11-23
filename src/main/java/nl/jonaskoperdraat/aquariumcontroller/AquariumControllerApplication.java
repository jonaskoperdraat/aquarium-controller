package nl.jonaskoperdraat.aquariumcontroller;

import lombok.extern.slf4j.Slf4j;
import nl.jonaskoperdraat.aquariumcontroller.model.AquariumState;
import nl.jonaskoperdraat.aquariumcontroller.model.SimulatorOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.time.LocalTime;
import java.util.stream.Stream;

@SpringBootApplication
@EnableScheduling
@RestController
@Slf4j
public class AquariumControllerApplication {

  public static void main(String[] args) {
    SpringApplication.run(AquariumControllerApplication.class, args);
  }

  @Autowired
  AquariumState aquariumState;

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
        })
        ;
  }

  @Autowired
  Simulator simulator;

  @RequestMapping(value = "/sim/time", method = RequestMethod.GET, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
  public Flux<LocalTime> getSimulationTime() {
    return Flux.fromStream(
        Stream.generate(() -> simulator.getTime()))
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
