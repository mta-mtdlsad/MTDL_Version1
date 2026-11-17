import { NavLink } from 'react-router'

export default function NavBar() {
    return (
        <nav aria-label="Main navigation">
            <NavLink to="/api/tracks/" end>Home</NavLink>
            <NavLink to="/api/tracks/list">List</NavLink>
            <NavLink to="/api/tracks/search">Search</NavLink>
            <NavLink to="/api/tracks/filter">Filter</NavLink>
            <NavLink to="/api/tracks/create">Create</NavLink>
            <NavLink to="/api/tracks/delete">Delete</NavLink>
            <NavLink to="/api/tracks/history">History</NavLink>
        </nav>
    )
}
