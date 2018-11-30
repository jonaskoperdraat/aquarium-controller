package nl.jonaskoperdraat.aquariumcontroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.jonaskoperdraat.aquariumcontroller.model.AquariumState;
import nl.jonaskoperdraat.aquariumcontroller.model.Color;
import nl.jonaskoperdraat.aquariumcontroller.model.SimulatorOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class Simulator {

  private static final TreeMap<LocalTime, Boolean> tlSchedule = new TreeMap<>();

  static {
    tlSchedule.put(LocalTime.of(0, 0), false);
    tlSchedule.put(LocalTime.of(0, 10), true);
    tlSchedule.put(LocalTime.of(0, 20), false);
    tlSchedule.put(LocalTime.of(0, 30), true);
    tlSchedule.put(LocalTime.of(0, 40), false);
    tlSchedule.put(LocalTime.of(0, 50), true);
  }

  public enum State { PLAY, PAUSE }

  private final AquariumState aquariumState;

  private SimulatorOptions options = new SimulatorOptions();

  private LocalTime time = LocalTime.now();

  LocalTime getTime() {
    return time;
  }

  void setOptions(SimulatorOptions options) {
    this.options = Objects.requireNonNull(options);
    if (options.getTime() != null) {
      time = options.getTime();
    }
  }

  void reset() {
    this.options = null;
  }

  @Scheduled(fixedRate = 1000L)
  public void step() {
    log.trace("step start, options: {}", options);

    if (options == null) {
      log.trace("set time to current system time.");
      time = LocalTime.now();
    } else {
      var speed = Optional.ofNullable(options.getSpeed()).orElse(1);
      time = time.plus(speed, ChronoUnit.SECONDS);
      log.trace("adding {} seconds to time (={})", speed, time);
    }

    // Updating state for current time.
    var entry = tlSchedule.floorEntry(time);
    if (entry == null) {
      entry = tlSchedule.lastEntry();
    }
    var tlValue = false;
    if (entry != null) {
      tlValue = entry.getValue();
    }

    if (aquariumState.getTl() != tlValue) {
      log.trace("switching TL");
      aquariumState.setTl(tlValue);
    }

    log.trace("step finish");
  }

  @Scheduled(fixedRate = 5000L)
  public void randomizeLeds() {
    Random random = new Random();
    aquariumState.setLed1(new Color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
    aquariumState.setLed2(new Color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
  }

}
