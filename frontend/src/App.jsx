import { useState } from 'react'
import './App.css'
import TrackDashboard from "./pages/TrackDashboard.jsx";

function App() {
  const [count, setCount] = useState(0)

  return (
    <TrackDashboard/>
  )
}

export default App
