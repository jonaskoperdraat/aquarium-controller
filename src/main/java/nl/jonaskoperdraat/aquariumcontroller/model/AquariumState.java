package nl.jonaskoperdraat.aquariumcontroller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.jonaskoperdraat.aquariumcontroller.AquariumStateListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.function.Predicate.not;

@Data
@Component
@Slf4j
@RequiredArgsConstructor
public class AquariumState {

  @Getter(AccessLevel.NONE)
  boolean tl = true;
  Color led1 = new Color(1.0, 0, 1.0);
  Color led2 = new Color(0, 1.0, 0);

  @JsonIgnore
  private final Set<AquariumStateListener> listeners = new HashSet<>();

  @JsonIgnore
  private final Set<ScheduledFuture> futures = new HashSet<>();

  public AquariumState(boolean tl, Color led1, Color led2) {
    this.tl = tl;
    this.led1 = led1;
    this.led2 = led2;
  }

  @JsonIgnore
  public AquariumState copy() {
    return new AquariumState(tl, led1, led2);
  }

  public boolean getTl() {
    return tl;
  }


  public void setTl(boolean tl) {
    this.tl = tl;
    publishChange();
  }

  public void setLed1(Color led) {
    this.led1 = led;
    publishChange();
  }

  public void setLed2(Color led) {
    this.led2 = led;
    publishChange();
  }


  public void addChangeListener(AquariumStateListener listener) {
    listeners.add(listener);
  }

  public void removeChangeListener(AquariumStateListener listener) {
    listeners.remove(listener);
  }

  /**
   * Notify any listeners of changes to this state. Waits 100 ms before publishing, so multiple changes in short will be
   * published as one.
   */
  private void publishChange() {
    log.trace("publishChange");
    futures.add(Executors.newSingleThreadScheduledExecutor().schedule(
        () -> {
          log.debug("notify listeners ({})", listeners);
          futures.stream()
              .filter(not(this::equals))
              .forEach(f -> f.cancel(false));
          futures.clear();
          listeners.forEach(listener -> {
            log.trace("notify listener");
            listener.stateChanged(this);
          });
        }, 100, TimeUnit.MILLISECONDS));
  }

}
