package nl.jonaskoperdraat.aquariumcontroller.model;

import lombok.Data;

@Data
public class Color {

  final double r, g, b;

  public static Color of(double r, double g, double b) {
    return new Color(r, g, b);
  }

}
