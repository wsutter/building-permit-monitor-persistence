package ch.studior2.buildingpermitmonitor.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.studior2.buildingpermitmonitor.contracts.geocoding.GeocodingProvider;
import ch.studior2.buildingpermitmonitor.contracts.geocoding.GeocodingQuality;
import ch.studior2.buildingpermitmonitor.persistence.entity.BuildingPermitEntity;
import ch.studior2.buildingpermitmonitor.persistence.geometry.PointFactory;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@DataJpaTest
@Testcontainers
@ContextConfiguration(classes = BuildingPermitRepositoryTest.TestApplication.class)
@Sql(statements = {"DELETE FROM building_permits"})
@DisplayName("BuildingPermitRepository")
class BuildingPermitRepositoryTest {

  private static final DockerImageName POSTGIS_IMAGE =
      DockerImageName.parse("postgis/postgis:17-3.5")
        .asCompatibleSubstituteFor("postgres");

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGIS_IMAGE);

  private final PointFactory pointFactory = new PointFactory();

  @Autowired
  private BuildingPermitRepository repository;

  @Nested
  @DisplayName("findBySourceAndExternalId")
  class FindBySourceAndExternalId {

    @Test
    @DisplayName("should find permit by natural business key")
    void shouldFindPermitByNaturalBusinessKey() {
      BuildingPermitEntity entity =
          permit("kt-zh:123", "kt-zh", "123", "Umbau Wohnung", 47.2918, 8.5631);

      repository.saveAndFlush(entity);

      Optional<BuildingPermitEntity> result = repository.findBySourceAndExternalId("kt-zh", "123");

      assertThat(result).isPresent();
      assertThat(result.get().getSource()).isEqualTo("kt-zh");
      assertThat(result.get().getExternalId()).isEqualTo("123");
      assertThat(result.get().getTitle()).isEqualTo("Umbau Wohnung");
    }

    @Test
    @DisplayName("should return empty optional when business key is unknown")
    void shouldReturnEmptyOptionalWhenBusinessKeyIsUnknown() {
      Optional<BuildingPermitEntity> result =
          repository.findBySourceAndExternalId("kt-zh", "unknown");

      assertThat(result).isEmpty();
    }
  }

  @Nested
  @DisplayName("findWithinRadius")
  class FindWithinRadius {

    @Test
    @DisplayName("should find permits within radius in meters")
    void shouldFindPermitsWithinRadiusInMeters() {
      BuildingPermitEntity thalwil =
          permit("kt-zh:123", "kt-zh", "123", "Umbau Wohnung Thalwil", 47.2918, 8.5631);

      BuildingPermitEntity zurich =
          permit("kt-zh:456", "kt-zh", "456", "Umbau Wohnung Zürich", 47.3769, 8.5417);

      repository.save(thalwil);
      repository.save(zurich);
      repository.flush();

      Point center = pointFactory.create(47.2918, 8.5631);

      List<BuildingPermitEntity> result = repository.findWithinRadius(center, 1_000);

      assertThat(result).extracting(BuildingPermitEntity::getExternalId).containsExactly("123");
    }
  }

  @Nested
  @DisplayName("findVisiblePermits")
  class FindVisiblePermits {

    @Test
    @DisplayName("should find permits inside map bounding box")
    void shouldFindPermitsInsideMapBoundingBox() {
      BuildingPermitEntity inside =
          permit("kt-zh:123", "kt-zh", "123", "Umbau Wohnung Thalwil", 47.2918, 8.5631);

      BuildingPermitEntity outside =
          permit("kt-zh:456", "kt-zh", "456", "Umbau Wohnung Zürich", 47.3769, 8.5417);

      repository.save(inside);
      repository.save(outside);
      repository.flush();

      List<BuildingPermitEntity> result = repository.findVisiblePermits(8.50, 47.25, 8.60, 47.35);

      assertThat(result).extracting(BuildingPermitEntity::getExternalId).containsExactly("123");
    }
  }

  private BuildingPermitEntity permit(
      String permitId,
      String source,
      String externalId,
      String title,
      Double latitude,
      Double longitude) {
    BuildingPermitEntity entity = new BuildingPermitEntity();

    entity.setId(UUID.nameUUIDFromBytes(permitId.getBytes(StandardCharsets.UTF_8)));
    entity.setSource(source);
    entity.setExternalId(externalId);
    entity.setTitle(title);
    entity.setDescription(title);
    entity.setCategory("RENOVATION");
    entity.setStatus("SUBMITTED");
    entity.setMunicipality("Thalwil");
    entity.setPublishedDate(LocalDate.of(2026, 5, 19));
    entity.setAddress("Eisenbahnstrasse 27");
    entity.setLatitude(latitude);
    entity.setLongitude(longitude);
    entity.setGeom(pointFactory.create(latitude, longitude));
    entity.setGeocodingProvider(GeocodingProvider.GEO_ADMIN);
    entity.setGeocodingQuality(GeocodingQuality.ADDRESS);
    entity.setRawPayload("{}");

    return entity;
  }

  @SpringBootConfiguration
  @EnableAutoConfiguration
  @EntityScan(basePackageClasses = BuildingPermitEntity.class)
  @EnableJpaRepositories(basePackageClasses = BuildingPermitRepository.class)
  static class TestApplication {}
}
