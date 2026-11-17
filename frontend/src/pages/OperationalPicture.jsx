import React, { useEffect, useState } from 'react';



import CurrentMap from '../components/CurrentMap';

import CurrentTracks from '../components/CurrentTracks';

import Correlation from '../components/Correlation';

import NearestTracks from '../components/NearestTracks';

import IdentityStatistics from '../components/IdentityStatistics';

import TrackStatus from '../components/TrackStatus';



import '../App.css';



function OperationalPicture() {

    const [tracks, setTracks] = useState([]);

    const [error, setError] = useState('');



    async function fetchTracks() {

        try {

            const response = await fetch('/api/tracks/current');



            if (!response.ok) {

                throw new Error('Failed to load tracks');

            }



            const data = await response.json();

            setTracks(data);

            setError('');

        } catch (err) {

            setError(err.message);

        }

    }



    useEffect(() => {

        let mounted = true;



        async function loadTracks() {

            try {

                const response = await fetch('/api/tracks/current');



                if (!response.ok) {

                    throw new Error('Failed to load tracks');

                }



                const data = await response.json();



                if (mounted) {

                    setTracks(data);

                    setError('');

                }

            } catch (err) {

                if (mounted) setError(err.message);

            }

        }



        loadTracks();



        const interval = setInterval(loadTracks, 5000);



        return () => {

            mounted = false;

            clearInterval(interval);

        };

    }, []);



    return (

        <div className="app-container">

            <main className="app-content">



                <div className="app-content-header">

                    <h1 className="app-content-headerText">

                        Operational Picture

                    </h1>



                    <button

                        className="app-content-headerButton"

                        onClick={fetchTracks}

                    >

                        Refresh

                    </button>

                </div>



                {error && (

                    <p className="error-message">{error}</p>

                )}



                <div className="dashboard-grid">



                    {/* LEFT COLUMN */}

                    <div className="dashboard-left">



                        <section className="map-section">

                            <div className="map-header">

                                <h2>Current Track Locations</h2>

                                <span className="live-indicator">

                  ● Live Updates

                </span>

                            </div>



                            <CurrentMap tracks={tracks} />

                        </section>



                        <section className="correlation-section">

                            <Correlation />

                        </section>



                    </div>



                    {/* RIGHT COLUMN */}

                    <div className="dashboard-right">



                        <IdentityStatistics tracks={tracks} />



                        <TrackStatus tracks={tracks} />



                        <div className="nearest-section">

                            <NearestTracks />

                        </div>



                    </div>



                </div>



                {/* FULL WIDTH TRACK TABLE */}

                <section className="table-section">

                    <CurrentTracks filteredTracks={tracks} />

                </section>



            </main>

        </div>

    );

}



export default OperationalPicture;