package ch.studior2.buildingpermitmonitor.persistence.api;

public interface BuildingPermitRawEventRegistry {

  boolean registerIfNew(String rawId, String publicationNumber);

  boolean exists(String rawId, String publicationNumber);
}
