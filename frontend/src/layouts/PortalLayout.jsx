import { Outlet } from 'react-router'
import Header from '../components/Header.jsx'
import NavBar from '../components/NavigationBar.jsx'
import Footer from '../components/Footer.jsx'

export default function PortalLayout() {
    return (
        <>
            <Header />
            <NavBar />
            <main><Outlet /></main>
            <Footer />
        </>
    )
}