package ch.studior2.buildingpermitmonitor.persistence.consumer;

import static org.mockito.Mockito.verify;

import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitEnrichedEvent;
import ch.studior2.buildingpermitmonitor.contracts.geocoding.GeocodingProvider;
import ch.studior2.buildingpermitmonitor.contracts.geocoding.GeocodingQuality;
import ch.studior2.buildingpermitmonitor.persistence.service.BuildingPermitPersistenceService;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@DisplayName("BuildingPermitPersistenceConsumer")
class BuildingPermitPersistenceConsumerTest {

  private final BuildingPermitPersistenceService service =
      Mockito.mock(BuildingPermitPersistenceService.class);

  private final BuildingPermitPersistenceConsumer consumer =
      new BuildingPermitPersistenceConsumer(service);

  @Test
  @DisplayName("should delegate enriched events to persistence service")
  void shouldDelegateEnrichedEventsToPersistenceService() throws Exception {
    BuildingPermitEnrichedEvent event = sampleEvent();

    consumer.persist(event);

    verify(service).upsert(event);
  }

  private static BuildingPermitEnrichedEvent sampleEvent() {
    return new BuildingPermitEnrichedEvent(
        "kt-zh:123456",
        "kt-zh",
        "123456",
        "Umbau Wohnung",
        "Umbau Wohnung, Balkoninstandsetzung",
        "RENOVATION",
        "SUBMITTED",
        "Thalwil",
        LocalDate.of(2026, 5, 17),
        "Eisenbahnstrasse 27, 8800 Thalwil",
        47.2918,
        8.5631,
        GeocodingProvider.GEO_ADMIN,
        GeocodingQuality.ADDRESS);
  }
}
