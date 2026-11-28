-- V1__tour_execution_monitoring.sql
-- Versioned schema migration for TourMonitoring TourExecution aggregate and child collections

CREATE TABLE IF NOT EXISTS tour_executions (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    tour_id BIGINT NOT NULL,
    guide_id BIGINT NOT NULL,
    booking_id BIGINT,
    status VARCHAR(30) NOT NULL,
    risk_level VARCHAR(30) NOT NULL,
    current_latitude DOUBLE PRECISION,
    current_longitude DOUBLE PRECISION,
    scheduled_start_at TIMESTAMPTZ NOT NULL,
    started_at TIMESTAMPTZ,
    paused_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    aborted_at TIMESTAMPTZ,
    pause_reason VARCHAR(500),
    abort_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tour_executions_status ON tour_executions (status);
CREATE INDEX IF NOT EXISTS idx_tour_executions_guide_id ON tour_executions (guide_id);
CREATE INDEX IF NOT EXISTS idx_tour_executions_tour_id ON tour_executions (tour_id);

CREATE TABLE IF NOT EXISTS tour_execution_checkpoints (
    tour_execution_id BIGINT NOT NULL REFERENCES tour_executions (id) ON DELETE CASCADE,
    checkpoint_index INTEGER NOT NULL,
    checkpoint_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    sequence_order INTEGER NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    expected_arrival TIMESTAMPTZ NOT NULL,
    latest_allowed_arrival TIMESTAMPTZ NOT NULL,
    reached BOOLEAN NOT NULL DEFAULT FALSE,
    reached_at TIMESTAMPTZ,
    delay_minutes BIGINT NOT NULL DEFAULT 0,
    delayed_beyond_tolerance BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (tour_execution_id, checkpoint_index)
);

CREATE TABLE IF NOT EXISTS tour_execution_participants (
    tour_execution_id BIGINT NOT NULL REFERENCES tour_executions (id) ON DELETE CASCADE,
    participant_index INTEGER NOT NULL,
    participant_id BIGINT NOT NULL,
    tourist_id BIGINT NOT NULL,
    booking_id BIGINT,
    attendance_status VARCHAR(30) NOT NULL,
    checked_in_at TIMESTAMPTZ,
    PRIMARY KEY (tour_execution_id, participant_index)
);

CREATE TABLE IF NOT EXISTS tour_execution_incidents (
    tour_execution_id BIGINT NOT NULL REFERENCES tour_executions (id) ON DELETE CASCADE,
    incident_index INTEGER NOT NULL,
    incident_id BIGINT NOT NULL,
    type VARCHAR(100) NOT NULL,
    severity VARCHAR(30) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    reported_by_id BIGINT NOT NULL,
    reported_by_role VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    resolution_notes VARCHAR(1000),
    reported_at TIMESTAMPTZ NOT NULL,
    resolved_at TIMESTAMPTZ,
    PRIMARY KEY (tour_execution_id, incident_index)
);

CREATE TABLE IF NOT EXISTS tour_execution_alerts (
    tour_execution_id BIGINT NOT NULL REFERENCES tour_executions (id) ON DELETE CASCADE,
    alert_index INTEGER NOT NULL,
    alert_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL,
    severity VARCHAR(30) NOT NULL,
    message VARCHAR(500) NOT NULL,
    source_user_id BIGINT,
    source_role VARCHAR(30),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (tour_execution_id, alert_index)
);

CREATE TABLE IF NOT EXISTS tour_execution_timeline (
    tour_execution_id BIGINT NOT NULL REFERENCES tour_executions (id) ON DELETE CASCADE,
    timeline_index INTEGER NOT NULL,
    entry_id BIGINT NOT NULL,
    event_type VARCHAR(60) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (tour_execution_id, timeline_index)
);

CREATE INDEX IF NOT EXISTS idx_tour_execution_timeline_occurred_at
    ON tour_execution_timeline (occurred_at);
