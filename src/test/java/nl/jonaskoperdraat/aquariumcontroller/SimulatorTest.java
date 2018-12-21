package nl.jonaskoperdraat.aquariumcontroller;

import nl.jonaskoperdraat.aquariumcontroller.model.Color;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SimulatorTest {

  @Test

  public void durationBetween() {
    assertThat(Simulator.calculateDurationBetween(
        LocalTime.of(13, 0), LocalTime.of(14, 0)), is(Duration.ofHours(1)));
    assertThat(Simulator.calculateDurationBetween(
        LocalTime.of(14, 0), LocalTime.of(13, 0)), is(Duration.ofHours(23)));
  }

  @Test
  public void colorInterpolation() {
    assertThat(Simulator.interpolate(Color.of(0, 1, .5), Color.of(.2, .5, .9), .25),
        is(Color.of(.05, .875, .6)));
  }

  @Test
  public void doubleInterpolation() {
    assertThat(Simulator.interpolate(0, .2, .25), is(.05));
    assertThat(Simulator.interpolate(1, .5, .25), is(.875));
  }

}