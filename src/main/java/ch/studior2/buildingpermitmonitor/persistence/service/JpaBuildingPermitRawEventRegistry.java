package ch.studior2.buildingpermitmonitor.persistence.service;

import ch.studior2.buildingpermitmonitor.persistence.api.BuildingPermitRawEventRegistry;
import ch.studior2.buildingpermitmonitor.persistence.entity.BuildingPermitRawEventRegistryEntry;
import ch.studior2.buildingpermitmonitor.persistence.repository.BuildingPermitRawEventRegistryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaBuildingPermitRawEventRegistry implements BuildingPermitRawEventRegistry {

  private final BuildingPermitRawEventRegistryRepository repository;

  public JpaBuildingPermitRawEventRegistry(BuildingPermitRawEventRegistryRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional
  public boolean registerIfNew(String rawId, String publicationNumber) {
    String externalId = toExternalId(rawId, publicationNumber);

    try {
      repository.save(new BuildingPermitRawEventRegistryEntry(externalId));
      return true;
    } catch (DataIntegrityViolationException duplicate) {
      return false;
    }
  }

  @Override
  @Transactional(readOnly = true)
  public boolean exists(String rawId, String publicationNumber) {
    return repository.existsById(toExternalId(rawId, publicationNumber));
  }

  private static String toExternalId(String rawId, String publicationNumber) {
    return rawId + ":" + publicationNumber;
  }
}
