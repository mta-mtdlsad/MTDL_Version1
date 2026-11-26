# MTDL-SAD Client Guide

## 1. Product Overview

**MTDL-SAD** is a multi-track situational awareness dashboard designed to receive, process, monitor and visualise track observations through a map-based operational picture.

The application provides the following capabilities:

- Current track monitoring and geographical visualisation.
- Track search, filtering and management.
- Historical track replay with adjustable playback speed.
- Platform-specific map symbols and identity-based colours.
- Track correlation and proximity information.
- Track validation and automated alert generation.
- Source health monitoring and processing statistics.
- Synthetic track simulation with configurable scenarios.
- UDP message reception and REST API integration.
- Persistent data storage using PostgreSQL.
- Platform-specific geographical validation.

### 1.1 Platform Symbols

| Platform Category | Operator Label | Symbol |
|---|---|---|
| Ship | Ship | Ship icon |
| Aircraft | Aircraft | Aircraft icon |
| Land vehicle | Land vehicle | Vehicle icon |
| Unknown or unsupported | Unknown platform | Question mark icon |

Platform symbols represent the declared platform category. Unrecognised platform types are displayed using the unknown platform symbol.

### 1.2 Identity Colours

Track identity is represented independently of platform category.

| Identity | Colour |
|---|---|
| FRIEND | Green |
| HOSTILE | Red |
| NEUTRAL | Blue |
| UNKNOWN | Amber |

Track status is represented separately as ACTIVE or INACTIVE.

---

## 2. Release Information

| Item | Description |
|---|---|
| Product | MTDL-SAD |
| Release Version | v3.0 |
| Deployment Method | Docker Compose |
| Application Type | Web-based application |
| Frontend | React and Vite |
| Backend | Spring Boot |
| Database | PostgreSQL |
| Communication | HTTP REST API and UDP |
| Map Provider | OpenStreetMap |
| Geographical Dataset | Natural Earth Land |
| Support | MTDL-SAD development and deployment team |

---

## 3. System Architecture

MTDL-SAD consists of three main application components.

| Component | Responsibility |
|---|---|
| Frontend | Displays dashboards, maps, track information, alerts and simulation controls. |
| Backend | Receives and processes messages, validates tracks, generates alerts and provides REST APIs. |
| Database | Stores track observations, alerts and source information. |

Incoming UDP messages are parsed and validated by the backend. Accepted observations are stored in PostgreSQL and made available to the frontend through REST APIs.

The application uses Docker to package and deploy its components.

### 3.1 Technology Stack

| Technology | Version / Description |
|---|---|
| Java | 21 |
| Spring Boot | 3.3.1 |
| Maven | 3.9.9 Docker build image |
| PostgreSQL | Relational database |
| React | Frontend framework |
| Vite | Frontend build tool |
| Node.js | 22 |
| Nginx | Frontend web server |
| Leaflet | Interactive map library |
| JTS Core | 1.20.0 |
| Docker Compose | Container orchestration |

---

## 4. System Requirements

### 4.1 Software Requirements

- Docker Engine or Docker Desktop.
- Docker Compose v2.
- A modern web browser.
- Access to the supplied deployment package.
- Internet connectivity for downloading required Docker images and accessing online map tiles.

Docker must be running before starting the application.

### 4.2 Recommended Hardware

The following values are initial deployment recommendations rather than formally established minimum requirements.

| Resource | Recommendation |
|---|---|
| Processor | 4 CPU cores |
| Memory | 8 GB RAM |
| Storage | 20 GB available disk space |
| Operating System | Windows, macOS or Linux with Docker support |

Storage requirements may increase depending on the volume of received track observations and database retention requirements.

### 4.3 Network Ports

| Component | Container Port | Protocol | Purpose |
|---|---|---|---|
| Frontend | 80 | TCP | Web application access |
| Backend | 8080 | TCP | REST API |
| UDP receiver | 5000 | UDP | Track message reception |
| PostgreSQL | 5432 | TCP | Database communication |

External host ports are determined by the supplied Docker Compose configuration.

The database and backend should not be exposed to external networks unless required by the approved deployment architecture.

---

## 5. Quick Start with Docker

### 5.1 Obtain the Application

Obtain the MTDL-SAD deployment package from the development or deployment team.

The package should contain the application source or deployment images, Docker configuration and required application resources.

### 5.2 Prepare the Configuration

Ensure the database connection settings and required network ports are correctly configured.

Database credentials must be supplied by the deployment administrator and must not be published in documentation.

### 5.3 Start the Application

Open a terminal in the directory containing the Docker Compose configuration.

Build and start the application:

```bash
docker compose up -d --build
```

The initial build may require internet access to download Docker images and application dependencies.

### 5.4 Verify Application Status

Check the status of the containers:

```bash
docker compose ps
```

The application services should be running before accessing the dashboard.

To inspect application logs:

```bash
docker compose logs --tail=100
```

### 5.5 Access the Application

Open a web browser and navigate to the frontend address configured in Docker Compose.

For a deployment publishing the frontend on host port 80:

```text
http://localhost
```

For a deployment publishing the frontend on host port 5173:

```text
http://localhost:5173
```

When accessing the application from another authorised computer, replace localhost with the deployment host's address and use the configured frontend port.

---

## 6. Main Screens

### 6.1 Operational Picture

The Operational Picture provides an overview of current track observations and their geographical positions.

The dashboard includes:

- Interactive geographical map.
- Current track information.
- Track search and filtering.
- Identity and status statistics.
- Track correlation information.
- Nearest-track information.
- Track management controls.

#### Viewing Current Tracks

1. Open the Operational Picture dashboard.
2. Locate the required track on the map or in the track table.
3. Select the track marker to display its information.
4. Review the track details in the map popup.

The track popup displays available information including:

- Track ID and track number.
- Source ID.
- Sequence number.
- Latitude and longitude.
- Speed.
- Platform type.
- Identity.
- Track status.

The map legend explains the platform symbols and identity colours.

### 6.2 Track Replay

The Track Replay dashboard allows operators to review historical movement for a selected track within a specified time range.

#### Loading Historical Track Data

1. Open the Track Replay dashboard.
2. Select a track number.
3. Enter the start date and time.
4. Enter the end date and time.
5. Select the playback speed.
6. Select **Load Replay**.
7. Select **Play** to begin playback.

#### Playback Controls

| Control | Function |
|---|---|
| Play | Starts or resumes playback. |
| Pause | Temporarily stops playback. |
| Reset | Returns to the first recorded position. |
| Progress slider | Allows manual navigation between recorded positions. |
| Half Speed | Displays observations at 0.5x playback speed. |
| Normal Speed | Displays observations at 1x playback speed. |
| Double Speed | Displays observations at 2x playback speed. |

The complete historical route is displayed using a dashed line. Travelled route segments are coloured according to the recorded track identity.

The map automatically follows the current replay position.

Replay displays stored observations in chronological order. Playback speed controls the interval between displayed observations rather than reproducing the exact original time intervals.

Historical data availability depends on the observations stored in the database.

### 6.3 Alerts and System Health

The Alerts and System Health dashboard provides information about message processing, source availability, track statistics and generated alerts.

The dashboard includes:

- Simulation controls.
- Alert summary.
- Track statistics.
- Overall processing analytics.
- Correlation information.
- Alert events.
- Source registry.

#### Alert Severity

| Severity | Description |
|---|---|
| HIGH | Significant validation issues, malformed messages or geographical platform violations. |
| MEDIUM | Invalid track attributes, including coordinate, sequence or speed values. |
| LOW | Lower-severity validation issues, duplicate messages and out-of-order observations. |

#### Source Health Status

| Status | Description |
|---|---|
| ACTIVE | Source is currently considered active. |
| STALE | Source has not provided a recent observation within its configured active period. (after 15 seconds)|
| DISCONNECTED | Source has exceeded its configured disconnection period. (after 60 seconds)|
| NOT SEEN | No observation has been recorded for the source. |

Source health thresholds are determined by the backend implementation.

#### Message Processing Outcomes

| Outcome | Description |
|---|---|
| ACCEPTED | Observation passed validation and was stored successfully. |
| REJECTED | Observation failed one or more validation checks. |
| DUPLICATE | Observation duplicates an existing source sequence. |
| FAILURE | An unexpected processing or persistence error occurred. |

Out-of-order observations may still be accepted and stored, with an associated warning alert.

---

## 7. Track Simulation

The built-in simulator generates synthetic track observations for demonstration and system evaluation.

### 7.1 Simulation Scenarios

| Scenario | Description |
|---|---|
| NORMAL | Generates active tracks with unknown, friendly and neutral identities. |
| HOSTILE | Generates active tracks with  unknown, friendly and hostile identities. |
| MIXED | Generates a combination of active and inactive tracks with unknown, friendly and neutral identities. |

Unknown identities may also appear during initial simulated observations.

### 7.2 Starting the Simulator

1. Open the Operational Picture dashboard.
2. Locate the simulation control panel.
3. Select the required simulation scenario.
4. Select **Start**.
5. Confirm that the simulation status changes to STARTED.
6. Observe the incoming tracks in the Operational Picture dashboard.

### 7.3 Stopping the Simulator

1. Return to the simulation control panel.
2. Select **Stop**.
3. Confirm that the simulation status changes to STOPPED.

Stopping the simulator does not automatically delete previously stored track observations.

### 7.4 Simulation Configuration

| Attribute | Value |
|---|---|
| Simulated sources | 5 |
| Tracks per source | 20 |
| Total generated paths | 100 |
| Waypoints per path | 12 |
| Nominal regular message rate | 4 messages per second |
| Supported simulated platforms | LAND, SEA and AIR |

Additional predefined messages are generated to demonstrate duplicate detection, out-of-order detection, invalid speed validation and correlation behaviour.

### 7.5 Geographical Movement

The simulator uses the Natural Earth geographical land dataset to generate platform-aware paths.

- Land platforms are restricted to land.
- Maritime platforms are restricted to water.
- Aircraft are unrestricted by the land mask.
- Generated paths are restricted to configured geographical areas.

Simulation paths are generated dynamically during application initialisation and may differ between restarts.

The simulator is intended for synthetic demonstrations and does not represent real operational platform movement.

---

## 8. Data and Integration

### 8.1 Integration Overview

| Integration | Format / Protocol | Direction |
|---|---|---|
| Track feed | UDP, pipe-delimited text | Inbound |
| REST API | HTTP, JSON | Request and response |
| Database | PostgreSQL | Read and write |
| Map tiles | HTTPS | Inbound |
| Geographical land mask | GeoJSON | Local application resource |

### 8.2 UDP Message Format

The application receives track observations using ten pipe-delimited fields.

```text
SourceID|SequenceNumber|TrackNumber|TrackType|Latitude|Longitude|Speed|Identity|Status|ObservationTime
```

| Field | Description |
|---|---|
| SourceID | Unique identifier of the transmitting source. |
| SequenceNumber | Sequence number of the observation. |
| TrackNumber | Identifier of the observed track. |
| TrackType | Declared platform category. |
| Latitude | Geographical latitude in decimal degrees. |
| Longitude | Geographical longitude in decimal degrees. |
| Speed | Reported track speed. |
| IdentityType | Declared track identity. |
| TrackStatus | ACTIVE or INACTIVE. |
| ObservationTime | Date and time of the observation. |

Observation timestamps use an ISO-style local date and time format.

```text
YYYY-MM-DDTHH:mm:ss
```

### 8.3 Platform Type Classification

The following values are recognised for geographical classification and map symbology.

| Platform | Recognised Values |
|---|---|
| Land | LAND, LAND_VEHICLE, GROUND_VEHICLE, VEHICLE |
| Maritime | SEA, SHIP, VESSEL, MARITIME, BOAT |
| Aircraft | AIR, AIRCRAFT, AERIAL, PLANE |
| Unknown | UNKNOWN or unrecognised values |

Unrecognised platform values are displayed using the unknown platform symbol and are not subject to land or water restrictions.

Empty UDP fields are rejected during message parsing.


### 8.4 Track Validation Rules

| Attribute | Validation Requirement | Severity |
|---|---|---|
| Source ID | 2-30 letters, numbers, underscores or hyphens | LOW |
| Sequence number | Greater than zero | MEDIUM |
| Track number | Minimum five characters | MEDIUM |
| Latitude | Finite value between -90 and +90 | MEDIUM |
| Longitude | Finite value between -180 and +180 | MEDIUM |
| Speed | Finite value greater than zero and not exceeding 2,000 | MEDIUM |
| Identity | FRIEND, HOSTILE, NEUTRAL or UNKNOWN | HIGH |
| Track status | ACTIVE or INACTIVE | HIGH |
| Observation time | Required | MEDIUM |
| Platform position | Must satisfy applicable geographical restrictions | HIGH |

Malformed messages generate HIGH-severity alerts.

Incoming track coordinates are not automatically modified. Observations that fail validation are rejected.

### 8.5 Coordinate Reference System

The application uses WGS 84 (EPSG:4326) geographical coordinates.

Map visualisation uses Web Mercator (EPSG:3857).

The bundled Natural Earth land mask supports geographical classification of land and maritime platforms.

### 8.6 REST API

The backend provides REST APIs for accessing application functionality.

The following endpoints are used by the supplied application components.

| Method | Endpoint | Description |
|---|---|---|
| GET | /api/tracks/current | Retrieves current track observations. |
| GET | /api/tracks/list | Retrieves available tracks. |
| GET | /api/tracks/history | Retrieves historical track observations. |
| GET | /api/simulator/state | Retrieves simulation status. |
| POST | /api/simulator/control | Starts or stops the simulator. |

The track history endpoint accepts start, end and trackNumber query parameters.

The simulation control endpoint accepts action and scenario query parameters.

Supported simulation actions are START and STOP.

### 8.7 Map Data Sources

OpenStreetMap provides the online map background.

https://www.openstreetmap.org/

Natural Earth provides the geographical land boundary dataset.

https://www.naturalearthdata.com/downloads/10m-physical-vectors/10m-land/

The Natural Earth dataset is bundled with the backend application. OpenStreetMap tiles require network connectivity.

---

## 9. Application Configuration

Application configuration is managed through the supplied deployment files and Spring Boot settings.

| Setting | Purpose | Configuration |
|---|---|---|
| SPRING_DATASOURCE_URL | Database connection address | PostgreSQL JDBC URL |
| SPRING_DATASOURCE_USERNAME | Database account | Administrator supplied |
| SPRING_DATASOURCE_PASSWORD | Database authentication | Administrator supplied |
| POSTGRES_DB | PostgreSQL database name | Deployment configuration |
| POSTGRES_USER | PostgreSQL account | Administrator supplied |
| POSTGRES_PASSWORD | PostgreSQL password | Administrator supplied |
| UDP listener | Receives incoming track observations | Backend network configuration |
| Frontend port | Provides browser access | Docker Compose port mapping |


Database credentials must remain consistent between PostgreSQL and the backend application configuration.

When the backend and database run in separate containers, the database connection must use the appropriate Docker service hostname rather than localhost.

Do not publish passwords or other confidential configuration values.

---

## 10. Common Administration Tasks

### 10.1 Start the Application

```bash
docker compose up -d
```

### 10.2 Stop the Application

```bash
docker compose stop
```

### 10.3 Restart the Application

```bash
docker compose restart
```

### 10.4 Remove Application Containers

```bash
docker compose down
```

This removes the application containers and associated Compose network without removing named volumes by default.

Do not use volume removal commands unless permanent data deletion is explicitly intended and authorised.

### 10.5 View Application Logs

```bash
docker compose logs --tail=100
```

To monitor logs continuously:

```bash
docker compose logs -f
```

### 10.6 Rebuild the Application

```bash
docker compose up -d --build
```

### 10.7 Update the Application

1. Obtain the approved application release.
2. Back up the existing database and deployment configuration.
3. Review database migration requirements.
4. Stop the current application.
5. Replace the application package.
6. Rebuild and start the containers.
7. Verify container status.
8. Confirm dashboard access and track reception.

### 10.8 Database Backup and Recovery

PostgreSQL backups should be managed by the deployment administrator using approved database backup procedures.

The administrator should:

- Maintain regular database backups.
- Store backups securely.
- Apply the organisation's data-retention policy.
- Verify database recovery procedures before production deployment.
- Back up the database before application upgrades.

---

## 11. Troubleshooting

| Symptom | Possible Cause | Recommended Action |
|---|---|---|
| Application page does not load | Docker services are stopped or the frontend port is incorrect. | Run docker compose ps and verify the published frontend port. |
| Backend repeatedly restarts | Database connection or configuration failure. | Review container logs and verify database connection settings. |
| Database authentication fails | Database credentials do not match the backend configuration. | Verify credentials with the deployment administrator. |
| Map background is missing | OpenStreetMap tiles cannot be accessed. | Check internet connectivity and network restrictions. |
| Tracks do not appear | No incoming observations, UDP connectivity issues or validation rejection. | Check source connectivity, simulation status and alerts. |
| Platform appears as unknown | Unsupported or unavailable platform type. | Review the declared track type in the source message. |
| Replay returns no results | No stored observations exist within the selected period. | Verify the track number and selected date range. |
| Simulation does not start | Simulator already running or initialisation failure. | Check simulation status and backend logs. |
| Simulation stops unexpectedly | Generated path exhaustion or processing failure. | Review backend logs and restart the simulator if appropriate. |
| Port is already in use | Another application is using a required host port. | Identify the conflicting service or update the authorised port mapping. |
| Track position is rejected | Declared platform type does not match its geographical position. | Verify the platform type and reported coordinates. |
| Duplicate alert appears | A source sequence has already been received. | Review the transmitting source's sequence management. |
| Out-of-order alert appears | An observation arrived with a sequence below the highest recorded sequence. | Review source transmission order. |

When reporting an issue, include the affected dashboard, approximate time, application version and sanitised error logs.

---

## 12. Known Limitations

- Online map backgrounds require access to OpenStreetMap tile servers.
- Offline map tile support is not currently implemented.
- The UDP parser supports ten-field messages only.
- Geographical validation uses a general-purpose Natural Earth land mask and is not intended for precision navigation.
- No explicit coastline safety buffer is implemented.
- Aircraft are not validated against authoritative airspace boundaries.
- Generated simulation paths may differ between application restarts.
- Continuous simulation is limited by the available generated waypoints.
- Reported simulated speeds are not currently differentiated by platform category.
- Historical replay depends on stored observations.
- Replay timing does not reproduce exact original observation intervals.
- Formal minimum hardware requirements and browser compatibility have not been established.

---

## 13. Security and Data Handling

MTDL-SAD should be deployed within an authorised network environment.

The deployment administrator is responsible for applying the organisation's security policies, including access control, network restrictions, database protection and data retention.

The following practices are recommended:

- Restrict application access to authorised users.
- Restrict UDP reception to authorised sources.
- Avoid exposing database ports to public networks.
- Protect database credentials and configuration files.
- Use approved network security controls.
- Use synthetic data for demonstrations.
- Avoid sharing real track information through unauthorised channels.
- Sanitise logs before sharing them for troubleshooting.
- Maintain secure database backups.

Authentication, role-based authorisation and encrypted external communication must not be assumed to be enabled without verification of the delivered deployment.

All operational data must be handled according to the client's security and classification requirements.

---

## 14. Support

Application support is provided through the designated MTDL-SAD development or deployment team.

When requesting support, provide:

- Application release version.
- Date and time of the issue.
- Affected dashboard or component.
- Description of the unexpected behaviour.
- Steps required to reproduce the issue.
- Sanitised application or Docker logs.

Do not include credentials, confidential geographical information or sensitive operational track data in support requests unless explicitly authorised.

---

## 15. External Resources

| Resource | URL |
|---|---|
| Docker Documentation | https://docs.docker.com/ |
| Docker Compose | https://docs.docker.com/compose/ |
| PostgreSQL | https://www.postgresql.org/docs/ |
| OpenStreetMap | https://www.openstreetmap.org/ |
| OpenStreetMap Licensing | https://www.openstreetmap.org/copyright |
| Natural Earth | https://www.naturalearthdata.com/ |
| Natural Earth Licensing | https://www.naturalearthdata.com/about/terms-of-use/ |
| Leaflet | https://leafletjs.com/ |

---

## 16. Change History

| Version | Description |
|---|---|
| v3.0 | Documentation of the Operational Picture, Track Replay, Alerts and System Health dashboards, platform symbology, geographical validation, track simulation and Docker deployment. |
