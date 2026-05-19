package ch.studior2.buildingpermitmonitor.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitEnrichedEvent;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.ObjectMapper;

@DisplayName("BuildingPermitRepository")
class BuildingPermitRepositoryTest {

  private final JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
  private final ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
  private final BuildingPermitRepository repository =
      new BuildingPermitRepository(jdbcTemplate, objectMapper);

  @Nested
  @DisplayName("upsert")
  class Upsert {

    @Test
    @DisplayName("should serialize the event and execute an idempotent upsert")
    void shouldSerializeEventAndExecuteIdempotentUpsert() throws Exception {
      BuildingPermitEnrichedEvent event = eventWithCoordinates();
      when(objectMapper.writeValueAsString(event)).thenReturn("{\"permitId\":\"kt-zh:123\"}");

      repository.upsert(event);

      ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<Object[]> paramsCaptor = ArgumentCaptor.forClass(Object[].class);
      verify(jdbcTemplate).update(sqlCaptor.capture(), paramsCaptor.capture());

      String sql = sqlCaptor.getValue();
      Object[] params = paramsCaptor.getValue();

      assertThat(sql)
          .contains("INSERT INTO building_permits")
          .contains("ON CONFLICT (source, external_id)")
          .contains("DO UPDATE SET")
          .contains("ST_SetSRID(ST_MakePoint(?, ?), 4326)")
          .contains("?::jsonb");

      assertThat(params)
          .containsExactly(
              UUID.nameUUIDFromBytes("kt-zh:123".getBytes(StandardCharsets.UTF_8)),
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
              8.5631,
              47.2918,
              8.5631,
              47.2918,
              "{\"permitId\":\"kt-zh:123\"}");
    }

    @Test
    @DisplayName("should keep geometry parameters nullable when no coordinates are available")
    void shouldKeepGeometryParametersNullableWhenNoCoordinatesAreAvailable() throws Exception {
      BuildingPermitEnrichedEvent event = eventWithoutCoordinates();
      when(objectMapper.writeValueAsString(event)).thenReturn("{\"permitId\":\"kt-zh:124\"}");

      repository.upsert(event);

      ArgumentCaptor<Object[]> paramsCaptor = ArgumentCaptor.forClass(Object[].class);
      verify(jdbcTemplate).update(any(String.class), paramsCaptor.capture());

      Object[] params = paramsCaptor.getValue();

      assertThat(params[10]).isNull();
      assertThat(params[11]).isNull();
      assertThat(params[12]).isNull();
      assertThat(params[13]).isNull();
      assertThat(params[14]).isNull();
      assertThat(params[15]).isNull();
      assertThat(params[16]).isEqualTo("{\"permitId\":\"kt-zh:124\"}");
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
        8.5631);
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
        null);
  }
}
