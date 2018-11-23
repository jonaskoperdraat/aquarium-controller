package nl.jonaskoperdraat.aquariumcontroller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableScheduling
@RestController
@Slf4j
public class AquariumControllerApplication {

  public static void main(String[] args) {
    SpringApplication.run(AquariumControllerApplication.class, args);
  }

}
