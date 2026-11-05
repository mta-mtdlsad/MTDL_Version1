CREATE TABLE IF NOT EXISTS tracks (
    track_id BIGSERIAL PRIMARY KEY,
    source_id VARCHAR(30) NOT NULL,
    sequence_number INTEGER NOT NULL,
    track_number VARCHAR(50) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    speed DOUBLE PRECISION NOT NULL,
    identity_type VARCHAR(30) NOT NULL,
    track_status VARCHAR(30) NOT NULL,
    created_time TIMESTAMP NOT NULL,
    recieved_time TIMESTAMP NOT NULL,
    CONSTRAINT unique_source_sequence UNIQUE (source_id, sequence_number)
);

CREATE TABLE alerts (
	alert_id BIGSERIAL PRIMARY KEY,
	track_number VARCHAR(20),
	alert_message VARCHAR(500), 
	alert_severity VARCHAR(20),
	alert_time TIMESTAMP
);

CREATE TABLE sources (
    source_id VARCHAR(30) PRIMARY KEY,
    source_status VARCHAR(30),
    first_seen TIMESTAMP,
    last_seen TIMESTAMP,
    highest_sequence INTEGER,
    received_count INTEGER,
    accepted_count INTEGER,
    rejected_count INTEGER,
    duplicate_count INTEGER,
    processing_failure_count INTEGER
);