package ch.studior2.buildingpermitmonitor.persistence.service;

import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitEnrichedEvent;
import ch.studior2.buildingpermitmonitor.contracts.topic.KafkaTopics;
import ch.studior2.buildingpermitmonitor.persistence.repository.BuildingPermitRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BuildingPermitPersistenceConsumer {

  private final BuildingPermitRepository repository;

  public BuildingPermitPersistenceConsumer(BuildingPermitRepository repository) {
    this.repository = repository;
  }

  @KafkaListener(topics = KafkaTopics.ENRICHED, groupId = "persistence")
  public void persist(BuildingPermitEnrichedEvent event) throws Exception {
    repository.upsert(event);
  }
}
