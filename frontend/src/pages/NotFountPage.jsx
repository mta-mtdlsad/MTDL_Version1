import { Link } from 'react-router'
import Card from '../components/Card.jsx'

export default function NotFoundPage() {
    return (
        <Card title="404 — Page Not Found">
            <p>The page you are looking for is not found.</p>
            <Link to="/">Return home</Link>
        </Card>
    )
}