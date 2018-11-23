package nl.jonaskoperdraat.aquariumcontroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.jonaskoperdraat.aquariumcontroller.model.AquariumState;
import nl.jonaskoperdraat.aquariumcontroller.model.SimulatorOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.TreeMap;

import static nl.jonaskoperdraat.aquariumcontroller.Simulator.State.PLAY;

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

  public LocalTime getTime() {
    return time;
  }

  public void setOptions(SimulatorOptions options) {
    this.options = options;
    if (options.getState() != null && options.getState() == PLAY) {
      if (options.getTime() != null) {
        time = options.getTime();
      }
    }
  }

  @Scheduled(fixedRate = 1000L)
  public void step() {
    log.trace("step start, options: {}", options);

    if (options.getState() != null && options.getState() == PLAY) {
      log.trace("playing");
      if (!options.isOverride()) {
        log.trace("update time to current time");
        time = LocalTime.now();
      } else {
        if (options.getSpeed() == null) {
          options.setSpeed(1);
        }
        log.trace("update time with {} seconds", options.getSpeed());
        time = time.plus(options.getSpeed(), ChronoUnit.SECONDS);

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
      }
    } else {
      log.trace("paused");
    }

    log.trace
        ("step finish");

  }

}
