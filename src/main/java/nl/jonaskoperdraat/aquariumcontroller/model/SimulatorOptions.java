package nl.jonaskoperdraat.aquariumcontroller.model;

import lombok.Data;
import nl.jonaskoperdraat.aquariumcontroller.Simulator;

import java.time.LocalTime;

import static nl.jonaskoperdraat.aquariumcontroller.Simulator.State.PLAY;

@Data
public class SimulatorOptions {

  boolean override = false;
  Simulator.State state = PLAY;
  LocalTime time;
  Integer speed;

}