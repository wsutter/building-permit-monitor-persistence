package ch.studior2.buildingpermitmonitor.persistence.geometry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

@Component
public class PointFactory {

  private static final GeometryFactory GEOMETRY_FACTORY =
      new GeometryFactory(new PrecisionModel(), 4326);

  public Point create(Double latitude, Double longitude) {

    if (latitude == null || longitude == null) {
      return null;
    }

    return GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude));
  }
}
