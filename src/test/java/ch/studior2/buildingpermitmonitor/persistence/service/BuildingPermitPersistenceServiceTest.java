package ch.studior2.buildingpermitmonitor.persistence.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitEnrichedEvent;
import ch.studior2.buildingpermitmonitor.contracts.geocoding.GeocodingProvider;
import ch.studior2.buildingpermitmonitor.contracts.geocoding.GeocodingQuality;
import ch.studior2.buildingpermitmonitor.persistence.entity.BuildingPermitEntity;
import ch.studior2.buildingpermitmonitor.persistence.geometry.PointFactory;
import ch.studior2.buildingpermitmonitor.persistence.repository.BuildingPermitRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import tools.jackson.databind.ObjectMapper;

@DisplayName("BuildingPermitPersistenceService")
class BuildingPermitPersistenceServiceTest {

  private final BuildingPermitRepository repository = Mockito.mock(BuildingPermitRepository.class);
  private final PointFactory pointFactory = Mockito.mock(PointFactory.class);
  private final ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);

  private final BuildingPermitPersistenceService service =
      new BuildingPermitPersistenceService(repository, pointFactory, objectMapper);

  @Nested
  @DisplayName("upsert")
  class Upsert {

    @Test
    @DisplayName("should create and save new entity from enriched event")
    void shouldCreateAndSaveNewEntityFromEnrichedEvent() throws Exception {
      BuildingPermitEnrichedEvent event = eventWithCoordinates();
      Point point = Mockito.mock(Point.class);

      when(repository.findBySourceAndExternalId("kt-zh", "123")).thenReturn(Optional.empty());
      when(pointFactory.create(47.2918, 8.5631)).thenReturn(point);
      when(objectMapper.writeValueAsString(event)).thenReturn("{\"permitId\":\"kt-zh:123\"}");

      service.upsert(event);

      ArgumentCaptor<BuildingPermitEntity> captor =
          ArgumentCaptor.forClass(BuildingPermitEntity.class);

      verify(repository).save(captor.capture());

      BuildingPermitEntity entity = captor.getValue();

      assertThat(entity.getId()).isNotNull();
      assertThat(entity.getSource()).isEqualTo("kt-zh");
      assertThat(entity.getExternalId()).isEqualTo("123");
      assertThat(entity.getTitle()).isEqualTo("Umbau Wohnung");
      assertThat(entity.getDescription()).isEqualTo("Umbau Wohnung");
      assertThat(entity.getCategory()).isEqualTo("RENOVATION");
      assertThat(entity.getStatus()).isEqualTo("SUBMITTED");
      assertThat(entity.getMunicipality()).isEqualTo("Thalwil");
      assertThat(entity.getPublishedDate()).isEqualTo(LocalDate.of(2026, 5, 19));
      assertThat(entity.getAddress()).isEqualTo("Eisenbahnstrasse 27");
      assertThat(entity.getLatitude()).isEqualTo(47.2918);
      assertThat(entity.getLongitude()).isEqualTo(8.5631);
      assertThat(entity.getGeocodingProvider()).isEqualTo(GeocodingProvider.GEO_ADMIN);
      assertThat(entity.getGeocodingQuality()).isEqualTo(GeocodingQuality.ADDRESS);
      assertThat(entity.getGeom()).isSameAs(point);
      assertThat(entity.getRawPayload()).isEqualTo("{\"permitId\":\"kt-zh:123\"}");
    }

    @Test
    @DisplayName("should update existing entity instead of creating duplicate")
    void shouldUpdateExistingEntityInsteadOfCreatingDuplicate() throws Exception {
      BuildingPermitEntity existingEntity = new BuildingPermitEntity();
      existingEntity.setSource("kt-zh");
      existingEntity.setExternalId("123");
      existingEntity.setTitle("Old title");

      BuildingPermitEnrichedEvent event = eventWithCoordinates();

      when(repository.findBySourceAndExternalId("kt-zh", "123"))
          .thenReturn(Optional.of(existingEntity));
      when(objectMapper.writeValueAsString(event)).thenReturn("{\"permitId\":\"kt-zh:123\"}");

      service.upsert(event);

      verify(repository).save(existingEntity);

      assertThat(existingEntity.getTitle()).isEqualTo("Umbau Wohnung");
      assertThat(existingEntity.getDescription()).isEqualTo("Umbau Wohnung");
      assertThat(existingEntity.getCategory()).isEqualTo("RENOVATION");
      assertThat(existingEntity.getStatus()).isEqualTo("SUBMITTED");
      assertThat(existingEntity.getMunicipality()).isEqualTo("Thalwil");
    }

    @Test
    @DisplayName("should keep geometry null when no coordinates are available")
    void shouldKeepGeometryNullWhenNoCoordinatesAreAvailable() throws Exception {
      BuildingPermitEnrichedEvent event = eventWithoutCoordinates();

      when(repository.findBySourceAndExternalId("kt-zh", "124")).thenReturn(Optional.empty());
      when(pointFactory.create(null, null)).thenReturn(null);
      when(objectMapper.writeValueAsString(event)).thenReturn("{\"permitId\":\"kt-zh:124\"}");

      service.upsert(event);

      ArgumentCaptor<BuildingPermitEntity> captor =
          ArgumentCaptor.forClass(BuildingPermitEntity.class);

      verify(repository).save(captor.capture());

      BuildingPermitEntity entity = captor.getValue();

      assertThat(entity.getLatitude()).isNull();
      assertThat(entity.getLongitude()).isNull();
      assertThat(entity.getGeom()).isNull();
      assertThat(entity.getRawPayload()).isEqualTo("{\"permitId\":\"kt-zh:124\"}");
    }
  }

  private static BuildingPermitEnrichedEvent eventWithCoordinates() {
    return new BuildingPermitEnrichedEvent(
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
        GeocodingQuality.ADDRESS);
  }

  private static BuildingPermitEnrichedEvent eventWithoutCoordinates() {
    return new BuildingPermitEnrichedEvent(
        "kt-zh:124",
        "kt-zh",
        "124",
        "Dachsanierung",
        "Dachsanierung",
        "RENOVATION",
        "SUBMITTED",
        "Meilen",
        LocalDate.of(2026, 5, 20),
        "Dorfstrasse 1",
        null,
        null,
        GeocodingProvider.GEO_ADMIN,
        GeocodingQuality.NOT_FOUND);
  }
}
