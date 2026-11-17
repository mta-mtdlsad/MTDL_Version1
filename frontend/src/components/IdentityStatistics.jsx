import React from 'react';

import { identityColors } from './CurrentMap';



function IdentityStatistics({ tracks = [] }) {

    const identities = [

        'FRIEND',

        'HOSTILE',

        'NEUTRAL',

        'UNKNOWN'

    ];



    return (

        <section className="dashboard-panel">

            <h2>Identity Types</h2>



            {identities.map(identity => {

                const count = tracks.filter(

                    track => track.identityType === identity

                ).length;



                return (

                    <div className="metric-row" key={identity}>

                        <div className="metric-label">

              <span

                  className="metric-dot"

                  style={{

                      backgroundColor:

                          identityColors[identity]

                  }}

              />

                            <span>{identity}</span>

                        </div>



                        <strong>{count}</strong>

                    </div>

                );

            })}

        </section>

    );

}



export default IdentityStatistics;
