package nl.jonaskoperdraat.aquariumcontroller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

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
  public static final SubmissionPublisher<AquariumState> PUBLISHER =
      new SubmissionPublisher<>(Executors.newFixedThreadPool(1), Flow.defaultBufferSize());

  public AquariumState(boolean tl, Color led1, Color led2) {
    this.tl = tl;
    this.led1 = led1;
    this.led2 = led2;
  }

  public boolean getTl() {
    return tl;
  }

  @JsonIgnore
  public AquariumState copy() {
    return new AquariumState(tl, led1, led2);
  }

  public void setTl(boolean tl) {
    AquariumState previousState = copy();
    this.tl = tl;
    notifyOnChange(previousState);
  }

  public void setLed1(Color led) {
    AquariumState previousState = copy();
    this.led1 = led;
    notifyOnChange(previousState);
  }

  public void setLed2(Color led) {
    AquariumState previousState = copy();
    this.led2 = led;
    notifyOnChange(previousState);
  }

  @JsonIgnore
  private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

  @JsonIgnore
  private final Runnable task = new NotifyTask(this);

  @JsonIgnore
  private final Set<ScheduledFuture> futures = new HashSet<>();

  private void notifyOnChange(AquariumState previous) {
    if (!previous.equals(this)) {
        futures.stream()
            .filter(Predicate.not(ScheduledFuture::isDone))
            .filter(Predicate.not(ScheduledFuture::isCancelled))
            .forEach(f -> {
              log.debug("cancelling");
              f.cancel(false);
            });
        futures.clear();
      }
      log.debug("scheduling");
      futures.add(scheduledExecutorService.schedule(task, 100, TimeUnit.MILLISECONDS));
    }

  @Data
  private static class NotifyTask implements Runnable {

    private final AquariumState aquariumState;

    @Override
    public void run() {
      log.info("submitting...");
      PUBLISHER.submit(aquariumState.copy());
    }
  }


}
