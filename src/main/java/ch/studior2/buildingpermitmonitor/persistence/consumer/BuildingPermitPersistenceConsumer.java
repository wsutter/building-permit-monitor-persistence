package ch.studior2.buildingpermitmonitor.persistence.consumer;

import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitEnrichedEvent;
import ch.studior2.buildingpermitmonitor.contracts.group.KafkaGroupIDs;
import ch.studior2.buildingpermitmonitor.contracts.topic.KafkaTopics;
import ch.studior2.buildingpermitmonitor.persistence.service.BuildingPermitPersistenceService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BuildingPermitPersistenceConsumer {

  private final BuildingPermitPersistenceService service;

  public BuildingPermitPersistenceConsumer(BuildingPermitPersistenceService service) {
    this.service = service;
  }

  @KafkaListener(topics = KafkaTopics.ENRICHED, groupId = KafkaGroupIDs.PERSISTENCE)
  public void persist(BuildingPermitEnrichedEvent event) throws Exception {

    service.upsert(event);
  }
}
