import { Outlet } from 'react-router'
import Header from '../components/Header.jsx'
import NavBar from '../components/NavBar.jsx'
import Footer from '../components/Footer.jsx'

export default function DefaultLayout() {
    return (
        <>
            <Header />
            <div className="app-layout">
                <NavBar />
                <main><Outlet /></main>
            </div>
            <Footer />
        </>
    )
}