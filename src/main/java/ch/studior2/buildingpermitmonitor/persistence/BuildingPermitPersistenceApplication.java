package ch.studior2.buildingpermitmonitor.persistence;

import ch.studior2.buildingpermitmonitor.contracts.config.KafkaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(KafkaConfig.class)
@SpringBootApplication
public class BuildingPermitPersistenceApplication {

  public static void main(String[] args) {
    SpringApplication.run(BuildingPermitPersistenceApplication.class, args);
  }
}
