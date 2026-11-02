CREATE TABLE tracks (
	track_id BIGSERIAL PRIMARY KEY,
	track_number VARCHAR(20),
	latitude NUMERIC(10,6),
	longitude NUMERIC(10,6),
	speed NUMERIC(10,2),
	identity_type VARCHAR(20),
	track_status VARCHAR(20),
	created_time TIMESTAMP
);

CREATE TABLE alerts (
	alert_id BIGSERIAL PRIMARY KEY,
	track_number VARCHAR(20),
	alert_message VARCHAR(500), 
	alert_severity VARCHAR(20),
	alert_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS udp_observations (
    id BIGSERIAL PRIMARY KEY,
    source_id VARCHAR(30) NOT NULL,
    sequence_number INTEGER NOT NULL,
    track_number VARCHAR(50) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    speed DOUBLE PRECISION NOT NULL,
    identity VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    observation_time TIMESTAMP NOT NULL,
    received_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_source_sequence UNIQUE (source_id, sequence_number)
    );