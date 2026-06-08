CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE building_permits (
    id UUID PRIMARY KEY,
    source TEXT NOT NULL,
    external_id TEXT NOT NULL,
    title TEXT,
    description TEXT,
    category TEXT,
    status TEXT,
    municipality TEXT,
    published_date DATE,
    address TEXT,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    geocoding_provider TEXT,
    geocoding_quality TEXT,
    geom GEOMETRY(Point, 4326),
    raw_payload JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (source, external_id)
);

CREATE INDEX idx_building_permits_source_external_id
    ON building_permits (source, external_id);

CREATE INDEX idx_building_permits_municipality
    ON building_permits (municipality);

CREATE INDEX idx_building_permits_published_date
    ON building_permits (published_date);

CREATE INDEX idx_building_permits_geom
    ON building_permits
    USING GIST (geom);
