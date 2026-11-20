import { useState, useRef } from "react";
import "../../styles/App.css";
import Card from "./Card.jsx";

export default function TrackCreate() {

    // Store form values and validation errors
    const [sourceId, setSourceId] = useState("");
    const [sequenceNumber, setSequenceNumber] = useState("");
    const [trackNumber, setTrackNumber] = useState("");
    const [trackType, setTrackType] = useState("");
    const [latitude, setLatitude] = useState("");
    const [longitude, setLongitude] = useState("");
    const [speed, setSpeed] = useState("");
    const [identityType, setIdentityType] = useState("");
    const [trackStatus, setTrackStatus] = useState("");

    const [errors, setErrors] = useState({});

    // Store API submission status
    const [isSuccess, setIsSuccess] = useState(false);
    const [apiError, setApiError] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Reference for first input
    const sourceIdRef = useRef(null);

    // Validate form values
    function validateErrors() {
        const newErrors = {};

        // VALIDATE SOURCE ID
        if (!sourceId.trim()) {
            newErrors.sourceId = "Please enter source ID";
        }

        // VALIDATE SEQUENCE NUMBER
        if (
            !sequenceNumber.trim() ||
            !Number.isInteger(Number(sequenceNumber)) ||
            Number(sequenceNumber) <= 0
        ) {
            newErrors.sequenceNumber =
                "Please enter a valid sequence number";
        }

        // VALIDATE TRACK NUMBER
        if (!trackNumber.trim() || trackNumber.trim().length < 5) {
            newErrors.trackNumber =
                "Track number must contain at least 5 characters";
        }

        // VALIDATE TRACK TYPE
        if (!trackType.trim()) {
            newErrors.trackType = "Please select a track type";
        }

        // VALIDATE LATITUDE
        if (
            latitude.trim() === "" ||
            !Number.isFinite(Number(latitude)) ||
            Number(latitude) < -90 ||
            Number(latitude) > 90
        ) {
            newErrors.latitude =
                "Enter a valid latitude (-90 to 90)";
        }

        // VALIDATE LONGITUDE
        if (
            longitude.trim() === "" ||
            !Number.isFinite(Number(longitude)) ||
            Number(longitude) < -180 ||
            Number(longitude) > 180
        ) {
            newErrors.longitude =
                "Enter a valid longitude (-180 to 180)";
        }

        // VALIDATE SPEED
        if (
            speed.trim() === "" ||
            !Number.isFinite(Number(speed)) ||
            Number(speed) < 0
        ) {
            newErrors.speed =
                "Enter a valid non-negative speed";
        }

        // VALIDATE IDENTITY TYPE
        if (!identityType.trim()) {
            newErrors.identityType =
                "Please select an identity type";
        }

        // VALIDATE TRACK STATUS
        if (!trackStatus.trim()) {
            newErrors.trackStatus =
                "Please select a track status";
        }

        return newErrors;
    }

    // Reset form after successful creation
    function resetForm() {
        setSourceId("");
        setSequenceNumber("");
        setTrackNumber("");
        setTrackType("");
        setLatitude("");
        setLongitude("");
        setSpeed("");
        setIdentityType("");
        setTrackStatus("");
        setErrors({});
    }

    // Handle form submission
    async function handleSubmit(event) {
        event.preventDefault();

        setIsSuccess(false);
        setApiError("");

        // Validate all form inputs
        const newErrors = validateErrors();
        setErrors(newErrors);

        if (Object.keys(newErrors).length > 0) {
            return;
        }

        // Create Track object matching Java Track fields
        const newTrack = {
            sourceId: sourceId.trim(),
            sequenceNumber: Number(sequenceNumber),
            trackNumber: trackNumber.trim(),
            trackType: trackType.trim(),
            latitude: Number(latitude),
            longitude: Number(longitude),
            speed: Number(speed),
            identityType: identityType.trim(),
            trackStatus: trackStatus.trim(),
            createdTime: new Date().toISOString().slice(0,19)
        };

        try {
            setIsSubmitting(true);

            // Send JSON to Spring Boot @RequestBody Track
            const response = await fetch("/api/tracks/create", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(newTrack)
            });

            // Read response from backend
            const message = await response.text();

            // Handle unsuccessful responses
            if (!response.ok) {
                throw new Error(
                    message || "Failed to create track"
                );
            }

            // Successful creation
            setIsSuccess(true);
            resetForm();
            sourceIdRef.current?.focus();

        } catch (error) {
            setApiError(
                error.message || "Failed to create track"
            );
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <Card title="Create Track">

            <form onSubmit={handleSubmit}>

                {/* SOURCE ID */}
                <label>
                    Source ID:&ensp;
                    <input
                        ref={sourceIdRef}
                        type="text"
                        value={sourceId}
                        onChange={(event) =>
                            setSourceId(event.target.value)
                        }
                        placeholder="e.g. SRC_01"
                    />
                </label>
                {errors.sourceId && (
                    <p className="error">{errors.sourceId}</p>
                )}

                {/* SEQUENCE NUMBER */}
                <label>
                    Sequence Number:&ensp;
                    <input
                        type="number"
                        min="0"
                        step="1"
                        value={sequenceNumber}
                        onChange={(event) =>
                            setSequenceNumber(event.target.value)
                        }
                        placeholder="e.g. 101"
                    />
                </label>
                {errors.sequenceNumber && (
                    <p className="error">
                        {errors.sequenceNumber}
                    </p>
                )}

                {/* TRACK NUMBER */}
                <label>
                    Track Number:&ensp;
                    <input
                        type="text"
                        value={trackNumber}
                        onChange={(event) =>
                            setTrackNumber(event.target.value)
                        }
                        placeholder="e.g. TRK00001"
                    />
                </label>
                {errors.trackNumber && (
                    <p className="error">{errors.trackNumber}</p>
                )}

                {/* TRACK TYPE - NEW FIELD */}
                <label>
                    Track Type:&ensp;
                    <select
                        value={trackType}
                        onChange={(event) =>
                            setTrackType(event.target.value)
                        }
                    >
                        <option value="">
                            -- Select Track Type --
                        </option>
                        <option value="AIR">AIR</option>
                        <option value="LAND">LAND</option>
                        <option value="SEA">SEA</option>
                    </select>
                </label>
                {errors.trackType && (
                    <p className="error">{errors.trackType}</p>
                )}

                {/* LATITUDE */}
                <label>
                    Latitude:&ensp;
                    <input
                        type="number"
                        step="any"
                        value={latitude}
                        onChange={(event) =>
                            setLatitude(event.target.value)
                        }
                        placeholder="e.g. 24.4539"
                    />
                </label>
                {errors.latitude && (
                    <p className="error">{errors.latitude}</p>
                )}

                {/* LONGITUDE */}
                <label>
                    Longitude:&ensp;
                    <input
                        type="number"
                        step="any"
                        value={longitude}
                        onChange={(event) =>
                            setLongitude(event.target.value)
                        }
                        placeholder="e.g. 54.3773"
                    />
                </label>
                {errors.longitude && (
                    <p className="error">{errors.longitude}</p>
                )}

                {/* SPEED */}
                <label>
                    Speed:&ensp;
                    <input
                        type="number"
                        min="0"
                        step="any"
                        value={speed}
                        onChange={(event) =>
                            setSpeed(event.target.value)
                        }
                        placeholder="e.g. 450.00"
                    />
                </label>
                {errors.speed && (
                    <p className="error">{errors.speed}</p>
                )}

                {/* IDENTITY TYPE */}
                <label>
                    Identity Type:&ensp;
                    <select
                        value={identityType}
                        onChange={(event) =>
                            setIdentityType(event.target.value)
                        }
                    >
                        <option value="">
                            -- Select Identity --
                        </option>
                        <option value="FRIEND">FRIEND</option>
                        <option value="NEUTRAL">NEUTRAL</option>
                        <option value="HOSTILE">HOSTILE</option>
                        <option value="UNKNOWN">UNKNOWN</option>
                    </select>
                </label>
                {errors.identityType && (
                    <p className="error">{errors.identityType}</p>
                )}

                {/* TRACK STATUS */}
                <label>
                    Track Status:&ensp;
                    <select
                        value={trackStatus}
                        onChange={(event) =>
                            setTrackStatus(event.target.value)
                        }
                    >
                        <option value="">
                            -- Select Status --
                        </option>
                        <option value="ACTIVE">ACTIVE</option>
                        <option value="INACTIVE">INACTIVE</option>
                    </select>
                </label>
                {errors.trackStatus && (
                    <p className="error">{errors.trackStatus}</p>
                )}

                {/* SUBMIT BUTTON */}
                <button
                    type="submit"
                    disabled={isSubmitting}
                >
                    {isSubmitting
                        ? "Creating..."
                        : "Submit Track Entry"}
                </button>

                {/* SUCCESS MESSAGE */}
                {isSuccess && (
                    <p className="success">
                        Track created successfully!
                    </p>
                )}

                {/* API ERROR MESSAGE */}
                {apiError && (
                    <p className="error">{apiError}</p>
                )}

            </form>
        </Card>
    );
}