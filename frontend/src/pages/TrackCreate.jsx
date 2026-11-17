import { useState, useRef } from "react";
import "../App.css";
import Card from '../components/Card.jsx'


export default function TrackCreate() {

    // Store form values & validation errors
    const [sourceId, setSourceId] = useState("");
    const [sequenceNumber, setSequenceNumber] = useState("");
    const [trackNumber, setTrackNumber] = useState("");
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

        //VALIDATE SOURCE ID
        if (!sourceId.trim()) {
            newErrors.sourceId = "Please enter source ID";
        }
        //VALIDATE SEQUENCE NUMBER
        if (!sequenceNumber.trim() ||
            !Number.isInteger(Number(sequenceNumber)) ||
            Number(sequenceNumber) < 0) {
            newErrors.sequenceNumber =
                "Please enter a valid sequence number";
        }
        //VALIDATE TRACK NUMBER
        if (!trackNumber.trim() || trackNumber.trim().length < 5) {
            newErrors.trackNumber =
                "Track number must contain at least 5 characters";
        }

        //VALIDATE LATITUDE
        if (latitude.trim() === "" ||
            !Number.isFinite(Number(latitude)) ||
            Number(latitude) < -90 ||
            Number(latitude) > 90) {
            newErrors.latitude = "Enter a valid latitude (-90 to 90)";
        }
        //VALIDATE LONGITUDE
        if (longitude.trim() === "" ||
            !Number.isFinite(Number(longitude)) ||
            Number(longitude) < -180 ||
            Number(longitude) > 180) {
            newErrors.longitude = "Enter a valid longitude (-180 to 180)";
        }
        //VALIDATE SPEED
        if (speed.trim() === "" ||
            !Number.isFinite(Number(speed)) ||
            Number(speed) < 0) {
            newErrors.speed = "Enter a valid non-negative speed";
        }
        //VALIDATE IDENTITY TYPE
        if (!identityType.trim()) {
            newErrors.identityType = "Please enter identity type";
        }
        //VALIDATE TRACK STATUS
        if (!trackStatus.trim()) {
            newErrors.trackStatus = "Please enter track status";
        }
        return newErrors;
    }

    // Reset form after successful creation
    function resetForm() {
        setSourceId("");
        setSequenceNumber("");
        setTrackNumber("");
        setLatitude("");
        setLongitude("");
        setSpeed("");
        setIdentityType("");
        setTrackStatus("");
        setErrors({});
    }

    // Handle form submission
    async function handleSubmit(event) {
        event.preventDefault(); //prevents the browser from refreshing the page

        //validates the form
        setIsSuccess(false);
        setApiError("");

        const newErrors = validateErrors();
        setErrors(newErrors);

        if (Object.keys(newErrors).length > 0) {
            return;
        }

        //Collects values into one object
        const newTrack = {
            sourceId: sourceId.trim(),
            sequenceNumber: Number(sequenceNumber),
            trackNumber: trackNumber.trim(),
            latitude: Number(latitude),
            longitude: Number(longitude),
            speed: Number(speed),
            category: identityType.trim(),
            trackStatus: trackStatus.trim()
        };

        try {
            //Converts object into formData to allow Spring Boot receive through @RequestParam
            setIsSubmitting(true);
            const formData = new URLSearchParams();
            Object.entries(newTrack).forEach(([key, value]) => {formData.append(key, value);});

            //Sends POST request to /api/tracks/create
            const response = await fetch("/api/tracks/create", {
                method: "POST",
                headers: {"Content-Type": "application/x-www-form-urlencoded"},
                body: formData.toString()
            });

            //checks API request
            const message = await response.text();
            if (!response.ok) {
                throw new Error(message || "Failed to create track");
            }

            //if the request succeeds:
            setIsSuccess(true); //Displays success
            resetForm(); //Clears the form
            sourceIdRef.current?.focus(); //Focuses on Source ID

        } catch (error) { //displays an error
            setApiError(error.message || "Failed to create track");
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <Card title="Create Track">

            <form onSubmit={handleSubmit}>
                <label>Source ID:&ensp;
                    <input
                        ref={sourceIdRef}
                        type="text"
                        value={sourceId}
                        onChange={(event) => setSourceId(event.target.value)}
                        placeholder="e.g.SRC_01"
                    >
                    </input>
                </label>
                {errors.sourceId && <p className="error">{errors.sourceId}</p>}

                <label>Sequence Number:&ensp;
                    <input
                        type="number"
                        min="0"
                        step="1"
                        value={sequenceNumber}
                        onChange={(event) => setSequenceNumber(event.target.value)}
                        placeholder="e.g.101"
                    >
                    </input>
                </label>
                {errors.sequenceNumber && <p className="error">{errors.sequenceNumber}</p>}

                <label>Track Number:&ensp;
                    <input
                        type="text"
                        value={trackNumber}
                        onChange={(event) => setTrackNumber(event.target.value)}
                        placeholder="e.g.TRK10001"
                    >
                    </input>
                </label>
                {errors.trackNumber && <p className="error">{errors.trackNumber}</p>}

                <label>Latitude:&ensp;
                    <input
                        type="number"
                        step="any"
                        value={latitude}
                        onChange={(event) => setLatitude(event.target.value)}
                        placeholder="e.g.34.052234"
                    >
                    </input>
                </label>
                {errors.latitude && <p className="error">{errors.latitude}</p>}

                <label>Longitude:&ensp;
                    <input
                        type="number"
                        step="any"
                        value={longitude}
                        onChange={(event) => setLongitude(event.target.value)}
                        placeholder="e.g.-118.243684"
                    >
                    </input>
                </label>
                {errors.longitude && <p className="error">{errors.longitude}</p>}

                <label>Speed:&ensp;
                    <input
                        type="number"
                        min="0"
                        step="any"
                        value={speed}
                        onChange={(event) => setSpeed(event.target.value)}
                        placeholder="e.g.450.00"
                    >
                    </input>
                </label>
                {errors.speed && <p className="error">{errors.speed}</p>}

                <label>Identity Type:&ensp;
                    <select
                        value={identityType}
                        onChange={(event) => setIdentityType(event.target.value)}>
                        <option value="">-- Select Identity --</option>
                        <option value="FRIEND">FRIEND</option>
                        <option value="NEUTRAL">NEUTRAL</option>
                        <option value="HOSTILE">HOSTILE</option>
                        <option value="UNKNOWN">UNKNOWN</option>
                    </select>
                    {errors.identityType && <p className="error">{errors.identityType}</p>}
                </label>

                <label>Track Status:&ensp;
                    <select
                        value={trackStatus}
                        onChange={(event) => setTrackStatus(event.target.value)}>
                        <option value="">-- Select Status --</option>
                        <option value="Active">Active</option>
                        <option value="Inactive">Inactive</option>
                    </select>
                    {errors.trackStatus && <p className="error">{errors.trackStatus}</p>}
                </label>

                <button type="submit" disabled={isSubmitting}> {isSubmitting ? "Creating..." : "Submit Track Entry"} </button>

                {/* Success message */}
                {isSuccess && <p className="success"> Track created successfully! </p>}

                {/* API error message */}
                {apiError && <p className="error"> {apiError} </p>}

            </form>
        </Card>
    );
}