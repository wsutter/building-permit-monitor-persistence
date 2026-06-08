package ch.studior2.buildingpermitmonitor.persistence.repository;

import ch.studior2.buildingpermitmonitor.persistence.entity.BuildingPermitEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingPermitRepository extends JpaRepository<BuildingPermitEntity, UUID> {

  Optional<BuildingPermitEntity> findBySourceAndExternalId(String source, String externalId);

  @Query(
      value =
          """
          SELECT *
          FROM building_permits bp
          WHERE ST_DWithin(
              CAST(bp.geom AS geography),
              CAST(:point AS geography),
              :radiusMeters
          )
          """,
      nativeQuery = true)
  List<BuildingPermitEntity> findWithinRadius(
      @Param("point") Point point, @Param("radiusMeters") double radiusMeters);

  @Query(
      value =
          """
          SELECT *
          FROM building_permits bp
          WHERE bp.geom && ST_MakeEnvelope(
              :minLon,
              :minLat,
              :maxLon,
              :maxLat,
              4326
          )
          """,
      nativeQuery = true)
  List<BuildingPermitEntity> findVisiblePermits(
      double minLon, double minLat, double maxLon, double maxLat);
}
