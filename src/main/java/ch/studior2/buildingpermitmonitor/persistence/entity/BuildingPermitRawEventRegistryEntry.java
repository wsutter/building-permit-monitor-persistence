package ch.studior2.buildingpermitmonitor.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "building_permit_raw_event_registry")
public class BuildingPermitRawEventRegistryEntry {

  @Id
  @Column(name = "external_id", nullable = false)
  private String externalId;

  @Column(name = "first_seen_at", nullable = false)
  private OffsetDateTime firstSeenAt;

  protected BuildingPermitRawEventRegistryEntry() {}

  public BuildingPermitRawEventRegistryEntry(String externalId) {
    this.externalId = externalId;
    this.firstSeenAt = OffsetDateTime.now();
  }

  public String getExternalId() {
    return externalId;
  }
}
