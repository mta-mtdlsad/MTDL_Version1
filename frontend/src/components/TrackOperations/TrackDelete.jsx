import Card from './Card.jsx'
import { useState } from "react";

export default function TrackDelete() {

    // Store track number & validation errors
    const [trackNumber, setTrackNumber] = useState("");
    const [error, setError] = useState("");

    // Store confirmation state
    const [showConfirmation, setShowConfirmation] = useState(false);
    // Store track number that user entered
    const [confirmedTrackNumber, setConfirmedTrackNumber] = useState("");

    // Store API status
    const [isDeleting, setIsDeleting] = useState(false);
    const [successMessage, setSuccessMessage] = useState("");
    const [apiError, setApiError] = useState("");

    // Validate track number
    function validateTrackNumber() {
        if (!trackNumber.trim()) {
            setError("Please enter a track number");
            return false;
        }
        if (trackNumber.trim().length < 5) {
            setError("Track number must contain at least 5 characters");
            return false;
        }
        setError("");
        return true;
    }

    // Handle delete button
    function handleSubmit(event) {
        event.preventDefault(); // Prevents the page from refreshing

        // Clears previous success and error messages
        setSuccessMessage("");
        setApiError("");

        // Validates track number
        if (!validateTrackNumber()) {
            return;
        }

        setConfirmedTrackNumber(trackNumber.trim()); // Save track number for confirmation
        setShowConfirmation(true); // Show confirmation message
    }

    // Confirm and delete tracks
    async function handleConfirmDelete()// This function runs when the user clicks Yes, Delete
    {
        try {
            setIsDeleting(true);
            setApiError("");
            setSuccessMessage("");

            // Sends a POST request
            const response = await fetch(`/api/tracks/delete?trackNumber=${encodeURIComponent(confirmedTrackNumber)}`, {method: "POST"});
            // encodeURIComponent() makes sure special characters in the track number are safely included in the URL
            const message = await response.text();

            // if returns 404, it displays a useful message -> [no matching records were found]
            if (response.status === 404) {
                setSuccessMessage(`No tracks found with track number ${confirmedTrackNumber}.`);
                setShowConfirmation(false);
                return;
            }

            // if the request fails -> throws an error
            if (!response.ok) {throw new Error(message || "Failed to delete tracks");}
            setSuccessMessage(message);

            // Reset form after successful deletion
            setTrackNumber("");
            setConfirmedTrackNumber("");
            setShowConfirmation(false);

        } catch (error) {
            setApiError(error.message || "Failed to delete tracks");
        } finally {
            setIsDeleting(false);
        }
    }

    // Cancel deletion
    function handleCancel() // This function runs when the user clicks Cancel
    {
        setShowConfirmation(false);
        setConfirmedTrackNumber("");
    }

    return (
        <Card title="Delete Track">

            <form onSubmit={handleSubmit}>
                <label>Track Number:&ensp;
                    <input
                        type="text"
                        value={trackNumber}
                        onChange={(event) => {setTrackNumber(event.target.value);setError("");}}
                        placeholder="e.g.TRK00001"
                    >
                    </input>
                </label>
                {error && <p className="error">{error}</p>}

                <button type="submit">Delete</button>
            </form>
            <br/><br/>
            {/* Confirmation section */}
            {showConfirmation && (
                <Card title="Confirm Deletion">
                    <p>Are you sure you want to delete all records matching track number: <strong>{confirmedTrackNumber}</strong></p>
                    <p className="warning"><strong>Warning: </strong> This action will permanently delete ALL database rows matching this track number.</p>
                    <button type="button" onClick={handleConfirmDelete} disabled={isDeleting}>{isDeleting ? "Deleting..." : "Yes, Delete"}</button>
                    &ensp;&ensp;
                    <button type="button" onClick={handleCancel} disabled={isDeleting}>Cancel</button>
                </Card>
            )}

            {/* Success message */}
            {successMessage && <p className="success">{successMessage}</p>}

            {/* API error message */}
            {apiError && <p className="error">{apiError}</p>}
        </Card>
    );
}