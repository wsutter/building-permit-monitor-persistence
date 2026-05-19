package ch.studior2.buildingpermitmonitor.persistence.config;

import ch.studior2.buildingpermitmonitor.contracts.config.JacksonConfig;
import ch.studior2.buildingpermitmonitor.persistence.api.BuildingPermitRawEventRegistry;
import ch.studior2.buildingpermitmonitor.persistence.repository.BuildingPermitRawEventRegistryRepository;
import ch.studior2.buildingpermitmonitor.persistence.service.JpaBuildingPermitRawEventRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(JacksonConfig.class)
@Configuration
public class BuildingPermitPersistenceConfig {

  @Bean
  public BuildingPermitRawEventRegistry buildingPermitRawEventRegistry(
      BuildingPermitRawEventRegistryRepository repository) {
    return new JpaBuildingPermitRawEventRegistry(repository);
  }
}
