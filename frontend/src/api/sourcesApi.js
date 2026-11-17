export async function fetchAllSources() {
    const response = await fetch('/api/sources/list');
    if (!response.ok) {
        throw new Error(`Failed to load UDP sources (${response.status})`);
    }
    const data = await response.json();
    return Array.isArray(data) ? data : [];
}
