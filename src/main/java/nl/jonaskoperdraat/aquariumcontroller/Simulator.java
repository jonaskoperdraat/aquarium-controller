package nl.jonaskoperdraat.aquariumcontroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.jonaskoperdraat.aquariumcontroller.model.AquariumState;
import nl.jonaskoperdraat.aquariumcontroller.model.Color;
import nl.jonaskoperdraat.aquariumcontroller.model.SimulatorOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class Simulator {

  private static final TreeMap<LocalTime, Boolean> tlSchedule = new TreeMap<>();
  private static final long SIM_RATE_MILLIS = 100L;

  static {
    tlSchedule.put(LocalTime.of(0, 0), false);
    tlSchedule.put(LocalTime.of(2, 0), true);
    tlSchedule.put(LocalTime.of(4, 0), false);
    tlSchedule.put(LocalTime.of(6, 0), true);
    tlSchedule.put(LocalTime.of(8, 0), false);
    tlSchedule.put(LocalTime.of(10, 0), true);
    tlSchedule.put(LocalTime.of(12, 0), false);
    tlSchedule.put(LocalTime.of(14, 0), true);
    tlSchedule.put(LocalTime.of(16, 0), false);
    tlSchedule.put(LocalTime.of(18, 0), true);
    tlSchedule.put(LocalTime.of(20, 0), false);
    tlSchedule.put(LocalTime.of(22, 0), true);
  }

  private static final TreeMap<LocalTime, Color> ledSchedule = new TreeMap<>();

  static {
    ledSchedule.put(LocalTime.of(0, 0), Color.of(1, 0, 0));
    ledSchedule.put(LocalTime.of(2, 0), Color.of(1, .5, 0));
    ledSchedule.put(LocalTime.of(4, 0), Color.of(1, 1, 0));
    ledSchedule.put(LocalTime.of(6, 0), Color.of(.5, 1, 0));
    ledSchedule.put(LocalTime.of(8, 0), Color.of(0, 1, 0));
    ledSchedule.put(LocalTime.of(10, 0), Color.of(0, 1, .5));
    ledSchedule.put(LocalTime.of(12, 0), Color.of(0, 1, 1));
    ledSchedule.put(LocalTime.of(14, 0), Color.of(0, .5, 1));
    ledSchedule.put(LocalTime.of(16, 0), Color.of(0, 0, 1));
    ledSchedule.put(LocalTime.of(18, 0), Color.of(.5, 0, 1));
    ledSchedule.put(LocalTime.of(20, 0), Color.of(1, 0, 1));
    ledSchedule.put(LocalTime.of(22, 0), Color.of(1, 0, .5));
  }

  @PostConstruct
  public void init() {
    if (tlSchedule.isEmpty() || ledSchedule.isEmpty()) {
      throw new IllegalStateException("Missing schedule.");
    }
  }

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

  @Scheduled(fixedRate = SIM_RATE_MILLIS)
  public void step() {
    log.trace("step start, options: {}", options);


    if (options == null) {
      log.trace("set time to current system time.");
      time = LocalTime.now();
    } else {
      var speed = Optional.ofNullable(options.getSpeed()).orElse(1) * SIM_RATE_MILLIS;

      time = time.plus(speed, ChronoUnit.MILLIS);
      log.trace("adding {} seconds to time (={})", speed, time);
    }

    computeTlValue();
    computeLedValue();

    log.trace("step finish");
  }

  private void computeTlValue() {
    var entry = getBefore(tlSchedule, time);

    var tlValue = false;
    if (entry != null) {
      tlValue = entry.getValue();
    }

    if (aquariumState.getTl() != tlValue) {
      aquariumState.setTl(tlValue);
    }
  }

  private <K, T> Map.Entry<K, T> getBefore(TreeMap<K, T> map, K key) {
    var before = map.floorEntry(key);
    if (before != null) {
      return before;
    }
    return map.lastEntry();
  }

  private <K, V> Map.Entry<K, V> getAfter(TreeMap<K, V> map, K key) {
    var after = map.ceilingEntry(key);
    if (after != null) {
      return after;
    }
    return map.firstEntry();
  }

  private void computeLedValue() {
    var before = getBefore(ledSchedule, time);
    var after = getAfter(ledSchedule, time);

    final Duration between = calculateDurationBetween(before.getKey(), after.getKey());
    final Duration current = calculateDurationBetween(before.getKey(), time);
    double progress = ((double)current.getSeconds()) / between.getSeconds();

    Color color = interpolate(before.getValue(), after.getValue(), progress);

    log.trace("from {} to {}, progress @{}: {}, result: {}", before, after, time, progress, color);

    if (!color.equals(aquariumState.getLed1())) {
      aquariumState.setLed1(color);
    }
    if (!color.equals(aquariumState.getLed2())) {
      aquariumState.setLed2(color);
    }
  }

  static Color interpolate(Color from, Color to, double percentage) {
    return Color.of(
        interpolate(from.getR(), to.getR(), percentage),
        interpolate(from.getG(), to.getG(), percentage),
        interpolate(from.getB(), to.getB(), percentage)
    );
  }

  static double interpolate(double from, double to, double percentage) {
    if (from == to) {
      return from;
    }
    return Math.round(1000d * (from + percentage * (to - from))) / 1000d;
  }

  /**
   * Calculate duration it takes to get from {@code from} to {@code to}.
   *
   * Duration between:
   * <ul>
   *   <li>13:00 and 14:00 = 1 hour</li>
   *   <li>14:00 and 13:00 = 2 hours</li>
   * </ul>
   *
   * @param from
   * @param to
   * @return
   */
  static Duration calculateDurationBetween(LocalTime from, LocalTime to) {
    Duration duration = Duration.between(from, to);
    if (from.isAfter(to)) {
      return Duration.ofDays(1).plus(duration);
    }
    return duration;
  }

}
