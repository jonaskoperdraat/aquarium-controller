package nl.jonaskoperdraat.aquariumcontroller;

import lombok.extern.slf4j.Slf4j;
import nl.jonaskoperdraat.aquariumcontroller.model.AquariumState;
import nl.jonaskoperdraat.aquariumcontroller.model.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.Flow;
import java.util.stream.Stream;

@SpringBootApplication
@RestController
@Slf4j
public class AquariumControllerApplication {

  public static void main(String[] args) {
    SpringApplication.run(AquariumControllerApplication.class, args);
  }

  @Bean
  public CommandLineRunner runner() {
    return new CommandLineRunner() {
      @Override
      public void run(String... args) throws Exception {
        Random r = new Random();
        while(true) {
            log.info("Updating state...");
            aquariumState.setLed1(new Color(r.nextDouble(), r.nextDouble(), r.nextDouble()));
            aquariumState.setLed2(new Color(r.nextDouble(), r.nextDouble(), r.nextDouble()));
            aquariumState.setTl(r.nextBoolean());
            try {
              Thread.sleep(r.nextInt(10000));
            } catch (InterruptedException e) {
              log.error("Interrupted", e);
            }

        }
      }
    };
  }

  @Autowired
  AquariumState aquariumState;

  @RequestMapping(value = "/state", method = RequestMethod.GET, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
  public Flux<AquariumState> getCurrentState() {
    return Flux.fromStream(
        Stream.generate(() -> this.aquariumState.copy()))
          .delayElements(Duration.ofMillis(100))
          .distinctUntilChanged();

  }

  @RequestMapping(value = "/state2", method = RequestMethod.GET, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
  public Flow.Publisher<AquariumState> getCurrentState2() {
    return AquariumState.PUBLISHER;
  }

  @RequestMapping(value = "/switchTl")
  public void switchTl() {
    aquariumState.setTl(!aquariumState.getTl());
  }

}
