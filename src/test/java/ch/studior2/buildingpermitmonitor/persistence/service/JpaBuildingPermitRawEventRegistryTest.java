package ch.studior2.buildingpermitmonitor.persistence.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.studior2.buildingpermitmonitor.persistence.entity.BuildingPermitRawEventRegistryEntry;
import ch.studior2.buildingpermitmonitor.persistence.repository.BuildingPermitRawEventRegistryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

@DisplayName("JpaBuildingPermitRawEventRegistry")
class JpaBuildingPermitRawEventRegistryTest {

  private final BuildingPermitRawEventRegistryRepository repository =
      Mockito.mock(BuildingPermitRawEventRegistryRepository.class);
  private final JpaBuildingPermitRawEventRegistry registry =
      new JpaBuildingPermitRawEventRegistry(repository);

  @Nested
  @DisplayName("registerIfNew")
  class RegisterIfNew {

    @Test
    @DisplayName("should save a composed external id and return true for a new event")
    void shouldSaveComposedExternalIdAndReturnTrueForNewEvent() {
      registry.registerIfNew("raw-123", "pub-456");

      ArgumentCaptor<BuildingPermitRawEventRegistryEntry> entryCaptor =
          ArgumentCaptor.forClass(BuildingPermitRawEventRegistryEntry.class);
      verify(repository).save(entryCaptor.capture());

      assertThat(entryCaptor.getValue().getExternalId()).isEqualTo("raw-123:pub-456");
    }

    @Test
    @DisplayName("should return true when repository save succeeds")
    void shouldReturnTrueWhenRepositorySaveSucceeds() {
      boolean registered = registry.registerIfNew("raw-123", "pub-456");

      assertThat(registered).isTrue();
    }

    @Test
    @DisplayName("should return false when the composed external id already exists")
    void shouldReturnFalseWhenComposedExternalIdAlreadyExists() {
      when(repository.save(any(BuildingPermitRawEventRegistryEntry.class)))
          .thenThrow(new DataIntegrityViolationException("duplicate external id"));

      boolean registered = registry.registerIfNew("raw-123", "pub-456");

      assertThat(registered).isFalse();
    }
  }

  @Nested
  @DisplayName("exists")
  class Exists {

    @Test
    @DisplayName("should check the composed external id")
    void shouldCheckComposedExternalId() {
      when(repository.existsById("raw-123:pub-456")).thenReturn(true);

      boolean exists = registry.exists("raw-123", "pub-456");

      assertThat(exists).isTrue();
      verify(repository).existsById("raw-123:pub-456");
    }
  }
}
