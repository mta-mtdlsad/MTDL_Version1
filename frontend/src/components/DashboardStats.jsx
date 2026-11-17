//
// import '../App.css'
// import { useEffect, useState } from 'react'
//
// export default function DashboardStats() {
//
//     const [tracks, setTracks] = useState([])
//     const [loading, setLoading] = useState(true)
//     const [error, setError] = useState(null)
//
//     useEffect(() => {
//
//         const fetchTracks = async () => {
//
//             try {
//                 const response = await fetch('/api/tracks/list')
//
//                 if (!response.ok) {
//                     throw new Error(`HTTP Error: ${response.status}`)
//                 }
//
//                 const data = await response.json()
//                 setTracks(data)
//
//             } catch (error) {
//                 setError(error.message)
//
//             } finally {
//                 setLoading(false)
//             }
//         }
//
//         fetchTracks()
//
//     }, [])
//
//     const totalTracks = tracks.length
//
//     const activeTracks = tracks.filter(
//         track => track.trackStatus?.toUpperCase() === 'ACTIVE'
//     ).length
//
//     const inactiveTracks = tracks.filter(
//         track => track.trackStatus?.toUpperCase() === 'INACTIVE'
//     ).length
//
//     const unknownTracks = tracks.filter(
//         track => track.identityType?.toUpperCase() === 'UNKNOWN'
//     ).length
//
//     const stats = [
//         {
//             title: 'Total Tracks',
//             value: totalTracks,
//             color: '#2563eb'
//         },
//         {
//             title: 'Active Tracks',
//             value: activeTracks,
//             color: '#16a34a'
//         },
//         {
//             title: 'Inactive Tracks',
//             value: inactiveTracks,
//             color: '#d90606'
//         },
//         {
//             title: 'Unknown Identity',
//             value: unknownTracks,
//             color: '#9333ea'
//         }
//     ]
//
//     if (loading) {
//         return <p>Loading dashboard statistics...</p>
//     }
//
//     if (error) {
//         return <p className="error">Unable to load statistics: {error}</p>
//     }
//
//     return (
//         <div className="stats-grid">
//
//             {stats.map((stat, index) => (
//
//                 <div
//                     className="stat-card"
//                     key={index}
//                     style={{ borderLeft: `5px solid ${stat.color}` }}
//                 >
//
//                     <div className="stat-info">
//                         <h3>{stat.title}</h3>
//                         <p>{stat.value}</p>
//                     </div>
//
//                 </div>
//
//             ))}
//
//         </div>
//     )
// }



import '../App.css';
import { useEffect, useState } from 'react';

export default function DashboardStats() {

    const [tracks, setTracks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {

        const fetchTracks = async () => {
            try {
                const response = await fetch('/api/tracks/list');

                if (!response.ok) {
                    throw new Error(`HTTP Error: ${response.status}`);
                }

                const data = await response.json();
                setTracks(data);

            } catch (error) {
                setError(error.message);

            } finally {
                setLoading(false);
            }
        };

        fetchTracks();

    }, []);

    // TRACK STATISTICS
    const totalTracks = tracks.length;

    const activeTracks = tracks.filter(
        track => track.trackStatus?.toUpperCase() === 'ACTIVE'
    ).length;

    const inactiveTracks = tracks.filter(
        track => track.trackStatus?.toUpperCase() === 'INACTIVE'
    ).length;

    const unknownTracks = tracks.filter(
        track => track.identityType?.toUpperCase() === 'UNKNOWN'
    ).length;

    // STATISTICS DATA
    const stats = [
        {
            id: 'total',
            title: 'Total Tracks',
            value: totalTracks,
            color: '#3b82f6'
        },
        {
            id: 'active',
            title: 'Active Tracks',
            value: activeTracks,
            color: '#22c55e'
        },
        {
            id: 'unknown',
            title: 'Unknown Identity',
            value: unknownTracks,
            color: '#a855f7'
        },
        {
            id: 'inactive',
            title: 'Inactive Tracks',
            value: inactiveTracks,
            color: '#ef4444'
        }
    ];

    if (loading) {
        return (
            <div className="stats-message">
                Loading statistics...
            </div>
        );
    }

    if (error) {
        return (
            <div className="stats-message stats-error">
                Unable to load statistics: {error}
            </div>
        );
    }

    return (
        <div className="stats-grid">

            {stats.map((stat) => (

                <div
                    className="stat-card"
                    key={stat.id}
                    style={{
                        '--stat-color': stat.color
                    }}
                >

                    <div className="stat-info">

                        <h3>{stat.title}</h3>

                        <p>{stat.value}</p>

                    </div>

                </div>

            ))}

        </div>
    );
}
