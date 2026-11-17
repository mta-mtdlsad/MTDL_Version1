import { NavLink } from 'react-router'

export default function NavBar() {
  return (
    <aside>
      <div>
        <span>MyApp</span>
      </div>
      <nav aria-label="Main navigation">
        <ul>
          <li><NavLink to="/" end>Operational Picture</NavLink></li>
          <li><NavLink to="/track-operations">Track Operations</NavLink></li>
          <li><NavLink to="/alerts-and-source-health">Alerts and Source Health</NavLink></li>
        </ul>
      </nav>
    </aside>
  )
}
