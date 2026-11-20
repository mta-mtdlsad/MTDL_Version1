export async function fetchAllSources() {
    const response = await fetch('/api/sources/list');
    if (!response.ok) {
        throw new Error(`Failed to fetch sources: ${response.status}`);
    }
    return response.json();
}