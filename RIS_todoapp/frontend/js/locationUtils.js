// locationUtils.js
export async function geocodeAddress(address) {
    const url = `https://maps.googleapis.com/maps/api/geocode/json?address=${encodeURIComponent(address)}&key=YOUR_API_KEY`;
    const res = await fetch(url);
    const data = await res.json();
    if (data.results?.length) {
      return {
        latitude: data.results[0].geometry.location.lat,
        longitude: data.results[0].geometry.location.lng,
      };
    }
    return null;
  }