package ch.studior2.buildingpermitmonitor.persistence.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.locationtech.jts.geom.Point;

@DisplayName("PointFactory")
class PointFactoryTest {

  private final PointFactory factory = new PointFactory();

  @Nested
  @DisplayName("valid coordinates")
  class ValidCoordinates {

    @ParameterizedTest(name = "{0}")
    @MethodSource("coordinates")
    @DisplayName("should create WGS84 point with longitude as x and latitude as y")
    void shouldCreateWgs84Point(Double latitude, Double longitude) {
      Point point = factory.create(latitude, longitude);

      assertEquals(longitude, point.getX());
      assertEquals(latitude, point.getY());
      assertEquals(4326, point.getSRID());
    }

    static Stream<Arguments> coordinates() {
      return Stream.of(
          arguments(named("Thalwil", 47.2918), 8.5631),
          arguments(named("Zürich", 47.3769), 8.5417));
    }
  }

  @Nested
  @DisplayName("missing coordinates")
  class MissingCoordinates {

    @ParameterizedTest(name = "{0}")
    @MethodSource("coordinates")
    @DisplayName("should return null when latitude or longitude is missing")
    void shouldReturnNullWhenCoordinateIsMissing(Double latitude, Double longitude) {
      assertNull(factory.create(latitude, longitude));
    }

    static Stream<Arguments> coordinates() {
      return Stream.of(
          arguments(named("missing latitude", null), 8.5631),
          arguments(named("missing longitude", 47.2918), null),
          arguments(named("missing both", null), null));
    }
  }
}
