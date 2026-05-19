package ch.studior2.buildingpermitmonitor.persistence.repository;

import ch.studior2.buildingpermitmonitor.persistence.entity.BuildingPermitRawEventRegistryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildingPermitRawEventRegistryRepository
    extends JpaRepository<BuildingPermitRawEventRegistryEntry, String> {}
