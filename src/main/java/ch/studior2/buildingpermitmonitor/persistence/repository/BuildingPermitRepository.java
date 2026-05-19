package ch.studior2.buildingpermitmonitor.persistence.repository;

import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitEnrichedEvent;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

@Repository
public class BuildingPermitRepository {

  private final JdbcTemplate jdbcTemplate;
  private final ObjectMapper objectMapper;

  public BuildingPermitRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
    this.jdbcTemplate = jdbcTemplate;
    this.objectMapper = objectMapper;
  }

  public void upsert(BuildingPermitEnrichedEvent event) throws Exception {
    String rawJson = objectMapper.writeValueAsString(event);

    jdbcTemplate.update(
        """
        INSERT INTO building_permits (
            id,
            source,
            external_id,
            title,
            description,
            category,
            status,
            municipality,
            published_date,
            address,
            latitude,
            longitude,
            geom,
            raw_payload,
            created_at,
            updated_at
        )
        VALUES (
            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
            CASE
                WHEN ? IS NOT NULL AND ? IS NOT NULL
                THEN ST_SetSRID(ST_MakePoint(?, ?), 4326)
                ELSE NULL
            END,
            ?::jsonb,
            now(),
            now()
        )
        ON CONFLICT (source, external_id)
        DO UPDATE SET
            title = EXCLUDED.title,
            description = EXCLUDED.description,
            category = EXCLUDED.category,
            status = EXCLUDED.status,
            municipality = EXCLUDED.municipality,
            published_date = EXCLUDED.published_date,
            address = EXCLUDED.address,
            latitude = EXCLUDED.latitude,
            longitude = EXCLUDED.longitude,
            geom = EXCLUDED.geom,
            raw_payload = EXCLUDED.raw_payload,
            updated_at = now()
        """,
        UUID.nameUUIDFromBytes(event.permitId().getBytes(StandardCharsets.UTF_8)),
        event.source(),
        event.externalId(),
        event.title(),
        event.description(),
        event.category(),
        event.status(),
        event.municipality(),
        event.publishedDate(),
        event.address(),
        event.latitude(),
        event.longitude(),
        event.longitude(),
        event.latitude(),
        event.longitude(),
        event.latitude(),
        rawJson);
  }
}
