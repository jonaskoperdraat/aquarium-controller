package nl.jonaskoperdraat.aquariumcontroller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalTime;

@Data
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class SimulatorOptions {

  LocalTime time;
  Integer speed;

}