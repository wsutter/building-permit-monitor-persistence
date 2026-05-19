CREATE TABLE building_permit_raw_event_registry (
    external_id VARCHAR(255) PRIMARY KEY,
    first_seen_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_building_permit_raw_event_registry_external_id
    ON building_permit_raw_event_registry (external_id);
