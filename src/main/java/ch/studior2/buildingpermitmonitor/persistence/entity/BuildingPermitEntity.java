package ch.studior2.buildingpermitmonitor.persistence.entity;

import ch.studior2.buildingpermitmonitor.contracts.geocoding.GeocodingProvider;
import ch.studior2.buildingpermitmonitor.contracts.geocoding.GeocodingQuality;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.UUID;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

@Entity
@Table(
    name = "building_permits",
    uniqueConstraints = @UniqueConstraint(columnNames = {"source", "external_id"}))
public class BuildingPermitEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private String source;

  @Column(name = "external_id", nullable = false)
  private String externalId;

  private String title;

  @Column(columnDefinition = "text")
  private String description;

  private String category;

  private String status;

  private String municipality;

  @Column(name = "published_date")
  private LocalDate publishedDate;

  private String address;

  private Double latitude;

  private Double longitude;

  @Enumerated(EnumType.STRING)
  @Column(name = "geocoding_provider")
  private GeocodingProvider geocodingProvider;

  @Enumerated(EnumType.STRING)
  @Column(name = "geocoding_quality")
  private GeocodingQuality geocodingQuality;

  @JdbcTypeCode(SqlTypes.GEOMETRY)
  @Column(columnDefinition = "geometry(Point,4326)")
  private Point geom;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "raw_payload", columnDefinition = "jsonb")
  private String rawPayload;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMunicipality() {
    return municipality;
  }

  public void setMunicipality(String municipality) {
    this.municipality = municipality;
  }

  public LocalDate getPublishedDate() {
    return publishedDate;
  }

  public void setPublishedDate(LocalDate publishedDate) {
    this.publishedDate = publishedDate;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public GeocodingProvider getGeocodingProvider() {
    return geocodingProvider;
  }

  public void setGeocodingProvider(GeocodingProvider geocodingProvider) {
    this.geocodingProvider = geocodingProvider;
  }

  public GeocodingQuality getGeocodingQuality() {
    return geocodingQuality;
  }

  public void setGeocodingQuality(GeocodingQuality geocodingQuality) {
    this.geocodingQuality = geocodingQuality;
  }

  public Point getGeom() {
    return geom;
  }

  public void setGeom(Point geom) {
    this.geom = geom;
  }

  public String getRawPayload() {
    return rawPayload;
  }

  public void setRawPayload(String rawPayload) {
    this.rawPayload = rawPayload;
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }

    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    BuildingPermitEntity other = (BuildingPermitEntity) obj;

    return new EqualsBuilder()
        .append(id, other.id)
        .append(source, other.source)
        .append(externalId, other.externalId)
        .isEquals();
  }

  @Override
  public int hashCode() {

    return new HashCodeBuilder(17, 37).append(id).append(source).append(externalId).toHashCode();
  }
}
