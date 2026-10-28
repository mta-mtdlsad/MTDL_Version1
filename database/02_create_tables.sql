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