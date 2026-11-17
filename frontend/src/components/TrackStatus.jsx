import React from 'react';



function TrackStatus({ tracks = [] }) {

    const activeCount = tracks.filter(

        track => track.trackStatus === 'ACTIVE'

    ).length;



    const inactiveCount = tracks.filter(

        track => track.trackStatus === 'INACTIVE'

    ).length;



    return (

        <section className="status-panel">

            <h2>Track Status</h2>



            <div className="metric-row">

        <span className="track-status active">

          ACTIVE

        </span>

                <strong>{activeCount}</strong>

            </div>



            <div className="metric-row">

        <span className="track-status inactive">

          INACTIVE

        </span>

                <strong>{inactiveCount}</strong>

            </div>

        </section>

    );

}



export default TrackStatus;