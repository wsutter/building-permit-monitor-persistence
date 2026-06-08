package ch.studior2.buildingpermitmonitor.persistence.service;

import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitEnrichedEvent;
import ch.studior2.buildingpermitmonitor.persistence.entity.BuildingPermitEntity;
import ch.studior2.buildingpermitmonitor.persistence.geometry.PointFactory;
import ch.studior2.buildingpermitmonitor.persistence.repository.BuildingPermitRepository;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
@Transactional
public class BuildingPermitPersistenceService {

  private final BuildingPermitRepository repository;
  private final PointFactory pointFactory;
  private final ObjectMapper objectMapper;

  public BuildingPermitPersistenceService(
      BuildingPermitRepository repository, PointFactory pointFactory, ObjectMapper objectMapper) {
    this.repository = repository;
    this.pointFactory = pointFactory;
    this.objectMapper = objectMapper;
  }

  public void upsert(BuildingPermitEnrichedEvent event) throws Exception {

    BuildingPermitEntity entity =
        repository
            .findBySourceAndExternalId(event.source(), event.externalId())
            .orElseGet(BuildingPermitEntity::new);

    entity.setId(UUID.nameUUIDFromBytes(event.permitId().getBytes(StandardCharsets.UTF_8)));

    entity.setSource(event.source());
    entity.setExternalId(event.externalId());
    entity.setTitle(event.title());
    entity.setDescription(event.description());
    entity.setCategory(event.category());
    entity.setStatus(event.status());
    entity.setMunicipality(event.municipality());
    entity.setPublishedDate(event.publishedDate());
    entity.setAddress(event.address());

    entity.setLatitude(event.latitude());
    entity.setLongitude(event.longitude());

    entity.setGeocodingProvider(event.geocodingProvider());
    entity.setGeocodingQuality(event.geocodingQuality());

    entity.setGeom(pointFactory.create(event.latitude(), event.longitude()));

    entity.setRawPayload(objectMapper.writeValueAsString(event));

    repository.save(entity);
  }
}
