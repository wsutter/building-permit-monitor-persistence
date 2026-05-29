package ch.studior2.buildingpermitmonitor.persistence.service;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.verify;

import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitEnrichedEvent;
import ch.studior2.buildingpermitmonitor.contracts.geocoding.GeocodingProvider;
import ch.studior2.buildingpermitmonitor.contracts.geocoding.GeocodingQuality;
import ch.studior2.buildingpermitmonitor.persistence.repository.BuildingPermitRepository;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

@DisplayName("BuildingPermitPersistenceConsumer")
class BuildingPermitPersistenceConsumerTest {

  private final BuildingPermitRepository repository = Mockito.mock(BuildingPermitRepository.class);
  private final BuildingPermitPersistenceConsumer consumer =
      new BuildingPermitPersistenceConsumer(repository);

  @Nested
  @DisplayName("persist")
  class Persist {

    @ParameterizedTest(name = "{0}")
    @MethodSource("events")
    @DisplayName("should delegate upsert to repository")
    void shouldDelegateUpsertToRepository(BuildingPermitEnrichedEvent event) throws Exception {
      consumer.persist(event);

      verify(repository).upsert(event);
    }

    static Stream<Arguments> events() {
      return Stream.of(
          arguments(
              named(
                  "enriched event with coordinates",
                  new BuildingPermitEnrichedEvent(
                      "kt-zh:123",
                      "kt-zh",
                      "123",
                      "Umbau Wohnung",
                      "Umbau Wohnung",
                      "RENOVATION",
                      "SUBMITTED",
                      "Thalwil",
                      LocalDate.of(2026, 5, 19),
                      "Eisenbahnstrasse 27",
                      47.2918,
                      8.5631,
                      GeocodingProvider.GEO_ADMIN,
                      GeocodingQuality.ADDRESS))));
    }
  }
}
