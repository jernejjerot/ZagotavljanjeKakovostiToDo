export async function geocodeAddress(address) {
    const response = await fetch(`https://maps.googleapis.com/maps/api/geocode/json?address=${encodeURIComponent(address)}&key=YOUR_API_KEY`);
    const data = await response.json();
    if (data.results.length > 0) {
        return {
            latitude: data.results[0].geometry.location.lat,
            longitude: data.results[0].geometry.location.lng,
        };
    }
    return null;
}
